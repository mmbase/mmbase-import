README for maven-mmbase $Id: README.txt,v 1.7 2005-02-12 22:36:09 keesj Exp $

RUNNING:
download maven from http://maven.apache.org/
@> cd tmp
@> tar zxvf ~/Documents/maven-1.0-rc1.tar.gz
@>MAVEN_HOME=$HOME/tmp/maven-1.0-rc1
@>export MAVEN_HOME
@>PATH=$PATH:$MAVEN_HOME/bin

under windows for some reason (maybe user home not defined the ant cvspass command fails)
run
cvs -d:pserver:guest@cvs.mmbase.org:/var/cvs login
(type guest at the prompt)

run the mavenised ant task in this directory
@>maven init
This wil checkout the current mmbase to the tmp directory
and copy files to the respective projects


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

