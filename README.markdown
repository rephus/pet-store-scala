### Scala REST template

Scala REST skeleton service that features:

* Akka 2.3.9
* Spray 1.3
* Spray-json 1.3
* Slick 2.1 (using postgresql connector)

### Run server (with local dependencies)

    ./sbt run

### Run tests

    ./sbt test

### Useful documentation

* [Matcher on tests using specs2](https://etorreborre.github.io/specs2/guide/SPECS2-3.0/org.specs2.guide.Matchers.html)
* [Mockito usage specs2](https://etorreborre.github.io/specs2/guide/SPECS2-3.0/org.specs2.guide.UseMockito.html)
* [Quick guide to scala features: Scala wat](http://seanparsons.github.io/scalawat/Values+and+variables.html)
* [Slick 2.1 documentation](http://slick.typesafe.com/doc/2.1.0/)

## Docker setup

Requires docker and docker-compose

### Install dependencies

If you haven't start by pulling all the dependencies (and sbt 0.13), this only needs to be done once,
or everytime you change your Dockerfile or build.sbt

    docker-compose build

There are a few different ways to speed up this build, like using a different docker base image from dockerhub,
with sbt and some dependecies already installed.

### Migrate schema

Run the flywayMigrate command to update the database schema

    docker-compose run scalarest bash -c "./sbt flywayMigrate"

Also, our `docker-compose run scalarest ./run` do migrate.

If you want to add new migrations, just add new SQL files into `src/main/resources/db.migration`

### Run

    docker-compose up

Run with different config settings:

    docker-compose run --service-ports scalarest bash -c 'export MODE=production && ./sbt run'

### Test

Tests unit tests and integration (it) tests alltogether. Run this first to initialize test database too.

    docker-compose run scalarest ./test

or run specified unit test continuously

    docker-compose run scalarest bash -c "./sbt '~test-only *UserTest*'"

or run integration tests (it flag)

    docker-compose run scalarest bash -c "./sbt '~it:test-only *UserIntegrationTest*'"

## Troubleshooting

If you get a `java.io.IOException: Permmission denied (Boot.scala)` when importing the project to IntelliJ or running `./sbt`
make sure your `project` and `target` folder has the right permissions.

    sudo chmod 777 -R project target
