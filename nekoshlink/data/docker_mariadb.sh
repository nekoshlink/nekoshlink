DATA_DIR="$(dirname "$0")/mariadb"

docker run --name nkshlink-mariadb -p 3306:3306 -v $DATA_DIR:/var/lib/mysql -e MARIADB_DATABASE=nekoshlink -e MARIADB_USER=nekoshlinkuser -e MARIADB_PASSWORD=P@ssw0rd! -e MARIADB_ROOT_PASSWORD=P@ssw0rd! -d mariadb
