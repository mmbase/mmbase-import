#!/bin/bash

CLASSPATH=
export CLASSPATH

cd /usr/local/webserver/nl/vpro/mmbase/config/plongrunner

while [ 1=1 ]; do
/usr/local/java/bin/java org.mmbase.remote.startRemoteBuilders server.properties g2encoder.properties cdplayer.properties 2>&1 | cat >>remoteBuilder.log
done

