# Installation of DCEP system components

## 0. Set no password login and make directory
   ssh-copy-id username@host
   example: ssh-copy-id satoshi@172.16.0.6
    
    

## 1. Install Zookeeper Cluster
    
 a).unzip the zookeeper-version.tar.gz
 b).make the configuration
  1). conf/zoo.cfg:
        tickTime=2000
        dataDir=/var/lib/zookeeper
        clientPort=2181
        server.1=cn6:2888:3888
        server.2=cn7:2888:3888
        server.3=cn8:2888:3888
  2). add myid file into the data dir of zookeeper

  c).ref deploy_zookeeper.sh

## 2. Install Kafka Cluster

## 3. Install Storm Cluster
 

