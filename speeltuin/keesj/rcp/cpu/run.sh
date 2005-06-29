#!/bin/sh
CLASSPATH=build
for i in lib/*.jar
do
 CLASSPATH=$i:$CLASSPATH
done
export CLASSPATH
rm -rf build
mkdir -p build
javac -d build src/java/*/*.java
cp  src/java/stats/*.jfrm build/stats/
java stats.Stats
