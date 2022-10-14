DATA_DIR="$(dirname "$0")/pgsql"

docker run --name nkshlink-pgsql -p 5432:5432 -v $DATA_DIR:/var/lib/postgresql/data -e POSTGRES_DB=nekoshlink -e POSTGRES_USER=nekoshlinkuser -e POSTGRES_PASSWORD=P@ssw0rd! -d postgres
