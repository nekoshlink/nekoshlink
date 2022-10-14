# NekoShlink Docker Image

The `Dockerfile` in the top-level directory creates a Docker image of NekoShlink
that can be used for running the service wherever a Docker container engine is
available.

## Environment Variables

Here is a [list of environment variables](https://github.com/nekoshlink/nekoshlink/blob/main/nekoshlink/docker/EnvVars.md) that can be used when running NekoShlink from the Docker image.

### Database

NekoShlink can work out of the box with the default values. It will run an
embedded instance of the H2 database. This is *not* a recommended configuration
for a production environment, but can be used as a proof-of-concept or as a
starting point to try configurations out.

More information on supported databases can be found in [the Database README file](https://github.com/nekoshlink/nekoshlink/blob/main/nekoshlink/data/README.md).

### Userbase

NekoShlink offers different user base repositories for authentication and authorization. You can choose
the option for your installation with the `NKSHLINK_CFG_USERBASE` environment variable.

| Value    | Description                                                                                        |
|----------|----------------------------------------------------------------------------------------------------|
| `NATIVE` | JPA-based user repository: HTTP BASIC challenge for authentication, roles table for authorization  |
| `X509`   | Same JPA-based repository as `NATIVE`, but challenge is via X.509 certificates                     |
| `OIDC`   | OIDC-based authentication and OAuth2-based authorization                                           |

## Running NekoShlink

The [Shell script provided here](https://github.com/nekoshlink/nekoshlink/blob/main/nekoshlink/docker/docker_run_nekoshlink.sh) is an example of how you can run NekoShlink
with MariaDB. The database configuration in the script matches the database engine that would be set up
by the [MariaDB docker script](https://github.com/nekoshlink/nekoshlink/blob/main/nekoshlink/data/docker_mariadb.sh).

The runner script assumes that the image has been built with the tag indicated in the 
[building instructions](#image-building).

## Image Building

Building the image for NekoShlink in the official repository is done as follows

```shell
docker build -t nekosoft/nekoshlink:v0.9.0 .
docker push nekosoft/nekoshlink:v0.9.0
```
