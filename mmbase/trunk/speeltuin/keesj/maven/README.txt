README for maven-mmbase $Id: README.txt,v 1.6 2005-02-08 14:32:13 keesj Exp $

RUNNING:
download maven from http://maven.apache.org/
@> cd tmp
@> tar zxvf ~/Documents/maven-1.0-rc1.tar.gz
@>MAVEN_HOME=$HOME/tmp/maven-1.0-rc1
@>export MAVEN_HOME
@>PATH=$PATH:$MAVEN_HOME/bin

run the ant task in this directory
@>ant


#
# creating the mmbase-core-config jar
#

goto the mmbase-core-config
run maven jar:install

#
# creating the mmbase-core jar
#

goto the mmbase-core
run maven jar:install

#
# creating the mmbase-taglib jar
#

goto the mmbase-taglib
run maven jar:install

#
# creating the mmbase-webapp 
#

goto the mmbase-webapp
run maven war

