#!/bin/sh
#
# $Id: tomcat_mmbase.sh,v 1.2 2001-03-02 22:10:42 gerard Exp $
# 
# Changed by GvE for starting MMBase
# 
# Tested on Tomcat 3.2
#

# Shell script to start and stop the server

#
# MMBase config
#

# change this to your MMBase-dir
MMBASE_HOME=/usr/local/java/mmbase
# change this to your Tomcat directory
TOMCAT_HOME=/usr/local/java/apache/jakarta-tomcat
# change this to your mmbase-config dir
MMBASE_CONFIG=$MMBASE_HOME/config/default
# change this to your JDBC-driver
JDBC_CLASSPATH=/usr/local/lib/java/mysql.jar

#####################################
#
# Below this line nothing has to change if you're using the default distribution
# Only if you're a programmer and know what you're doing, go on..... :)
#
#

# change this to your mmbase path where the classes are or to the mmbase.jar
# you only have to change this if you're gonna develop code
MMBASE_LIB=$MMBASE_HOME/lib
# change this to the location of the mmbase-html's (must be under webapps in Tomcat)
MMBASE_HTML=webapps/ROOT
# change this to the location where you'd like to see the mmbase.log
MMBASE_LOG=$MMBASE_HOME/log/mmbase.log

if [ -f $HOME/.tomcatrc ] ; then 
  . $HOME/.tomcatrc
fi

if [ "$TOMCAT_HOME" = "" ] ; then
  ## resolve links - $0 may be a link to  home
  PRG=$0
  progname=`basename $0`
  
  while [ -h "$PRG" ] ; do
    ls=`ls -ld "$PRG"`
    link=`expr "$ls" : '.*-> \(.*\)$'`
    if expr "$link" : '.*/.*' > /dev/null; then
	PRG="$link"
    else
	PRG="`dirname $PRG`/$link"
    fi
  done
  
  TOMCAT_HOME_1=`dirname "$PRG"`/..
  echo "Guessing TOMCAT_HOME from tomcat.sh to ${TOMCAT_HOME_1}" 
    if [ -d ${TOMCAT_HOME_1}/conf ] ; then 
	TOMCAT_HOME=${TOMCAT_HOME_1}
	echo "Setting TOMCAT_HOME to $TOMCAT_HOME"
    fi
fi


if [ "$TOMCAT_HOME" = "" ] ; then
  # try to find tomcat
  if [ -d ${HOME}/opt/tomcat/conf ] ; then 
    TOMCAT_HOME=${HOME}/opt/tomcat
    echo "Defaulting TOMCAT_HOME to $TOMCAT_HOME"
  fi

  if [ -d /opt/tomcat/conf ] ; then 
    TOMCAT_HOME=/opt/tomcat
    echo "Defaulting TOMCAT_HOME to $TOMCAT_HOME"
  fi
 
  # Add other "standard" locations for tomcat
fi

if [ "$TOMCAT_HOME" = "" ] ; then
    echo TOMCAT_HOME not set, you need to set it or install in a standard location
    exit 1
fi

if [ "$TOMCAT_OPTS" = "" ] ; then
  TOMCAT_OPTS=""
fi

if [ "$ANT_OPTS" = "" ] ; then
  ANT_OPTS=""
fi

if [ "$JSPC_OPTS" = "" ] ; then
  JSPC_OPTS=""
fi

if [ -z "$JAVA_HOME" ] ;  then
  JAVA=`which java`
  if [ -z "$JAVA" ] ; then
    echo "Cannot find JAVA. Please set your PATH."
    exit 1
  fi
  JAVA_BINDIR=`dirname $JAVA`
  JAVA_HOME=$JAVA_BINDIR/..
fi

if [ "$JAVACMD" = "" ] ; then 
   # it may be defined in env - including flags!!
   JAVACMD=$JAVA_HOME/bin/java
fi


oldCP=$CLASSPATH
 
