#!/usr/bin/env bash

#start the dcep system by local mode
zkServer.sh stop
kafka-server-stop.sh $KAFKA_HOME/config/server.properties
