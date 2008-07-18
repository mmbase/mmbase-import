#!/bin/bash

echo setting PATH, JAVA HOME
export PATH=/bin:/usr/bin:/usr/local/bin:/usr/local/sbin:/usr/ccs/bin:/home/nightly/bin

echo $HOME

export BUILD_HOME="/home/nightly"

export JAVA_HOME=/home/nightly/jdk
export JAVAC=${JAVA_HOME}/bin/javac

export MAVEN="/home/nightly/maven/bin/maven"
export CVS="/usr/bin/cvs -d :pserver:guest@cvs.mmbase.org:/var/cvs"
export ANT_HOME=/usr/ant
antcommand="/usr/bin/ant"

export FILTER="/home/nightly/bin/filterlog"


export CCMAILADDRESS="nico@klasens.net"
#export CCMAILADDRESS="Michiel.Meeuwissen@gmail.com"
#export MAILADDRESS="-c ${CCMAILADDRESS} developers@lists.mmbase.org"
export MAILADDRESS=${CCMAILADDRESS}
#export MAILADDRESS="developers@lists.mmbase.org"

echo generating version, and some directories

version=`date -u '+%Y-%m-%d'`
cvsversionoption="-D"
cvsversion=`date  '+%Y-%m-%d %H:%M'`
revision="-A"


#version="MMBase-1.9.0.beta2"
#cvsversion=
#cvsversionoption="-r"
#revision="MMBase-1_9_0_beta2"

dir=${version}

# UNSTABLE branch
builddir="/home/nightly/builds/${dir}"
mkdir -p ${builddir}

if [ 1 == 1 ] ; then 
    cd ${BUILD_HOME}/nightly-build/cvs/mmbase
    
    echo cwd: `pwd`, build dir: ${builddir}
    
    echo Cleaning
    echo >  ${builddir}/messages.log 2> ${builddir}/errors.log
# removes all 'target' directories 
# the same as ${MAVEN} multiproject:clean >>  ${builddir}/messages.log 2>> ${builddir}/errors.log
    find . -type d -name target -print | xargs rm -rf 

    pwd
    echo "CVS" | tee -a ${builddir}/messages.log
    echo ${CVS} update -d -P  ${cvsversionoption} ${cvsversion} ${revision} | tee -a ${builddir}/messages.log
    ${CVS} update -d -P  ${cvsversionoption} "${cvsversion}"  ${revision} | tee -a ${builddir}/messages.log
    
    
    echo Starting nightly build
    echo all:install
    ${MAVEN} all:install >>  ${builddir}/messages.log 2>> ${builddir}/errors.log
    
    ${CVS} log -N -d"last week<now" 2> /dev/null | ${FILTER} > ${builddir}/RECENTCHANGES.txt

    cd maven-site
    echo Creating site `pwd`.
    ${MAVEN} multiproject:site >> ${builddir}/messages.log 2>> ${builddir}/errors.log
fi

echo Copying todays artifacts
echo $HOME
for i in `/usr/bin/find $HOME/.maven/repository/mmbase -mtime -1` ; do 
    echo copy $i to ${builddir}
    cp $i ${builddir} 
done


if [ 1 == 1 ] ; then
    echo Now executing tests
    cd ${BUILD_HOME}/nightly-build/cvs/mmbase/tests
    ${antcommand} run.all > ${buildir}/tests-results.log
fi


echo Creating symlink for latest build
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



if [ 1 == 1 ] ; then 
    echo running tests

    if [ -f latest/tests-results.log ] ; then 
	if (( `cat latest/tests-results.log  | grep 'FAILURES' | wc -l` > 0 )) ; then  
	    echo Failures, sending mail to ${MAILADDRESS}
	    cat latest/tests-results.log  | grep -E -A 1 '(FAILURES|^run\.)' | \
		mutt -s "Test cases failures on build ${version}" Michiel.Meeuwissen@gmail.com
	fi
    else
	echo Build failed, sending mail to ${MAILADDRESS}
	echo -e "No test-cases available on build ${version}\n\nPerhaps the build failed:\n\n" | \
	    tail -q -n 20 - latest/messages.log last/errors.log | \
	    mutt -s "Build failed ${version}"  Michiel.Meeuwissen@gmail.com
    fi
fi


