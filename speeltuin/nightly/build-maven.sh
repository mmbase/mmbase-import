#!/bin/bash


echo setting PATH, JAVA HOME
export PATH=/home/nightly/jdk/bin:/bin:/usr/bin:/usr/local/bin:/usr/local/sbin:/usr/ccs/bin:/home/nightly/bin

echo $HOME

export BUILD_HOME="/home/nightly/"

export JAVA_HOME=/home/nightly/jdk
export JAVAC=${JAVA_HOME}/bin/javac

export MAVEN_OPTS=-Xmx512m
export MAVEN="/home/nightly/maven/bin/maven --nobanner --quiet"
export CVS="/usr/bin/cvs -d :pserver:guest@cvs.mmbase.org:/var/cvs"
export ANT_HOME=/usr/ant
antcommand="/usr/bin/ant"

export FILTER="/home/nightly/bin/filterlog"


export MAILADDRESS="developers@lists.mmbase.org"
#export MAILADDRESS="michiel.meeuwissen@gmail.com"
export BUILD_MAILADDRESS=$MAILADDRESS

echo generating version, and some directories


source $HOME/bin/version-stable.sh
source $HOME/bin/cvs-stable.sh

# UNSTABLE branch

if [ 1 == 1 ] ; then
    cd ${BUILD_HOME}/stable/nightly-build/cvs/mmbase

    echo cwd: `pwd`, build dir: ${builddir}

    echo Cleaning
    echo >  ${builddir}/messages-maven.log 2> ${builddir}/errors-maven.log

    find . -type d -name target -print | xargs rm -rf  >> ${builddir}/messages-maven.log

    pwd

    echo `java -version`

    echo Starting nightly build | tee -a ${builddir}/messages-maven.log
    echo ${MAVEN} all:install
    ((${MAVEN} all:install | tee -a ${builddir}/messages-maven.log) 3>&1 1>&2 2>&3 | tee -a ${builddir}/errors.log) 3>&1 1>&2 2>&3
fi

