FROM ubuntu
# base image for DCEP component
MAINTAINER Wang Xiaoyi <eatvlis@gmail.com>

# install jdk
RUN mkdir -p /usr/lib/jvm
COPY ./jdk-8u111-linux-x64.tar.gz  /usr/lib/jvm/
RUN cd /usr/lib/jvm/ && tar -zvxf jdk-8u111-linux-x64.tar.gz
RUN mv /usr/lib/jvm/jdk1.8.0_111  /usr/lib/jvm/jdk1_8

# set the environment variables
ENV JAVA_HOME /usr/lib/jvm/jdk1_8
ENV CLASSPATH .:$JAVA_HOME/bin/dt.jar:$JAVA_HOME/lib/tools.jar
ENV PATH $PATH:$JAVA_HOME/bin

# start cmd
ENTRYPOINT ["/bin/bash"]





