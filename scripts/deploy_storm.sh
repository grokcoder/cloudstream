#!/usr/bin/env bash

nodes=(172.16.0.9 172.16.0.8 172.16.0.7 172.16.0.6)
#nodes=(172.16.0.6)
export USER="satoshi"

function install_storm(){
    for node in ${nodes[@]}
    do
        ssh $USER@${node} "mkdir -p /home/satoshi/xiaoyi/dcep/data/storm"
        echo "sending storm to host $node"
        scp ./components/storm.tar  $USER@${node}:~/xiaoyi/dcep/components/storm.tar
        ssh $USER@${node} "cd ~/xiaoyi/dcep/components/ && tar -vxf ~/xiaoyi/dcep/components/storm.tar"
    done
}

function start_storm(){
    declare -i id=0
    STORM_PATH="/home/satoshi/xiaoyi/dcep/components/storm"
    for node in ${nodes[@]}
    do
        echo "start storm on ${node} id = $id"
        ssh $USER@${node} "mkdir -p /home/satoshi/xiaoyi/dcep/data/storm"
        if [ $id -eq 0 ] ; then
            echo "start nimbus"
            nohup ssh $USER@${node} "sudo $STORM_PATH/bin/storm nimbus" &
            nohup ssh $USER@${node} "sudo $STORM_PATH/bin/storm ui" &
        else
            nohup ssh $USER@${node} "sudo $STORM_PATH/bin/storm supervisor" &
        fi
        id = id=$(($id + 1))
    done
}


function stop_storm(){
    declare -i id=0
    for node in ${nodes[@]}
    do
        if [ $id -eq 0 ] ; then
            ssh $USER@${node} "sudo ps aux | grep core | awk '{print \$2}' | xargs kill -9"
            ssh $USER@${node} "sudo ps aux | grep nimbus | awk '{print \$2}' | xargs kill -9"
        else
            ssh $USER@${node} "sudo ps aux | grep supervisor | awk '{print \$2}' | xargs kill -9"
        fi
    done
}


function clean_storm(){
    for node in ${nodes[@]}
    do
        echo "cleaning host $node"
        ssh $USER@${node} "rm -rf /home/satoshi/xiaoyi/dcep/data/storm"
    done
}

#install_storm
#start_storm

#start_storm

stop_storm