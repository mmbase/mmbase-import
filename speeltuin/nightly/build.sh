#!/bin/bash

#echo Removing old build stuff if there was...
#rm -rf /export/home/nightlybuild/data/build

echo setting PATH, JAVA HOME and ANT HOME
export PATH=/bin:/usr/bin:/usr/local/bin:/usr/local/sbin:/usr/ccs/bin:/home/nightly/bin
export JAVA_HOME=/home/nightly/jdk
export JAVAC=${JAVA_HOME}/bin/javac
#export ANT_HOME=/usr/share/ant
#export ANT_HOME=${HOME}/ant
export CVS="/usr/bin/cvs -d :pserver:guest@cvs.mmbase.org:/var/cvs"
export FILTER="/home/nightly/filterlog"
export BUILD_HOME="/home/nightly"
export CCMAILADDRESS="Michiel.Meeuwissen@omroep.nl"
#export MAILADDRESS="-c ${CCMAILADDRESS} developers@lists.mmbase.org"
#export MAILADDRESS=${CCMAILADDRESS}
export MAILADDRESS="developers@lists.mmbase.org"

# settings
antcommand="/usr/bin/ant -lib /home/nightly/.ant/lib -buildfile nightly-build.xml"
#antcommand="${HOME}/ant/bin/ant --noconfig -buildfile nightly-build.xml"
downloaddir="/home/nightly/download"
optdir="/home/nightly/optional-libs"

echo generating version, and some directories
version=`date '+%Y-%m-%d'`
tag=
#version="MMBase-1.8.1.final"
#tag="MMBase-1_8_1_Final"

# UNSTABLE branch
builddir="/home/nightly/builds/${version}"
mkdir -p ${builddir}

cd ${BUILD_HOME}

#echo cleaning
#${antcommand} clean > ${builddir}/messages.log 2> ${builddir}/errors.log

# update the nightly-build.xml
${CVS} co -p  all/nightly-build.xml >  ${BUILD_HOME}/nightly-build.xml

options="-Ddeprecation=off -Dversion='${version}' -Dtag=${tag} -Ddestination.dir=${builddir} -Ddownload.dir=${downloaddir} -Doptional.lib.dir=${optdir}"
echo "options : ${options}"

echo "Ant Command: ${antcommand} ${options}"
echo Starting nightly build
#${antcommand} ${options} -Dbuild.documentation=false >> ${builddir}/messages.log 2>> ${builddir}/errors.log
${antcommand} ${options} -Dbuild.documentation=true >> ${builddir}/messages.log 2>> ${builddir}/errors.log
cd nightly-build/cvs/mmbase
${CVS} log -N -d"last week<now" 2> /dev/null | ${FILTER} > ${builddir}/RECENTCHANGES.txt


echo Creating sym for latest build
rm /home/nightly/builds/latest
cd /home/nightly/builds
ln -s ${version} latest


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


