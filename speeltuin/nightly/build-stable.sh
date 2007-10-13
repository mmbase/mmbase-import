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
#version=MMBase-1_8_4
#revision=MMBase-1_8_4_Final

# STABLE branch
builddir="/home/nightly/builds/stable/${version}"
mkdir -p ${builddir}

echo cleaning
echo ${antcommand}
#${antcommand} clean > ${builddir}/messages.log 2> ${builddir}/errors.log

cd ${BUILD_HOME}/stable/nightly-build/cvs/mmbase
echo update cvs to `pwd`

for i in '.' 'applications' 'contributions' ; do
    echo updating `pwd`/$i 
    ${CVS} -q update -d -P -l -D "${version}" -r "${revision}" $i >>  ${builddir}/messages.log 2>> ${builddir}/errors.log
done
for i in 'src' 'documentation' 'tests' 'config' 'html' \
    'applications/taglib' 'applications/editwizard' 'applications/dove' 'applications/crontab' 'applications/cloudcontext' \
    'applications/rmmci' 'applications/vwms' 'applications/scan' 'applications/clustering' 'applications/oscache-cache' \
    'applications/largeobjects' 'applications/packaging' \
    'contributations/aselect' 'contributions/mmbar' 'contributions/thememanager' 'contributions/multilanguagegui' \
    ; do 
    echo updating `pwd`/$i 
   ${CVS} -q update -d -P -D "${version}" -r "${revision}" $i >>  ${builddir}/messages.log 2>> ${builddir}/errors.log
done
echo "==========UPDATING TO HEAD========" >> ${builddir}/messages.log
for i in 'applications/email' 'contributions/lucene' 'contributions/mmbob' 'contributions/didactor2' \
    ; do
    echo updating to HEAD `pwd`/$i 
    echo updating to HEAD `pwd`/$i  >> ${builddir}/messages.log    
    ${CVS} -q update -d -P -D "${version}" -A $i >>  ${builddir}/messages.log 2>> ${builddir}/errors.log
done

stableoptions="-Dbuild.documentation=true -Ddestination.dir=${builddir} -Ddownload.dir=${downloaddir}"
echo "options : ${stableoptions}"

echo "Ant Command: ${antcommand} ${stableoptions}"
echo Starting nightly build
${antcommand} ${stableoptions} >> ${builddir}/messages.log 2>> ${builddir}/errors.log


${CVS} log -rMMBase-1_8 -N -d"last week<now" 2> /dev/null | ${FILTER} > ${builddir}/RECENTCHANGES.txt

echo Creating sym for last build
rm /home/nightly/builds/stable/latest

ln -s ${version} latest












