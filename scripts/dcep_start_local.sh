#!/usr/bin/env bash

#start the dcep system by local mode
zkServer.sh start
sleep 10s
kafka-server-start.sh $KAFKA_HOME/config/server.properties
