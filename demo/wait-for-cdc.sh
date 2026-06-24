#!/bin/sh

echo "Esperando ticdc-setup..."

until nc -z redpanda 9092
do
  sleep 3
done

echo "Kafka OK"

until nc -z tidb-server 4000
do
  sleep 3
done

echo "TiDB OK"

java \
  -Dnet.bytebuddy.experimental=true \
  -javaagent:/opentelemetry-javaagent.jar \
  -jar /app/app.jar