#!/usr/bin/env bash

nodes=(172.16.0.6 172.16.0.7 172.16.0.8 172.16.0.9)
export USER="satoshi"

function init(){

    for node in ${nodes[@]}
    do
        ssh $USER@${node} "mkdir -p xiaoyi/dcep/data/kafka"
        ssh $USER@${node} "mkdir -p xiaoyi/dcep/data/kafka/kafka_logs"

        # send file to remote machine
        #scp ./components/zookeeper-3.4.9.tar  $USER@${node}:~/xiaoyi/dcep/components/
    done
}

## install kafka
function install_kafka(){
    for node in ${nodes[@]}
    do
        echo "sending kafka to host $node"
        scp ./components/kafka_2.10-0.10.1.0.tar  $USER@${node}:~/xiaoyi/dcep/components/
        ssh $USER@${node} "tar -vxf ~/xiaoyi/dcep/components/kafka_2.10-0.10.1.0.tar"
        ssh $USER@${node} "rm -rf ~/xiaoyi/dcep/components/kafka"
        ssh $USER@${node} "mv -f ./kafka_2.10-0.10.1.0 ~/xiaoyi/dcep/components/kafka"
    done
}


function start_kafka(){
    declare -i id=1
    for node in ${nodes[@]}
    do
        echo "starting kafka on host $node"
        kafka_path="/home/satoshi/xiaoyi/dcep/components/kafka/"
        nohup ssh $USER@${node} "$kafka_path/bin/kafka-server-start.sh $kafka_path/config/server-$id.properties &" & #start service remotely
        id=$(($id + 1))
    done
}

function stop_kafka(){
    for node in ${nodes[@]}
    do
        echo "stopping kafka on host $node"
        kafka_path="/home/satoshi/xiaoyi/dcep/components/kafka/"
        ssh $USER@${node} "$kafka_path/bin/kafka-server-stop.sh $kafka_path/config/server-$id.properties "
        ssh $USER@${node} "ps aux | grep kafka | awk '{print \$2}' | xargs kill -9"
        id=$(($id + 1))
    done
}

stop_kafka
start_kafka

