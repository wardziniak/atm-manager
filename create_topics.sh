#!/usr/bin/env bash

export HOST_NAME=`ifconfig | grep -Eo 'inet (addr:)?([0-9]*\.){3}[0-9]*' | grep -v '127.0.0.1' | head -1 | tr -s ' ' ' ' | cut -f2 -d" "`

docker run -it --rm wurstmeister/kafka:1.1.0 /opt/kafka/bin/kafka-topics.sh  \
--zookeeper ${HOST_NAME}:2181 \
--partitions 10 \
--replication-factor 1 \
--create --topic requests

docker run -it --rm wurstmeister/kafka:1.1.0 /opt/kafka/bin/kafka-topics.sh  \
--zookeeper ${HOST_NAME}:2181 \
--partitions 10 \
--replication-factor 1 \
--create --topic responses

docker run -it --rm wurstmeister/kafka:1.1.0 /opt/kafka/bin/kafka-topics.sh  \
--zookeeper ${HOST_NAME}:2181 \
--partitions 10 \
--replication-factor 1 \
--create --topic accounts

