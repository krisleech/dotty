$(function(){
    $('#host').html(window.location.host);
})


var players = [];

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

var findPlayer = function(playerId) {
    return players.find(function(player) { return player.id === playerId });
}

handleMovePlayerEvent = function(event) {
    var player = findPlayer(event["player-id"]);

    // TODO: animate player

    switch(event.direction) {
        case "up":
            player.direction = 'up';
            break;
        case "down":
            player.direction = 'down';
            break;
        case "left":
            player.direction = 'left';
            break;
        case "right":
            player.direction = 'right';
            break;
        default:
            console.log("unknown direction", event.direction);
    }
}

var createPlayer = function(attributes) {
    playerSprite = $("<div id='" + attributes.id + "' " + "class='player'><i class='fas fa-2x fa-bug'></i></div>")
    playerSprite.css({"top": attributes.x + "px", "left": attributes.y + "px"});
    return { id: attributes.id, x: attributes.x, y: attributes.y, sprite: playerSprite, direction: 'stopped' };
}

socket.onmessage = function(raw_event) {
    event = JSON.parse(raw_event.data);
    console.log('got event', event)
    switch(event.type) {
        case "new-player":
            player = createPlayer(event.player);
            players.push(player);
            $('#canvas').append(player.sprite)
            console.log('New Player', player)
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

// speed of players
var pixelsToMovePerTick = 5;

// update state
function update(progress) {
    players.forEach(function(player) {
        switch(player.direction) {
            case "stopped":
                break;
            case "up":
                // needs to go in a player object
                nowTop = parseInt(player.sprite.css('top'), 10);
                if(nowTop < 0) { player.direction = 'stopped'; break; }
                newTop = nowTop - pixelsToMovePerTick;
                player.sprite.css('top', newTop + 'px');
                break;
            case "down":
                // needs to go in a player object
                nowTop = parseInt(player.sprite.css('top'), 10);
                if(nowTop > 1000) {  player.direction = 'stopped'; break; }
                newTop = nowTop + pixelsToMovePerTick;
                player.sprite.css('top', newTop + 'px');
                break;
            case "left":
                // needs to go in a player object
                nowLeft = parseInt(player.sprite.css('left'), 10);
                if(nowLeft < 0) {  player.direction = 'stopped'; break; }
                newLeft = nowLeft - pixelsToMovePerTick;
                player.sprite.css('left', newLeft + 'px');
                break;
            case "right":
                // needs to go in a player object
                nowLeft = parseInt(player.sprite.css('left'), 10);
                if(nowLeft > 1000) { player.direction = 'stopped'; break; }
                newLeft = nowLeft + pixelsToMovePerTick;
                player.sprite.css('left', newLeft + 'px');
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


