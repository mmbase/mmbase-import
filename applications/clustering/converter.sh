#!/bin/bash

export CLASSPATH=\
~/.m2/repository/org/mmbase/mmbase-utils/2.0-SNAPSHOT/mmbase-utils-2.0-SNAPSHOT.jar:\
~/.m2/repository/org/mmbase/mmbase-clustering/2.0-SNAPSHOT/mmbase-clustering-2.0-SNAPSHOT-classes.jar

echo ${CLASSPATH}

java org.mmbase.clustering.Converter $@