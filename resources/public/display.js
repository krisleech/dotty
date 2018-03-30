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

// speed of players
var pixelsToMovePerTick = 5;

var createPlayer = function(attributes) {
    playerSprite = $("<div id='" + attributes.id + "' " + "class='player'><i class='fas fa-2x fa-bug'></i></div>")
    playerSprite.css({"top": attributes.x + "px", "left": attributes.y + "px"});
    player = { id: attributes.id, x: attributes.x, y: attributes.y, sprite: playerSprite, direction: 'stopped' };

    player.render = function() {
        this.sprite.css({ 'top': this.x + 'px', 'left': this.y + 'px' });
    }

    player.move = function() {
        switch(this.direction) {
            case "stopped":
                break;
            case "up":
                if(this.x < 0) { this.direction = 'stopped'; break; }
                this.x = this.x - pixelsToMovePerTick;
                break;
            case "down":
                if(this.x > 1000) { this.direction = 'stopped'; break; }
                this.x = this.x + pixelsToMovePerTick;
                break;
            case "left":
                if(this.y < 0) {  this.direction = 'stopped'; break; }
                this.y = this.y - pixelsToMovePerTick;
                break;
            case "right":
                if(this.y > 1000) { this.direction = 'stopped'; break; }
                this.y = this.y + pixelsToMovePerTick;
                break;
        }
    }
    return player;
}

socket.onmessage = function(raw_event) {
    event = JSON.parse(raw_event.data);
    console.log('got event', event)
    switch(event.type) {
        case "new-player":
            player = createPlayer(event.player);
            players.push(player);
            player.sprite.hide();
            $('#canvas').append(player.sprite)
            player.sprite.fadeIn();
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

// update state
function update(progress) {
    players.forEach(function(player) {
        player.move();
        player.render();
    });
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


