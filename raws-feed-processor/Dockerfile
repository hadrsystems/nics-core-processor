#!/bin/bash

FROM openjdk:7-jre 		

ENV NICS_BASE /opt/nics/deploy	
RUN mkdir -p $NICS_BASE          
WORKDIR $NICS_BASE               

COPY target/raws-feed-processor-6.2-deployable.tar.gz .   
RUN tar -xzf raws-feed-processor-6.2-deployable.tar.gz && rm raws-feed-processor-6.2-deployable.tar.gz
WORKDIR $NICS_BASE/raws-feed-processor-6.2

#RUN rm -rf config   		

ENTRYPOINT ["./start.sh"] 	 

