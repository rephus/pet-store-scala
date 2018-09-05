### Scala REST pet store service

Scala REST skeleton service that features:

* Akka 2.3.9
* Spray 1.3
* Slick 2.1 (using postgresql connector)

## Run binary

![pet_store.gif](pet_store.gif)

First run database locally, this app requires a database postgresql "pet_store" running on localhost:5432

    docker-compose up -d postgres
    ./create_database

Then run the main app

    java -jar pet-store.jar

Once running, endpoint will be available on localhost:8000

    curl -X POST "localhost:8000/user" -H "accept: application/json" -H "Content-Type: application/json" -d "{ \"email\": \"string\", \"id\": 0, \"password\": \"string\"}"


## Run source code

### Run with docker !!

Requires docker and docker-compose

    Note: Dependencies and sbt could take a while to download
    Also note: Change the address of postgres on application.conf from "localhost" to "postgres"

    docker-compose up -d postgres
    docker-compose up pet_store

### Run server (with local dependencies)

    ./run or ./sbt run

Then you can generate a new runnable .jar package with

    ./sbt assembly


### Run tests

    ./test or ./sbt test


## Assumptions

- Passwords are not encrypted. This is trivial, but some discussion is needed to decide how to encrypt the password and which encryption to use (SHA1?)
- Although the URLs are supposed to have a versioning system (v2), I think this is something could be specified in the gateway/proxy placed behind this webapp (like nginx).
    This will allow us to trully support multiples version of the API living together. If not, we could easily add "v2" as prefix to all endpoints
- No foreign keys have been defined in the database (like petId on order), it should be done, but it doesn't provide any practical value on this excersise.
- Primary keys for all 3 tables are using Autoincremental (serial) values on Postgres to respect the swagger specification, however, it might be advisable to use UUIDs (string) instead.
- A few basic endpoints are missing from the specification, in order to match the swagger specification. Such as GET/PUT/DELETE user or some generic GET all pets (with or without filters) might be done at some point.
- There are not many specific HTTP errors based on invalid forms or data types on the API, this can be done easily with some map-matching functions on the controllers.
- The database is created on the ./run script, migrations (flyway) are updating the schema on service startup
- This project is ready to run in development mode. For production, a binary jar file can be distributed

## Sample curls

POST a pet

```
curl -X POST "localhost:8000/pet" -H "accept: application/json" -H "Content-Type: application/json" -d "{ \"id\": 0, \"name\": \"doggie\", \"photoUrls\": [ \"string\" ], \"status\": \"available\"}"
```

GET a pet
```
curl -X GET "localhost:8000/pet/2"
{
  "id": 2,
  "name": "doggie",
  "photoUrls": ["string"],
  "status": "available"
}
```

GET filter by status

```
curl -X GET "localhost:8000/pet/findByStatus?status=available"

[{
  "id": 1,
  "name": "doggie",
  "photoUrls": ["string"],
  "status": "available"
}, {
  "id": 2,
  "name": "doggie",
  "photoUrls": ["string"],
  "status": "available"
}, {
  "id": 3,
  "name": "doggie",
  "photoUrls": ["string"],
  "status": "available"
}]
```

POST an order

```
curl -X POST "localhost:8000/store/order" -H "accept: application/json" -H "Content-Type: application/json" -d "{ \"complete\": false, \"id\": 0, \"petId\": 0, \"quantity\": 0, \"status\": \"placed\"}"
```

POST an user

```
curl -X POST "localhost:8000/user" -H "accept: application/json" -H "Content-Type: application/json" -d "{ \"email\": \"string\", \"id\": 0, \"password\": \"string\"}"
```

