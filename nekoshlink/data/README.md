# NekoShlink Database

NekoShlink requires a relational database to store the entities it manages:
short URLs, domains, tags, as well as the log of visits to each short URL.

## Initial User

When a new database is created for the first time, NekoShlink will create all
the necessary tables, including the user tables for authentication using a local
user repository. In this case, an admin user is also created. The user is called
`nekoadmin` and a password is generated and stored in the logs at the `WARN`
level. You need access to the logs in order to get that initial password.

If you want to quickly reset the password of a user to a known, test-only value, you
can set it to `Passw0rd!` using the following BCrypt string

```
$2y$10$dqjLmJQsLu7QwGr8CIo9EuXiu97kHPhVCa3LF3ug3grzPQ.wh0O3e
```

or you can generate your own password on [https://bcrypt.online]().

## Supported Databases

The following databases have been tested and all necessary drivers are packed
within the main application bundle.

Any other relational database for which there is a JBDC driver and JPA support
should also work, but has not been tested and drivers must be included separately.

### H2

The default database, in embedded mode, if no other choice is made. It works fine,
but should not be used for production systems.

```yaml
spring:
  datasource:
    url: "jdbc:h2:file:./data/h2/nekoshlink.db;AUTO_SERVER=TRUE"
  jpa:
    database-platform: "org.hibernate.dialect.H2Dialect"
    hibernate:
      ddl-auto: "update"
```

### PostgreSQL

A production-ready distributed database. You can run a Docker instance with
the `docker_pgsql.sh` command in this folder.

```yaml
spring:
  datasource:
    url: "jdbc:postgresql://localhost:5432/nekoshlink"
    username: "nekoshlinkuser"
    password: "P@ssw0rd!"
  jpa:
    database-platform: "org.hibernate.dialect.PostgreSQL10Dialect"
    hibernate:
      ddl-auto: "update"
```

### MariaDB

A production-ready distributed database. You can run a Docker instance with
the `docker_mariadb.sh` command in this folder.

```yaml
spring:
  datasource:
    url: "jdbc:mariadb://localhost:3306/nekoshlink"
    username: "nekoshlinkuser"
    password: "P@ssw0rd!"
  jpa:
    database-platform: "org.hibernate.dialect.MariaDB10Dialect"
    hibernate:
      ddl-auto: "update"
```

### MySQL

A production-ready distributed database. You can run a Docker instance with
the `docker_mysql.sh` command in this folder.

```yaml
spring:
  datasource:
    url: "jdbc:mysql://localhost:3306/nekoshlink"
    username: "nekoshlinkuser"
    password: "P@ssw0rd!"
  jpa:
    database-platform: "org.hibernate.dialect.MySQL8Dialect"
    hibernate:
      ddl-auto: "update"
```

### Microsoft SQL Server

A production-ready distributed database. You can run a Docker instance with
the `docker_mssql.sh` command in this folder: the docker instance is only licensed to run
in development environments and cannot be used in production.

The docker script provided will also create the `NekoShlink` database. It is not possible
to do so while creating the docker container.

```yaml
spring:
  datasource:
    url: "jdbc:sqlserver://localhost;databaseName=NekoShlink"
    username: "sa"
    password: "P@ssw0rd!"
  jpa:
    database-platform: "org.hibernate.dialect.SQLServer2016Dialect"
    hibernate:
      ddl-auto: "update"
```
