docker run \
  -e "NKSHLINK_JDBC_URL=jdbc:mariadb://host.docker.internal:3306/nekoshlink" \
  -e "NKSHLINK_JDBC_USERNAME=nekoshlinkuser" \
  -e "NKSHLINK_JDBC_PASSWORD=P@ssw0rd!" \
  -e "NKSHLINK_JPA_PLATFORM=org.hibernate.dialect.MariaDB10Dialect" \
  -e "NKSHLINK_CFG_USERBASE=NATIVE" \
  -e "NKSHLINK_CFG_APIKEY=NekoShlinkRocks" \
  -e "NKSHLINK_CFG_HASHSALT=d71d792c-c849-4839-9bb6-cc6d519db975" \
  --name nekoshlink -p 8080:8080 -d nekosoft/nekoshlink:v0.9.0
