FROM  frolvlad/alpine-scala:2.11

#FROM docker pull hseeberger/scala-sbt:latest
# ENV SCALA_VERSION 2.12.1
# ENV SBT_VERSION 0.13.13

EXPOSE 8080

RUN mkdir /code
WORKDIR /code

RUN apk update && apk add postgresql git

ADD . /code/

RUN ./sbt clean update

CMD ./run