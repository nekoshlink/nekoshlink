#!/bin/sh

java \
    -Dspring.config.location=/nekoshlink/conf/application.yaml \
    -Dspring.main.banner-mode=OFF \
    -Dlogging.pattern.console= \
    -Dlogging.level.root=INFO \
    -Dlogging.level.org.nekosoft=INFO \
    -Dlogging.level.org.springframework.security=INFO \
    -Dlogging.file.path=/nekoshlink/logs \
    -jar /nekoshlink/lib/shlink-cli-*.jar \
    "$@"
