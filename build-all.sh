#!/usr/bin/env bash
# exit when any command fails
set -e
export MAVEN_OPTS="-Dorg.slf4j.simpleLogger.log.org.apache.maven.cli.transfer.Slf4jMavenTransferListener=warn"

DIR="$( cd -- "$( dirname -- "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )"
#MVN="mvn -Dcom.sun.net.ssl.checkRevocation=false -Dhttps.protocols=TLSv1.2 -Duser.home=$DIR"
SETTINGS=${GITHUB_WORKSPACE:-$DIR}/.github/settings.xml
cat $SETTINGS
echo $OSSRH_PASSWORD | md5sum
MVN="mvn $MAVEN_OPTS -s $SETTINGS -Dgpg.skip=true -B"

$MVN effective-pom
for d in  . maven-base maven maven/maven-mmbase-plugin maven-base/applications applications   ; do
    (cd $DIR/$d &&  $MVN -N clean deploy)
done


(cd $DIR && $MVN -P'deploy,!development' clean deploy)
(cd $DIR/base-app && $MVN -P'deploy,!development' clean deploy)
(cd $DIR/applications && $MVN -P'deploy,!development' clean deploy)
