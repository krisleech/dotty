# dotty

Dotty Game Server and UI.

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
