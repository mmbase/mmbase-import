#!/bin/bash


#echo Removing old build stuff if there was...
#rm -rf /export/home/nightlybuild/data/build

echo setting PATH, JAVA HOME and ANT HOME
export PATH=/bin:/usr/bin:/usr/local/bin:/usr/local/sbin:/usr/ccs/bin:/home/nightly/bin:/usr/local/ant/bin

## java 1.4
#export JAVA_HOME=/usr/java/jdk

#$ java 1.5
## We build with java 1.5 nowadays.
export JAVA_HOME=/home/nightly/jdk
export JAVAC=${JAVA_HOME}/bin/javac

export ANT_HOME=/usr/ant
export CVS="/usr/bin/cvs -d :pserver:guest@cvs.mmbase.org:/var/cvs"
export FILTER="/home/nightly/filterlog"
export BUILD_HOME="/home/nightly"

# settings
antcommand="/usr/bin/ant"
downloaddir="/home/nightly/download"
optdir="/home/nightly/optional-libs"

echo generating version, and some directories

#nightly build:
version=`date '+%Y-%m-%d'`
cvsversion="${version} `date '+%H:%M:%S'`"
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

rm -rf ${builddir}/*
echo update cvs to `pwd`  >  ${builddir}/messages.log 2> ${builddir}/errors.log

if ( true ) ; then 
    for i in '.' 'applications' 'contributions' ; do
	echo updating `pwd`/$i 
	${CVS} -q update -d -P -l -D "${cvsversion}" -r "${revision}" $i >>  ${builddir}/messages.log 2>> ${builddir}/errors.log
    done
    for i in 'src' 'documentation' 'tests' 'config' 'html' \
	'applications/taglib' 'applications/editwizard' 'applications/dove' 'applications/crontab' 'applications/cloudcontext' \
	'applications/rmmci' 'applications/vwms' 'applications/scan' 'applications/clustering' 'applications/oscache-cache' \
	'applications/largeobjects' 'applications/packaging' \
	'contributions/aselect' 'contributions/mmbar' 'contributions/thememanager' 'contributions/multilanguagegui' \
	; do 
      echo updating `pwd`/$i 
      ${CVS} -q update -d -P -D "${cvsversion}" -r "${revision}" $i >>  ${builddir}/messages.log 2>> ${builddir}/errors.log
    done
    echo "==========UPDATING TO HEAD========" >> ${builddir}/messages.log
    for i in 'applications/email' 'contributions/lucene' 'contributions/mmbob' 'contributions/didactor2' 'applications/richtext' \
	; do
      echo updating to HEAD `pwd`/$i 
      echo updating to HEAD `pwd`/$i  >> ${builddir}/messages.log    
      ${CVS} -q update -d -P -D "${cvsversion}" -A $i >>  ${builddir}/messages.log 2>> ${builddir}/errors.log
    done

fi
stableoptions="-Doptional.lib.dir=${optdir} -Dbuild.documentation=false -Ddestination.dir=${builddir} -Ddownload.dir=${downloaddir}"
echo "options : ${stableoptions}"

echo "Ant Command: ${antcommand} ${stableoptions}  " 

if ( true ) ; then 
    echo cleaning
    find . -name build | xargs rm -r
    
    echo "Starting nightly build" + `pwd`
    ${antcommand} bindist ${stableoptions} >> ${builddir}/messages.log 2>> ${builddir}/errors.log
    cd applications
    ${antcommand} all ${stableoptions} >> ${builddir}/messages.log 2>> ${builddir}/errors.log
    cd ../contributions
    ${antcommand} all ${stableoptions} >> ${builddir}/messages.log 2>> ${builddir}/errors.log
    cd ..
    ${antcommand} srcdist ${stableoptions} >> ${builddir}/messages.log 2>> ${builddir}/errors.log
    ${antcommand} war ${stableoptions} >> ${builddir}/messages.log 2>> ${builddir}/errors.log
    
    ${CVS} log -rMMBase-1_8 -N -d"last week<now" 2> /dev/null | ${FILTER} > ${builddir}/RECENTCHANGES.txt
fi

for i in `find . -regex ".*/mmbase-.*\.zip"` ; do
    cp  -a $i ${builddir}
done

for i in `find . -regex ".*/mmbase.*\.war"` ; do
    cp  -a $i ${builddir}
done

echo Creating sym for last build
latest=/home/nightly/builds/stable/latest
rm -f ${latest}

ln -s ${version} ${latest}