CLASSPATH=.
for i in ${TOMCAT_HOME}/lib/* ; do
  CLASSPATH=${CLASSPATH}:$i
done

if [ -f ${JAVA_HOME}/lib/tools.jar ] ; then
   # We are probably in a JDK1.2 environment
   CLASSPATH=${CLASSPATH}:${JAVA_HOME}/lib/tools.jar
fi

# Backdoor classpath setting for development purposes when all classes
# are compiled into a /classes dir and are not yet jarred.
if [ -d ${TOMCAT_HOME}/classes ]; then
    CLASSPATH=${TOMCAT_HOME}/classes:${CLASSPATH}
fi

if [ "$oldCP" != "" ]; then
    CLASSPATH=${CLASSPATH}:${oldCP}
fi

# adding MMBASE libs to classpath
for i in ${MMBASE_LIB}/* ; do
  CLASSPATH=$i:${CLASSPATH}
done

CLASSPATH=${JDBC_CLASSPATH}:${CLASSPATH}

export CLASSPATH

# We start the server up in the background for a couple of reasons:
#   1) It frees up your command window
#   2) You should use `stop` option instead of ^C to bring down the server
if [ "$1" = "start" ] ; then 
  shift 
  echo Using classpath: ${CLASSPATH}
  if [ "$1" = "-security" ] ; then
    echo Starting with a SecurityManager
    $JAVACMD $TOMCAT_OPTS -Djava.security.manager -Djava.security.policy==${TOMCAT_HOME}/conf/tomcat.policy -Dtomcat.home=${TOMCAT_HOME} -Dmmbase.config=${MMBASE_CONFIG} -Dmmbase.htmlroot=${TOMCAT_HOME}/${MMBASE_HTML} -Dmmbase.outputfile=${MMBASE_LOG} org.apache.tomcat.startup.Tomcat "$@" &
  else
  $JAVACMD $TOMCAT_OPTS -Dtomcat.home=${TOMCAT_HOME} -Dmmbase.config=${MMBASE_CONFIG} -Dmmbase.htmlroot=${TOMCAT_HOME}/${MMBASE_HTML} -Dmmbase.outputfile=${MMBASE_LOG} org.apache.tomcat.startup.Tomcat "$@" &
  fi
#   $JAVACMD org.apache.tomcat.shell.Startup "$@" &

elif [ "$1" = "stop" ] ; then 
  shift 
  echo Using classpath: ${CLASSPATH}
  $JAVACMD $TOMCAT_OPTS -Dtomcat.home=${TOMCAT_HOME} org.apache.tomcat.startup.Tomcat -stop "$@"
#   $JAVACMD org.apache.tomcat.shell.Shutdown "$@"

elif [ "$1" = "run" ] ; then 
  shift 
  echo Using classpath: ${CLASSPATH}
  if [ "$1" = "-security" ] ; then
    echo Starting with a SecurityManager
    $JAVACMD $TOMCAT_OPTS -Djava.security.manager -Djava.security.policy==${TOMCAT_HOME}/conf/tomcat.policy -Dtomcat.home=${TOMCAT_HOME} -Dmmbase.config=${MMBASE_CONFIG} -Dmmbase.htmlroot=${TOMCAT_HOME}/${MMBASE_HTML} -Dmmbase.outputfile=${MMBASE_LOG} org.apache.tomcat.startup.Tomcat "$@"
  else
  $JAVACMD $TOMCAT_OPTS -Dtomcat.home=${TOMCAT_HOME} -Dmmbase.config=${MMBASE_CONFIG} -Dmmbase.htmlroot=${TOMCAT_HOME}/${MMBASE_HTML} -Dmmbase.outputfile=${MMBASE_LOG} org.apache.tomcat.startup.Tomcat "$@" 
  fi
#  $JAVACMD org.apache.tomcat.shell.Startup "$@" 
  # no &

elif [ "$1" = "ant" ] ; then 
  shift 

  $JAVACMD $ANT_OPTS -Dant.home=${TOMCAT_HOME} -Dtomcat.home=${TOMCAT_HOME} org.apache.tools.ant.Main $@

elif [ "$1" = "jspc" ] ; then 
  shift 

  $JAVACMD $JSPC_OPTS -Dtomcat.home=${TOMCAT_HOME} org.apache.jasper.JspC "$@"

elif [ "$1" = "env" ] ; then 
  ## Call it with source tomcat.sh to set the env for tomcat
  shift 
  echo Setting classpath to: ${CLASSPATH}
  oldCP=$CLASSPATH

else
  echo "Usage:"
  echo "tomcat (start|env|run|stop|ant)"
  echo "        start - start tomcat in the background"
  echo "        run   - start tomcat in the foreground"
  echo "              -security - use a SecurityManager when starting"
  echo "        stop  - stop tomcat"
  echo "        env  -  set CLASSPATH and TOMCAT_HOME env. variables"
  echo "        ant  - run ant script in tomcat context ( classes, directories, etc)"
  echo "        jspc - run jsp pre compiler"

  exit 0
fi


if [ "$oldCP" != "" ]; then
    CLASSPATH=${oldCP}
    export CLASSPATH
else
    unset CLASSPATH
fi




























