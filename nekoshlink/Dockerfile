#
# Build the binary packages from the source
#

FROM gradle:7.2-jdk17 AS build

WORKDIR /nekoshlink
COPY --chown=gradle:gradle docker/ docker/
COPY --chown=gradle:gradle shlink-cli/ shlink-cli/
COPY --chown=gradle:gradle shlink-core/ shlink-core/
COPY --chown=gradle:gradle shlink-rest/ shlink-rest/
COPY --chown=gradle:gradle shlink-security/ shlink-security/
COPY --chown=gradle:gradle shlink-userbase/ shlink-userbase/
COPY --chown=gradle:gradle build.gradle.kts .
COPY --chown=gradle:gradle settings.gradle.kts .

RUN gradle \
    :shlink-userbase:assemble \
    :shlink-security:assemble \
    :shlink-core:assemble \
    :shlink-cli:assemble \
    :shlink-rest:assemble

ENTRYPOINT /bin/sh

#
# Build a minimal executable image with the binary packages
#

FROM azul/zulu-openjdk-alpine:17-jre AS run

WORKDIR /nekoshlink

COPY --from=build /nekoshlink/shlink-rest/build/libs/shlink-rest-*.jar lib/
COPY --from=build /nekoshlink/shlink-cli/build/libs/shlink-cli-*.jar lib/
COPY --from=build /nekoshlink/docker/*.yaml conf/
COPY --from=build /nekoshlink/docker/*.sh bin/
RUN chmod -R a+x bin/*
RUN mkdir logs/
RUN mkdir data/

ENV NKSHLINK_JDBC_URL="jdbc:h2:file:/nekoshlink/data/nekoshlink.db;AUTO_SERVER=TRUE"
ENV NKSHLINK_JDBC_USERNAME=""
ENV NKSHLINK_JDBC_PASSWORD=""
ENV NKSHLINK_JPA_PLATFORM="org.hibernate.dialect.H2Dialect"
ENV NKSHLINK_OAUTH2_ISSUER=""
ENV NKSHLINK_SERVER_PORT=8080
ENV NKSHLINK_SSL_ENABLED=false
ENV NKSHLINK_SSL_KEY_ALIAS=""
ENV NKSHLINK_SSL_KEY_PASSWORD=""
ENV NKSHLINK_SSL_KEYSTORE=""
ENV NKSHLINK_SSL_KEYSTORE_PASSWORD=""
ENV NKSHLINK_SSL_TRUSTSTORE=""
ENV NKSHLINK_SSL_TRUSTSTORE_PASSWORD=""
ENV NKSHLINK_AUDIT_PORT=8081
ENV NKSHLINK_AUDIT_PATH="/manage"
ENV NKSHLINK_CFG_LOGLEVEL="info"
ENV NKSHLINK_CFG_DOMAIN="http://localhost:8080"
ENV NKSHLINK_CFG_APIKEY="you_must_change_this"
ENV NKSHLINK_CFG_HASHSALT="you_must_change_this"
ENV NKSHLINK_CFG_BASE_URL=""
ENV NKSHLINK_CFG_REQ_ERR=""
ENV NKSHLINK_CFG_PWD_FORM=""
ENV NKSHLINK_MAXMIND_PATH=""
ENV NKSHLINK_MAXMIND_KEY=""

EXPOSE $NKSHLINK_SERVER_PORT

ENTRYPOINT ["sh", "/nekoshlink/bin/nkshlink-api.sh"]
