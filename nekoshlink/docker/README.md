# NekoShlink Docker Image

The `Dockerfile` in the top-level directory creates a Docker image of NekoShlink
that can be used for running the service wherever a Docker container engine is
available.

## Environment Variables

This is a list of environment variables and their corresponding default values

```properties
# Database configuration properties

NKSHLINK_JDBC_URL="jdbc:h2:file:/nekoshlink/data/nekoshlink.db;AUTO_SERVER=TRUE"
NKSHLINK_JDBC_USERNAME=""
NKSHLINK_JDBC_PASSWORD=""
NKSHLINK_JPA_PLATFORM="org.hibernate.dialect.H2Dialect"

# OAuth2 issuer endpoint

NKSHLINK_OAUTH2_ISSUER=""

# Server and TLS configuration options

NKSHLINK_SERVER_PORT=8080
NKSHLINK_SSL_ENABLED=false
NKSHLINK_SSL_KEY_ALIAS=""
NKSHLINK_SSL_KEY_PASSWORD=""
NKSHLINK_SSL_KEYSTORE=""
NKSHLINK_SSL_KEYSTORE_PASSWORD=""
NKSHLINK_SSL_TRUSTSTORE=""
NKSHLINK_SSL_TRUSTSTORE_PASSWORD=""

# Audit configuration options
# (NKSHLINK_AUDIT_PATH must start with /)

NKSHLINK_AUDIT_PORT=8081
NKSHLINK_AUDIT_PATH="/manage"

# Logging level

NKSHLINK_CFG_LOGLEVEL="info"

# NekoShlink options
# (NKSHLINK_CFG_DOMAIN must include protocol, and port if non-standard)

NKSHLINK_CFG_DOMAIN="http://localhost:8080"

# Security options

NKSHLINK_CFG_USERBASE="NATIVE"
NKSHLINK_CFG_APIKEY="you_must_change_this"
NKSHLINK_CFG_HASHSALT="you_must_change_this"
```

### Database

NekoShlink can work out of the box with the default values. It will run an
embedded instance of the H2 database. This is *not* a recommended configuration
for a production environment, but can be used as a proof-of-concept or as a
starting point to try configurations out.

More information on supported databases can be found in [the Database README file](../data/README.md).

### Userbase

NekoShlink offers different user base repositories for authentication and authorization. You can choose
the option for your installation with the `NKSHLINK_CFG_USERBASE` environment variable.

| Value    | Description                                                                                        |
|----------|----------------------------------------------------------------------------------------------------|
| `NATIVE` | JPA-based user repository: HTTP BASIC challenge for authentication, roles table for authorization  |
| `X509`   | Same JPA-based repository as `NATIVE`, but challenge is via X.509 certificates                     |
| `OIDC`   | OIDC-based authentication and OAuth2-based authorization                                           |

## Running NekoShlink

The [Shell script provided here](docker_run_nekoshlink.sh) is an example of how you can run NekoShlink
with MariaDB. The database configuration in the script matches the database engine that would be set up
by the [MariaDB docker script](../data/docker_mariadb.sh).

The runner script assumes that the image has been built with the tag indicated in the 
[building instructions](#image-building).

## Image Building

Building the image for NekoShlink in the official repository is done as follows

```shell
docker build -t nekosoft/nekoshlink:v0.9.0 .
docker push nekosoft/nekoshlink:v0.9.0
```
