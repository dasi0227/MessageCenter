#!/bin/bash

set -e

IMAGE_NAME="dasi0227/message-center"
IMAGE_TAG="1.0"
CONTAINER_NAME="mc"
ENV_FILE=".env.docker"
NETWORK="message-net"

echo "========== 1. Maven clean & package =========="
./mvnw clean package -DskipTests

echo "========== 2. 停止并删除旧容器 =========="
if [ "$(docker ps -aq -f name=$CONTAINER_NAME)" ]; then
  echo "Stopping container..."
  docker stop $CONTAINER_NAME || true
  echo "Removing container..."
  docker rm $CONTAINER_NAME || true
else
  echo "No old container found."
fi

echo "========== 3. 删除旧镜像（如果存在） =========="
if docker images | grep -q "$IMAGE_NAME"; then
  docker rmi -f ${IMAGE_NAME}:${IMAGE_TAG} || true
fi

echo "========== 4. 构建 Docker 镜像 =========="
docker build -t ${IMAGE_NAME}:${IMAGE_TAG} .

echo "========== 5. 启动新容器 =========="
docker run -d \
  --name ${CONTAINER_NAME} \
  --env-file ${ENV_FILE} \
  --network ${NETWORK} \
  -p 8080:8080 \
  -e TZ=Asia/Shanghai \
  ${IMAGE_NAME}:${IMAGE_TAG}

echo "========== 6. 跟踪容器日志 =========="
docker logs -f ${CONTAINER_NAME}

#echo "======= 7. 推送到 Docker Hub ======="
#docker tag dasi0227/message-center:1.0 dasi0227/message-center:1.0
#docker push dasi0227/message-center:1.0