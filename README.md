# URL Shortener

Spring Boot based REST API that takes a original URL, custom URL (optional) and expiry Date for urls and returns a shortened URL and uses MySQL to persist data.

# Getting Started

## Dependencies

This project depends on
* spring-boot-starter-web (Spring boot framework)
* spring-boot-starter-data-jpa (for data persistence)
* spring-boot-starter-actuator (for API statistics)
* commons-validator:1.6 (for URL validation)
* h2 (for tests)
* spring-boot-starter-test (for testss)

## Project Build

To build this project, run

```shell script
git clone https://github.com/zeeshaanahmad/url-shortener.git
cd url-shortener
gradle clean build
```

## Deployment

Project build can be deployed using docker-compose.yml which sets up two containers for
* MySql
* REST API

To deploy the project, run

```shell script
docker-compose up --build
```

**The application will be accessible on http://localhost:8080**

### db.Dockerfile
`db.Dockerfile` builds the docker image for MySql using MySql version 8 as the base image. It uses `schema.sql` at startup to set up the database schema.

### api.Dockerfile
`api.Dockerfile` sets up an image to deploy the project's jar file generated above from `build/libs/url-shortener-0.0.1-SNAPSHOT.jar`. It exposes the API on port `8080`

### docker-compose.yml
Provides the configuration for containers to host API and MySql. It sets up two services; `api-server` and `api-db` with container names `urlshortener-springboot` and `mysqlurldb` respectively.
The datasource url is being set in the `api-server` configuration so that it points to the MySql container.
Both `api-server` and `api-db` are linked together through the `urlshortener-mysql-network` docker network. The network enables both the containers to communicate together.

## API Endpoints

You can access following API endpoints at http://localhost:8080

### POST `/shorten`
It takes a JSON object in the following format as payload

```json
{
  "fullUrl":"<The URL to be shortened>"
}
```

#### cURL

```shell script
curl -X POST \
  http://localhost:8080/shorten \
  -H 'Content-Type: application/json' \
  -d '{"fullUrl":"https://example.com/example/1"}'
```

Response:

```json
{
  "originalUrl": "example.com",
  "customUrl": "short.url",
  "expiryPeriod": "2024-02-15 03:37:00"
}
```
### POST `/bulk-shorten`
It takes originalUrl,  customUrl and expiryPeriod in bulk
```json
[
  {
    "originalUrl": "example.com",
    "customUrl": "short.url",
    "expiryPeriod": "2024-02-15 03:37:00"
  }
]
```

#### cURL

```shell script
curl -X POST \
  http://localhost:8080/shorten \
  -H 'Content-Type: application/json' \
  -d '{"fullUrl":"https://example.com/example/1"}'
```

Response:

```json
{
  "shortUrl": "<shortened url for the fullUrl provided in the request payload>"
}
```

Please note that API works only with valid HTTP or HTTPS Urls. In case of malformed Url, it returns `400 Bad Request` error with response body containing a JSON object in the following format

```json
{
  "field":"fullUrl",
  "value":"<Malformed Url provided in the request>",
  "message":"<Exception message>"
}
```

### GET `/<shortened_text>`

This endpoint redirects to the corresponding fullUrl.

### GET `/actuator/health`

Included the spring boot actuator dependency for API metrics. You can try this endpoint for health checks.

#### cURL

```shell script
curl -X GET   http://localhost:8080/actuator/health
```

## Undeploy

To undeploy the containers, run

```shell script
docker-compose down
```

# Url Shortening Algorithm

I thought of two approaches
1. Generating hashes for the fullUrl and storing them as key value pairs in redis cache or in mysql database
2. Performing a Base62 conversion from Base10 on the id of stored fullUrl using zookeeper to support multiple request at once and redis cache 


