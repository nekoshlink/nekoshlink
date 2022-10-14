#!/bin/sh

java \
    -Dspring.config.location=/nekoshlink/conf/application.yaml \
    -Dlogging.file.path=/nekoshlink/logs \
    -jar /nekoshlink/lib/shlink-rest-*.jar
