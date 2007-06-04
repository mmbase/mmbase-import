#!/bin/bash

#echo Removing old build stuff if there was...
#rm -rf /export/home/nightlybuild/data/build

echo setting PATH, JAVA HOME
export PATH=/bin:/usr/bin:/usr/local/bin:/usr/local/sbin:/usr/ccs/bin:/home/nightly/bin

echo $HOME

export BUILD_HOME="/home/nightly"

export JAVA_HOME=/home/nightly/jdk
export JAVAC=${JAVA_HOME}/bin/javac

export MAVEN="/home/nightly/maven/bin/maven"
export CVS="/usr/bin/cvs -d :pserver:guest@cvs.mmbase.org:/var/cvs"

export FILTER="/home/nightly/filterlog"


export CCMAILADDRESS="Michiel.Meeuwissen@omroep.nl"
#export MAILADDRESS="-c ${CCMAILADDRESS} developers@lists.mmbase.org"
#export MAILADDRESS=${CCMAILADDRESS}
export MAILADDRESS="developers@lists.mmbase.org"

downloaddir="/home/nightly/download"
optdir="/home/nightly/optional-libs"

echo generating version, and some directories

version=`date '+%Y-%m-%d'`

cvsversion=`date '+%Y-%m-%d %H:%M'`
dir=${version}

#version="MMBase-1.8.1.final"
#tag="MMBase-1_8_1_Final"

# UNSTABLE branch
builddir="/home/nightly/builds/${dir}"
mkdir -p ${builddir}

cd ${BUILD_HOME}/nightly-build/cvs/mmbase

echo Cleaning
${MAVEN} multiproject:clean >  ${builddir}/messages.log 2> ${builddir}/errors.log
${MAVEN} clean:clean >  ${builddir}/messages.log 2> ${builddir}/errors.log

echo ${CVS} update -d -P -D "'"${cvsversion}"'"
${CVS} update -d -P -D "'"${cvsversion}"'"  >>  ${builddir}/messages.log 2>> ${builddir}/errors.log

echo Starting nightly build
echo jar:install-snapshot
${MAVEN} jar:install-snapshot >>  ${builddir}/messages.log 2>> ${builddir}/errors.log
echo all:install-snapshot
${MAVEN} all:install-snapshot >>  ${builddir}/messages.log 2>> ${builddir}/errors.log

${CVS} log -N -d"last week<now" 2> /dev/null | ${FILTER} > ${builddir}/RECENTCHANGES.txt

cd maven-site
echo Creating site `pwd`.
${MAVEN} multiproject:site >> ${builddir}/messages.log 2>> ${builddir}/errors.log


echo Copying todays artifacts
cp -ra $HOME/.maven/repository/mmbase/mmbase-modules/*SNAPSHOT* ${builddir}


echo Creating sym for latest build
rm /home/nightly/builds/latest
cd /home/nightly/builds
ln -s ${dir} latest


if [ 1 == 0 ] ; then 
    if [ -f latest/tests-results.log ] ; then 
	if (( `cat latest/tests-results.log  | grep 'FAILURES' | wc -l` > 0 )) ; then  
	    echo Failures, sending mail to ${MAILADDRESS}
	    cat latest/tests-results.log  | grep -E -A 1 '(FAILURES|^run\.)' | mutt -s "Test cases failures on build ${version}" ${MAILADDRESS}
	fi
    else
	echo Build failed, sending mail to ${MAILADDRESS}
	echo -e "No test-cases available on build ${version}\n\nPerhaps the build failed:\n\n" | tail -q -n 20 - latest/messages.log last/errors.log | mutt -s "Build failed ${version}" ${MAILADDRESS}
    fi
fi


