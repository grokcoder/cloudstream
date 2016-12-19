FROM dcepbase

MAINTAINER Wang Xiaoyi <eatvlis@gmail.com>

# install zookeeper
RUN mkdir -p /tmp/dcep && mkdir -p /tmp/zookeeper
WORKDIR /tmp/dcep
COPY ./zookeeper.tar /tmp/dcep/
RUN tar -vxf zookeeper.tar

COPY ./docker-entrypoint.sh /tmp/dcep/
RUN chmod +x docker-entrypoint.sh

# setup zookeeper env

ENV PATH $PATH:/tmp/dcep/zookeeper/bin
ENV PATH $PATH:/tmp/dcep/

EXPOSE 2888 3888

#ENTRYPOINT ["docker-entrypoint.sh"]

