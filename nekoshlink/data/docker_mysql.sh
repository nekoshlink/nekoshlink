DATA_DIR="$(dirname "$0")/mysql"

docker run --name nkshlink-mysql -p 3306:3306 -v $DATA_DIR:/var/lib/mysql -e MYSQL_DATABASE=nekoshlink -e MYSQL_USER=nekoshlinkuser -e MYSQL_PASSWORD=P@ssw0rd! -e MYSQL_ROOT_PASSWORD=P@ssw0rd! -d mysql
