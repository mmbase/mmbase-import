README for maven-mmbase $Id: README.txt,v 1.16 2005-06-02 11:57:43 keesj Exp $

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
@> PATH=$PATH:$MAVEN_HOME/bin

Maven documentation
-------------------

A nice small overview of what maven is, how to configure it and how to
use it, can be downloaded from the following location:

 http://www.sandcastsoftware.com/downloads/maven/ProJavaToolsChap26_Maven.pdf

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

Choose cvs-version
------------------

In order to checkout a certain version of mmbase, define the version
by setting mmbase.checkout.tag in project.properties. 

Supported versions are:

 - MMBase-1_4
 - MMBase-1_5
 - MMBase-1_6
 - MMBase-1_7   - latest stable release
 - HEAD         - current development release, later becomes 1.8

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
# next you should run maven all:install to create an mmbase war file
# 
#
maven all:install

Using eclipse
-------------

First generate a new project by issuing the following command:

maven all:eclipse

To edit sources, open eclipse and open a new Workspace and click on Workspace.
Now import mmbase with the following sequence:

 File ->
   Import -> 
     Existing project in Workspace

Point the directory to this maven-directory and add the following projects:

 - mmbase-core-config
 - mmbase-core
 - mmbase-taglibs
 - mmbase-webapp
 - mmbase-docs

After that, tell eclipse where it can find the maven-repository by clicking:

 Window -> 
   Preferences -> 
     Java -> 
       Build Path -> 
         Classpath Vairables 

and make a new variable with name 'MAVEN_REPO' where directory is the maven-repository.
Under linux, this is ~/.maven/repository, under windows this is in the equivalent of 
"home" directory.

Working with maven
------------------

After you have made changes to the code, you may want to compile it. This can be accomplished 
by typing 'maven all:install' everytime. But with maven, you can speed this up a little bit. 

Type 'maven console'. This will give a command-line menu of your targets. Type 'all:install'
to compile and install the code. After this, simply use 'enter' to recompile the code.
Use 'quit' to exit and go back. 
