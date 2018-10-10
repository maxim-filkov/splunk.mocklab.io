FROM java:8
MAINTAINER Maxim Filkov <maximfilkov@gmail.com>

RUN apt-get update && apt-get install -y \
  git \
  maven

RUN git clone https://github.com/maxim-filkov/splunk.mocklab.io.git

RUN cd splunk.mocklab.io && mvn clean test