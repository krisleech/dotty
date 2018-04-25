FROM clojure:lein

MAINTAINER Kris Leech <kris.leech@gmail.com>
RUN mkdir -p /var/app
WORKDIR /var/app
ADD . /var/app
EXPOSE 3000
CMD ["lein", "run"]
