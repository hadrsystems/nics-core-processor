#!/bin/bash

NAME=${NAME:-"raws-feed-processor"}
LIBDIR=${LIBDIR:-"./lib"}

echo "--------invoking camel spring app: ${NAME} on $(date)"
echo " $(pwd)"

java -DappName=${NAME} -Xmx512M -server -cp "$LIBDIR/*" org.apache.camel.spring.Main -fa config/spring/${NAME}.xml
