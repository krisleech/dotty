$(function(){
    $('#up').click(function(e) {
        e.preventDefault();
        sendEvent({ "type": "move", "direction": "up" });
    })

    $('#down').click(function(e) {
        e.preventDefault();
        sendEvent({ "type": "move", "direction": "down" });
    })

    $('#left').click(function(e) {
        e.preventDefault();
        sendEvent({ "type": "move", "direction": "left" });
    })

    $('#right').click(function(e) {
        e.preventDefault();
        sendEvent({ "type": "move", "direction": "right" });
    })
})

var playerId = window.localStorage.getItem('playerId');

console.log("playerId", playerId);

var socket = new WebSocket('ws://' + window.location.host + '/ws/player');

// socket.addEventListener('open', function(event) {
//     console.log('connection opened', event);
// });

socket.onopen = function() { joinGame(); }

var joinGame = function() {
    if(playerId == null) {
        sendEvent({ "type": "new-player" });
    }
    else
    {
        sendEvent({ "type": "returning-player" })
    }
}

socket.onmessage = function(message) {
    event = JSON.parse(message.data);
    console.log('event received', event)

    switch(event.type) {
        case "id-created":
            playerId = event.id;
            window.localStorage.setItem('playerId', playerId);
            // or $.cookies.set(key, value); (to support older clients)
            console.log('playerId set', playerId)
            break;
        default:
            console.log("Unknown event type", event.type)
    }


};

// send message to server
var sendEvent  = function(event) {
    if (socket.readyState === WebSocket.OPEN) {
        event['player-id'] = playerId;
        console.log('event sent', event);
        json = JSON.stringify(event);
        socket.send(json);
    }
};
