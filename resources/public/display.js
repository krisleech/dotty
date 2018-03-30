$(function(){
    $('#host').html(window.location.host);
})

// var lastEventSent
var socket = new WebSocket('ws://' + window.location.host + '/ws/display');

socket.onerror = function(error) {
    console.log('WebSocket Error: ' + error);
};

// socket.on('close', function(reasonCode, description) {
//   console.log('Websocket Closed: ', reasonCode, description);
// });

socket.addEventListener('open', function(event) {
    console.log('got open', event);
});

findPlayer = function(playerId) {
    return $('#' + playerId);
}

handleMovePlayerEvent = function(event) {
    var player = findPlayer(event["player-id"]);

    // TODO: animate player

    switch(event.direction) {
        case "up":
            player.data('direction', 'up')
            break;
        case "down":
            player.data('direction', 'down')
            break;
        case "left":
            player.data('direction', 'left')
            break;
        case "right":
            player.data('direction', 'right')
            break;
        default:
            console.log("unknown direction", event.direction);
    }
}


socket.onmessage = function(raw_event) {
    event = JSON.parse(raw_event.data);
    console.log('got event', event)
    switch(event.type) {
        case "new-player":
            playerId = event.player.id;
            console.log('New Player', playerId)
            // move below to functions, or maybe object.
            $('#canvas').append("<div id='" + playerId + "' " + "class='player'><i class='fas fa-2x fa-bug'></i></div>")
            $('#' + playerId).css({"top": event.player.x + "px", "left": event.player.y + "px"});
            break;
        case "move":
            handleMovePlayerEvent(event);
            break;
        default:
            console.log("Unknown event type", event.type)
    }

};

// send message to server
var socketPush  = function(message) {
    if (socket.readyState === WebSocket.OPEN) {
        console.log('sending msg', message);
        socket.send(JSON.stringify(message));
    }
};

var sendPing = function() {
    socketPush({ "type": "ping" })
}

setInterval(sendPing, 10000);

var pixelsToMovePerTick = 10;

// update state
function update(progress) {
    $('.player').each(function(i, player) {
        player = $(player);
        direction = player.data("direction");
        console.log('moving', player.attr('id'), direction)
        switch(direction) {
            case "up":
                // needs to go in a player object
                nowTop = parseInt(player.css('top'), 10);
                if(nowTop < 0) {  break; }
                newTop = nowTop - pixelsToMovePerTick;
                player.css('top', newTop + 'px');
                break;
            case "down":
                // needs to go in a player object
                nowTop = parseInt(player.css('top'), 10);
                if(nowTop > 1000) {  break; }
                newTop = nowTop + pixelsToMovePerTick;
                player.css('top', newTop + 'px');
                break;
            case "left":
                // needs to go in a player object
                nowLeft = parseInt(player.css('left'), 10);
                if(nowLeft < 0) {  break; }
                newLeft = nowLeft - pixelsToMovePerTick;
                player.css('left', newLeft + 'px');
                break;
            case "right":
                // needs to go in a player object
                nowLeft = parseInt(player.css('left'), 10);
                if(nowLeft > 1000) { break; }
                newLeft = nowLeft + pixelsToMovePerTick;
                player.css('left', newLeft + 'px');
                break;
        }
    }); // player each


}

function draw() {
    // Draw the state of the world
}

function loop(timestamp) {
    var progress = timestamp - lastRender

    update(progress)
    draw()

    lastRender = timestamp
    window.requestAnimationFrame(loop)
}

var lastRender = 0
window.requestAnimationFrame(loop)


