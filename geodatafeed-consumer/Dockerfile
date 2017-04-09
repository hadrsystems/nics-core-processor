FROM openjdk:7-jre

ENV NICS_BASE /opt/nics/deploy
RUN mkdir -p $NICS_BASE
WORKDIR $NICS_BASE

COPY target/geodatafeed-consumer-6.2-deployable.tar.gz .
RUN tar -xzf geodatafeed-consumer-6.2-deployable.tar.gz && rm geodatafeed-consumer-6.2-deployable.tar.gz
WORKDIR $NICS_BASE/geodatafeed-consumer-6.2

COPY config/* config/

ENTRYPOINT ["./start.sh"]
