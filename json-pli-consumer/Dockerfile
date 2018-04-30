FROM openjdk:7-jre

ENV NICS_BASE /opt/nics/deploy
RUN mkdir -p $NICS_BASE
WORKDIR $NICS_BASE

COPY target/json-pli-consumer-6.2-deployable.tar.gz .
RUN tar -xzf json-pli-consumer-6.2-deployable.tar.gz && rm json-pli-consumer-6.2-deployable.tar.gz
WORKDIR $NICS_BASE/json-pli-consumer-6.2

RUN rm -rf config

ENTRYPOINT ["./start.sh"]
