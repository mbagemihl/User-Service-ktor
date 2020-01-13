# User-Service
Simple ktor backend-application with postgres connection via Exposed Framework

#### Starting PostgreSQL database (Optional)

From the root path directory, run:

`docker-compose up -d`

This will create a container for PostgreSQL database.
To stop the database environment, run:

`docker-compose down`

#### Running ktor backend-application

For starting the ktor backend-application, run:

`chmod u+x gradlew`

`./gradlew run`
