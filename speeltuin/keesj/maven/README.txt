README for maven-mmbase $Id: README.txt,v 1.1 2003-12-19 18:13:41 keesj Exp $

RUNNING:
download maven from http://maven.apache.org/
@> cd tmp
@> tar zxvf ~/Documents/maven-1.0-rc1.tar.gz
@>MAVEN_HOME=$HOME/tmp/maven-1.0-rc1
@>export MAVEN_HOME
@>PATH=$PATH:$HAVEN_HOME/bin

run the ant task in this directory
@>ant

goto the mmbase-core directory
@>cd mmbase-code

run maven
@>maven site

wait.....

the result can be found in mmbase-core/target/docs

