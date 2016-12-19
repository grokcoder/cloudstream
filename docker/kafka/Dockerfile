FROM ubuntu

MAINTAINER Wang Xiaoyi

# install jdk
RUN mkdir -p /usr/lib/jvm
COPY ./jdk-8u111-linux-x64.tar.gz  /usr/lib/jvm/
RUN cd /usr/lib/jvm/ && tar -zvxf jdk-8u111-linux-x64.tar.gz
RUN mv /usr/lib/jvm/jdk1.8.0_111  /usr/lib/jvm/jdk1_8

ENV JAVA_HOME /usr/lib/jvm/jdk1_8
ENV CLASSPATH .:$JAVA_HOME/bin/dt.jar:$JAVA_HOME/lib/tools.jar
ENV PATH $PATH:$JAVA_HOME/bin

# install kafka
RUN mkdir -p /tmp/dcep
WORKDIR /tmp/dcep
COPY ./kafka.tar /tmp/dcep/
RUN tar -vxf kafka.tar

EXPOSE 9092


ENV PATH $PATH:/tmp/dcep/kafka/bin


#CMD ./kafka/bin/kafka-server-start.sh ./kafka/config/server.properties
#CMD /bin/bash






