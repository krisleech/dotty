# Dotty

Dotty Game Server and UI prototype.

Allow multiple players to play the same game displayed on a large screen. This
could be a smart TV web browser or a regular PC connected to a large monitor.

Players use their mobile phones as an interface (movement controls, score etc.).

Visit `localhost:3000` using the smart TV browser and click "display".

Visit `localhost:3000` on any number of mobile phones and click "join".

The mobile will have controls to move their player on the screen.

The game is [tag](https://en.wikipedia.org/wiki/Tag_(game)).

There is a two-way connection between the server and clients using web sockets.

The idea is to integrate [phaser.js](https://github.com/photonstorm/phaser)
next.

## Installation

* Install Java
* Install [Leiningen](https://github.com/technomancy/leiningen/wiki/Packaging)
* Clone the repository

## Development

```
lein run
```

and visit http://localhost:3000

### Using Docker

```
docker build -t dotty .
docker run -p 3000:3000 dotty
```

### Using Docker Compose

```
docker-compose build
docker-compose up
```

## Deployment

TODO
