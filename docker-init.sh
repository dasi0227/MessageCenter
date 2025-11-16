docker run -d \
    --name mysql \
    -p 3306:3306 \
    -e MYSQL_ROOT_PASSWORD=123456 \
    -v /Users/wyw/docker/mysql/data:/var/lib/mysql \
    -v /Users/wyw/docker/mysql/conf:/etc/mysql/conf.d \
    -v /Users/wyw/docker/mysql/logs:/logs \
    mysql:8.0.40

docker run -d \
  --name redis \
  -p 6379:6379 \
  -v ~/docker/redis/data:/data \
  redis:8.0.1 \
  redis-server --appendonly yes --requirepass "123456"

docker run -d \
  --name rabbitmq \
  --hostname rabbitmq \
  -v /Users/wyw/docker/rabbitmq/data:/var/lib/rabbitmq \
  -v /Users/wyw/docker/rabbitmq/log:/var/log/rabbitmq \
  -p 5672:5672 \
  -p 15672:15672 \
  -e RABBITMQ_DEFAULT_USER=root \
  -e RABBITMQ_DEFAULT_PASS=123456 \
  rabbitmq:3.13-management

docker cp rabbitmq_delayed_message_exchange-3.13.0.ez rabbitmq:/plugins/
docker exec -it rabbitmq rabbitmq-plugins enable rabbitmq_delayed_message_exchange
docker restart rabbitmq
docker exec -it rabbitmq rabbitmq-plugins list | grep delayed


docker network create message-net

docker network connect message-net mysql
docker network connect message-net redis
docker network connect message-net rabbitmq

docker network inspect message-net