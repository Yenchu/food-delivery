# Food Delivery
A demo backend for food delivery which is written in Java 11 with Gradle build tool and uses MySql as database.

## Setup Database Schema

Start MySql with root password and a local folder for persisting MySql data.

```shell script
docker run -d -p 3306:3306 -v YOUR_LOCAL_FOLDER:/var/lib/mysql -e MYSQL_ROOT_PASSWORD=YOUR_PASSWORD --name mysql mysql:5.7 --character-set-server=utf8mb4 --collation-server=utf8mb4_unicode_ci
```

Change to sql/ directory, execute the following command:

```shell script
docker exec -i mysql mysql -uroot -pYOUR_PASSWORD < schema.sql
```

## Build App Docker Image

Build Java jar file with Gradle

```shell script
./gradlew build -x test
```

Build Docker image

```shell script
./gradlew jibDockerBuild
```

## Load Data to Database

There are two raw data sources: 
- restaurant_with_menu: https://gist.githubusercontent.com/seahyc/b9ebbe264f8633a1bf167cc6a90d4b57/raw/021d2e0d2c56217bad524119d1c31419b2938505/restaurant_with_menu.json
- users_with_purchase_history: https://gist.githubusercontent.com/seahyc/de33162db680c3d595e955752178d57d/raw/785007bc91c543f847b87d705499e86e16961379/users_with_purchase_history.json

If the raw data sources changed, please use `RESTAURANT_DATA_URL` and `USER_DATA_URL` as environment variables to pass correct URL paths to Docker.
Execute the following command to extract, transform and load data to database:

```shell script
docker run -i -e DB_URL=mysql:3306 -e LOAD_DB_DATA=3 --rm --link mysql:mysql --name food-delivery food-delivery
```

## Run App

```shell script
docker run -d -p 8080:8080 -e DB_URL=mysql:3306 --link mysql:mysql --name food-delivery food-delivery
```