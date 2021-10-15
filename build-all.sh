#!/usr/bin/env bash
# exit when any command fails
set -e
export MAVEN_OPTS="-Dorg.slf4j.simpleLogger.log.org.apache.maven.cli.transfer.Slf4jMavenTransferListener=warn"

DIR="$( cd -- "$( dirname -- "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )"
#MVN="mvn -Dcom.sun.net.ssl.checkRevocation=false -Dhttps.protocols=TLSv1.2 -Duser.home=$DIR"
#echo $OSSRH_PASSWORD | base64

MVN="mvn $MAVEN_OPTS -fae  -Dgpg.skip=true -B -Duser.home=$HOME"

#cd $DIR/applications/streams && $MVN -P'deploy,!development' clean deploy
#exit
for d in  . maven-base maven maven/maven-mmbase-plugin maven-base/applications applications   ; do
    (cd $DIR/$d &&  $MVN -N clean deploy)
done


(cd $DIR && $MVN -P'deploy,!development' clean deploy)
