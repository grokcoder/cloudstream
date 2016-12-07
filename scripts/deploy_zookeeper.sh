#!/usr/bin/env bash

#tar -zvxf zookeeper-3.4.9.tar.gz

export USER="satoshi"

nodes=(172.16.0.6 172.16.0.7 172.16.0.8 172.16.0.9)

# setup no password login
function set_up_nopwd_login(){
    for node in ${nodes[@]}
    do
        echo send ssh key to ${node} ...
        echo $USER@${node}
        echo ssh-copy-id  $USER@${node}
    done
}

function install_jdk(){
    for node in ${nodes[@]}
    do
        ssh $USER@${node} "sudo yum -y install java-1.8.0-openjdk" #on centos
    done
}

#############################################################################################
#                                                                                           #
#                      set up zookeeper cluster                                             #
#                                                                                           #
#                                                                                           #
##############################################################################################
zoo_nodes=(172.16.0.6 172.16.0.7 172.16.0.8) #node ip seperate by blank

function init(){

    declare -i id=1
    for node in ${zoo_nodes[@]}
    do
        ssh $USER@${node} "mkdir -p xiaoyi/dcep/components"
        ssh $USER@${node} "mkdir -p xiaoyi/dcep/testcases"
        ssh $USER@${node} "mkdir -p xiaoyi/dcep/data/zookeeper"

        #config myid
        ssh $USER@${node} "touch xiaoyi/dcep/data/zookeeper/myid"
        ssh $USER@${node} "echo $id > xiaoyi/dcep/data/zookeeper/myid"
        id=$(($id + 1))

        # send file to remote machine
        scp ./components/zookeeper-3.4.9.tar  $USER@${node}:~/xiaoyi/dcep/components/
    done
}

## install zookeeper
function install_zookeeper(){
    for node in ${zoo_nodes[@]}
    do
        echo "sending zookeeper to host $node"
        scp ./components/zookeeper-3.4.9.tar  $USER@${node}:~/xiaoyi/dcep/components/
        ssh $USER@${node} "tar -vxf ~/xiaoyi/dcep/components/zookeeper-3.4.9.tar"
        ssh $USER@${node} "rm -rf ~/xiaoyi/dcep/components/zookeeper"
        ssh $USER@${node} "mv -f ./zookeeper-3.4.9 ~/xiaoyi/dcep/components/zookeeper"
    done
}

## start zookeeper cluster
# todo: can't start now
function start_zookeeper_cluster(){
    echo "try to start zookeeper cluster ..."
    for node in ${zoo_nodes[@]}
    do
        ssh $USER@${node} "~/xiaoyi/dcep/components/zookeeper/bin/zkServer.sh start"
    done
    echo "zookeeper cluster started!"
}

## stop zookeeper cluster

function stop_zookeeper_cluster(){
   for node in ${zoo_nodes[@]}
   do
       ssh $USER@${node} " ~/xiaoyi/dcep/components/zookeeper/bin/zkServer.sh stop"
   done
}

#install_zookeeper
#start_zookeeper_cluster
#stop_zookeeper_cluster
