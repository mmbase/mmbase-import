#! /bin/sh
#
# $Id: shutdown_tomcat_mmbase.sh,v 1.1 2000-10-28 23:16:56 gerard Exp $

# Shell script to shutdown the server

# There are other, simpler commands to shutdown the runner. The two
# commented commands good replacements. The first works well with
# Java Platform 1.1 based runtimes. The second works well with
# Java2 Platform based runtimes.

#jre -cp runner.jar:servlet.jar:classes org.apache.tomcat.shell.Shutdown $*
#java -cp runner.jar:servlet.jar:classes org.apache.tomcat.shell.Shutdown $*

BASEDIR=`dirname $0`

$BASEDIR/tomcat_mmbase.sh stop "$@"
