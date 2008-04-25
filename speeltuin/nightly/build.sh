#!/bin/bash

echo setting PATH, JAVA HOME
export PATH=/bin:/usr/bin:/usr/local/bin:/usr/local/sbin:/usr/ccs/bin:/home/nightly/bin

echo $HOME

export BUILD_HOME="/home/nightly"

export JAVA_HOME=/home/nightly/jdk
export JAVAC=${JAVA_HOME}/bin/javac

export MAVEN="/home/nightly/maven/bin/maven"
export CVS="/usr/bin/cvs -d :pserver:guest@cvs.mmbase.org:/var/cvs"

export FILTER="/home/nightly/bin/filterlog"


export CCMAILADDRESS="nico@klasens.net"
#export CCMAILADDRESS="Michiel.Meeuwissen@gmail.com"
#export MAILADDRESS="-c ${CCMAILADDRESS} developers@lists.mmbase.org"
export MAILADDRESS=${CCMAILADDRESS}
#export MAILADDRESS="developers@lists.mmbase.org"

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

echo cwd: `pwd`, build dir: ${builddir}

echo Cleaning
echo >  ${builddir}/messages.log 2> ${builddir}/errors.log
# removes all 'target' directories 
# the same as ${MAVEN} multiproject:clean >>  ${builddir}/messages.log 2>> ${builddir}/errors.log
find . -type d -name target -print|xargs rm -rf 

echo ${CVS} -q update -d -P -D "'"${cvsversion}"'"
${CVS} -q update -d -P -D "${cvsversion}"  >>  ${builddir}/messages.log 2>> ${builddir}/errors.log


echo Starting nightly build
echo all:install
${MAVEN} all:install >>  ${builddir}/messages.log 2>> ${builddir}/errors.log

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

if [ 1 == 1 ] ; then
    if [ -f latest/messages.log ] ; then
        if (( `cat latest/messages.log  | grep 'FAILED' | wc -l` > 0 )) ; then
        echo Build failed, sending mail to ${MAILADDRESS}
        echo -e "No build on ${version}\n\nPerhaps the build failed:\n\n" | \
            tail -q -n 20 - latest/messages.log last/errors.log | \
            mutt -s "Build failed ${version}" ${MAILADDRESS}
        fi
    else
        echo Build failed, sending mail to ${MAILADDRESS}
        echo -e "No build created on ${version}\n\n" | \
            tail -q -n 20 - last/errors.log | \
            mutt -s "Build failed ${version}" ${MAILADDRESS}
    fi
fi



if [ 1 == 0 ] ; then 
    echo running tests

    if [ -f latest/tests-results.log ] ; then 
	if (( `cat latest/tests-results.log  | grep 'FAILURES' | wc -l` > 0 )) ; then  
	    echo Failures, sending mail to ${MAILADDRESS}
	    cat latest/tests-results.log  | grep -E -A 1 '(FAILURES|^run\.)' | \
		mutt -s "Test cases failures on build ${version}" ${MAILADDRESS}
	fi
    else
	echo Build failed, sending mail to ${MAILADDRESS}
	echo -e "No test-cases available on build ${version}\n\nPerhaps the build failed:\n\n" | \
	    tail -q -n 20 - latest/messages.log last/errors.log | \
	    mutt -s "Build failed ${version}" ${MAILADDRESS}
    fi
fi


