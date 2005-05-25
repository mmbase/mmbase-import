README for maven-mmbase $Id: README.txt,v 1.10 2005-05-25 08:16:00 marcel Exp $

(See the MAVEN.txt when you have maven related issues)

prerequisitions
---------------

- java >= 1.4 
- maven 1.0.2

To install maven, expand it into a directory and set the environment and
path variable to this directory:

@> cd tmp
@> tar zxvf ~/Documents/maven-1.0-rc1.tar.gz
@> MAVEN_HOME=$HOME/tmp/maven-1.0-rc1
@> export MAVEN_HOME
@> PATH=$PATH:$MAVEN_HOME/bina

import external libraries
-------------------------

Due to license restrictions some libraries can't be redistribitued by others
than Sun, so you'll have to get it yourself. You'll have to download each one 
and place it in your local Maven repository (on UNIX systems, this defaults 
to ~/.maven/repository/; on Windows, it's in the equivalent "home" directory)

The jai-core and jai-codec can be downloaded from the following website:

 - http://java.sun.com/products/java-media/jai/index.jsp

If you already have a mmbase-checkout, those jai-file can be readily copied 
from that installation into the maven-repository.

@> cp ${mmbasepath}/build/lib/jai_codec.jar ~/.maven/repository/jai/jars/jai_codec-1.1.2_01.jar   
@> cp ${mmbasepath}/build/lib/jai_core.jar ~/.maven/repository/jai/jars/jai_core-1.1.2_01.jar 

checkout mmbase with maven
--------------------------

Under windows for some reason (maybe user home not defined the ant 
cvspass command fails) run

@> cvs -d:pserver:guest@cvs.mmbase.org:/var/cvs login
(type guest at the prompt)


After these prerequisitions are met, your system is ready to install
mmbase with maven.

# checkout mmbase
# ---------------
# 
# To checkout the mmbase sources use the following command:

maven mm:checkout

# initialize
# ----------
# Run the mavenised ant task in this directory.This wil checkout 
# the current mmbase to the tmp directory and copy files to 
# the respective projects

maven mm:mavenize

#
# creating the mmbase-core-config jar
#

cd mmbase-core-config
maven jar:install

#
# creating the mmbase-core jar
#

cd ../mmbase-core
maven jar:install

#
# creating the mmbase-taglib jar
#

cd ../mmbase-taglib
maven jar:install

#
# creating the mmbase-webapp 
#

cd ../mmbase-webapp
maven war

