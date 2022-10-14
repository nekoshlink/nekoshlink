DATA_DIR="$(dirname "$0")/mssql"

docker run --name nkshlink-mssql -e ACCEPT_EULA=Y -e MSSQL_SA_PASSWORD=P@ssw0rd! -e MSSQL_PID=Standard -p 1433:1433 -v $DATA_DIR:/var/opt/mssql/data -d mcr.microsoft.com/mssql/server:2022-latest
sleep 7
docker exec -it nkshlink-mssql /opt/mssql-tools/bin/sqlcmd -S localhost -U sa -P P@ssw0rd! -Q "CREATE DATABASE NekoShlink"
