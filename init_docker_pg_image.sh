docker run --name postgres-demo -d -p 54320:5432 -e POSTGRES_PASSWORD=demo postgres:9.6
docker exec postgres-demo psql -U postgres -c"CREATE DATABASE spring_batch_demo" postgres
