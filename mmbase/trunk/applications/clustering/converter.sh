#!/bin/bash
# The clustering jar contains a class 'Converter' which can be used as a unicast/multicast bridge.
# It is used at the VPRO to connect one 'out lying' server with unicast to the rest of there servers.
#
# This is an example script that only sets up the CLASSPATH correctly and calls this class. For an overview of possible
# parameters call it with an unknown one, e.g. ./converter.sh help.


export CLASSPATH=\
~/.m2/repository/org/mmbase/mmbase-utils/2.0-SNAPSHOT/mmbase-utils-2.0-SNAPSHOT.jar:\
~/.m2/repository/org/mmbase/mmbase-clustering/2.0-SNAPSHOT/mmbase-clustering-2.0-SNAPSHOT-classes.jar

#echo ${CLASSPATH}

java org.mmbase.clustering.Converter $@