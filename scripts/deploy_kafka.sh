#!/usr/bin/env bash

nodes=(172.16.0.7 172.16.0.8 172.16.0.9)
export USER="satoshi"

function init(){
    for node in ${nodes[@]}
    do
        ssh $USER@${node} "mkdir -p xiaoyi/dcep/data/kafka"
        ssh $USER@${node} "mkdir -p xiaoyi/dcep/data/kafka/kafka-logs"
    done
}

## install kafka
function install_kafka(){
    for node in ${nodes[@]}
    do
        echo "sending kafka to host $node"
        scp ./components/kafka.tar  $USER@${node}:~/xiaoyi/dcep/components/kafka.tar
        ssh $USER@${node} "cd ~/xiaoyi/dcep/components/ && tar -vxf ~/xiaoyi/dcep/components/kafka.tar"
    done
}

function start_kafka(){
    declare -i id=0
    rm ./nohup.out
    for node in ${nodes[@]}
    do
        echo "starting kafka on host $node broker id = $id"
        kafka_path="/home/satoshi/xiaoyi/dcep/components/kafka/"
        ssh $USER@${node} "rm -rf /home/satoshi/xiaoyi/dcep/data/kafka/kafka-logs"
        ssh $USER@${node} "rm -rf /home/satoshi/xiaoyi/dcep/components/kafka/logs"
        if [ $id -eq 0 ] ; then #blank should remain with condition
            nohup  ssh $USER@${node} "$kafka_path/bin/kafka-server-start.sh $kafka_path/config/server.properties &" &
        else

            nohup ssh $USER@${node} "$kafka_path/bin/kafka-server-start.sh $kafka_path/config/server-$id.properties &" & #start service remotely
        fi
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

function clean_kafka(){
    for node in ${nodes[@]}
    do
        echo "clean files on node ${node}"
        ssh $USER@${node} "rm -rf xiaoyi/dcep/data/kafka"
        ssh $USER@${node} "rm -rf xiaoyi/dcep/data/kafka/kafka-logs"
        ssh $USER@${node} "rm xiaoyi/dcep/components/kafka.tar"
        ssh $USER@${node} "rm -rf xiaoyi/dcep/components/kafka"
    done
}

stop_kafka

clean_kafka

install_kafka

start_kafka