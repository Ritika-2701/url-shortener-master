FROM mysql:8

ENV MYSQL_DATABASE=urldb \
    MYSQL_ROOT_PASSWORD=Root54321

ADD schema.sql /docker-entrypoint-initdb.d

EXPOSE 3306

