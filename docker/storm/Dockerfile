FROM ubuntu
MAINTAINER Wang Xiaoyi <eatvlis@gmail.com>

# install jdk
RUN mkdir -p /usr/lib/jvm
COPY ./jdk-8u111-linux-x64.tar.gz  /usr/lib/jvm/
RUN cd /usr/lib/jvm/ && tar -zvxf jdk-8u111-linux-x64.tar.gz
RUN mv /usr/lib/jvm/jdk1.8.0_111  /usr/lib/jvm/jdk1_8

ENV JAVA_HOME /usr/lib/jvm/jdk1_8
ENV CLASSPATH .:$JAVA_HOME/bin

# install storm
RUN mkdir -p /tmp/dcep
WORKDIR /tmp/dcep
COPY ./storm.tar /tmp/dcep/
RUN tar -vxf storm.tar


ENV PATH $PATH:/tmp/dcep/storm/bin
