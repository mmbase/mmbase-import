#!/bin/bash

#echo Removing old build stuff if there was...
#rm -rf /export/home/nightlybuild/data/build

echo setting PATH, JAVA HOME and ANT HOME
export PATH=/bin:/usr/bin:/usr/local/bin:/usr/local/sbin:/usr/ccs/bin:/home/nightly/bin:/usr/local/ant/bin
export JAVA_HOME=/usr/java/jdk
export ANT_HOME=/usr/ant
export CVS="/usr/bin/cvs -d :pserver:guest@cvs.mmbase.org:/var/cvs"
export FILTER="/home/nightly/filterlog"
export BUILD_HOME="/home/nightly"

# settings
antcommand="/usr/bin/ant -buildfile nightly-build.xml"
downloaddir="/home/nightly/download"
optdir="/home/nightly/optional-libs"

echo generating version, and some directories

#nightly build:
version=`date '+%Y-%m-%d'`
revision=MMBase-1_8

#release:
#version=MMBase-1_8_3
#revision=MMBase-1_8_3_Final

# STABLE branch
stablebuilddir="/home/nightly/builds/stable/${version}"
mkdir -p ${stablebuilddir}

cd /home/nightly/stable
echo cleaning
echo ${antcommand}
${antcommand} clean > ${stablebuilddir}/messages.log 2> ${stablebuilddir}/errors.log

echo update the nightly-build.xml
${CVS}  co -r MMBase-1_8 -p all/nightly-build.xml >  /home/nightly/stable/nightly-build.xml

stableoptions="-Dtag=${revision} -Dbuild.documentation=true -Dversion=${version} -Ddestination.dir=${stablebuilddir} -Ddownload.dir=${downloaddir}"
echo "options : ${stableoptions}"

echo "Ant Command: ${antcommand} ${stableoptions}"
echo Starting nightly build
${antcommand} ${stableoptions} >> ${stablebuilddir}/messages.log 2>> ${stablebuilddir}/errors.log

cd nightly-build/cvs/mmbase
${CVS} log -rMMBase-1_8 -N -d"last week<now" 2> /dev/null | ${FILTER} > ${stablebuilddir}/RECENTCHANGES.txt

echo Creating sym for last build
rm /home/nightly/builds/stable/latest
cd /home/nightly/builds/stable

ln -s ${version} latest












