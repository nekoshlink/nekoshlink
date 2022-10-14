## Environment Variables for Docker Image

This is a list of the environment variables used when running the Docker image, along with the corresponding default values

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
