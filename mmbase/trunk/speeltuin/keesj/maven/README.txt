README for maven-mmbase $Id: README.txt,v 1.3 2004-03-08 22:52:21 keesj Exp $

RUNNING:
download maven from http://maven.apache.org/
@> cd tmp
@> tar zxvf ~/Documents/maven-1.0-rc1.tar.gz
@>MAVEN_HOME=$HOME/tmp/maven-1.0-rc1
@>export MAVEN_HOME
@>PATH=$PATH:$MAVEN_HOME/bin

run the ant task in this directory
@>ant

goto the mmbase-core directory
@>cd mmbase-core

install some additional plugins:(sdocbook)
maven -DartifactId=maven-sdocbook-plugin -DgroupId=maven-plugins -Dversion=1.3 plugin:download


run maven
@>maven site

wait.....

the result can be found in mmbase-core/target/docs

