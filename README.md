# A Spring Batch test project

### Some useful Docker scripts

`sudo docker run --name postgres-demo -d -p 54320:5432 -e POSTGRES_PASSWORD=demo postgres:9.6`
`sudo docker exec postgres-demo psql -U postgres -c"CREATE DATABASE spring_batch_demo" postgres`

`sudo docker start postgres-demo`

`sudo docker stop postgres-demo`
`sudo docker rm postgres-demo`
