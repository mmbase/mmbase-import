#!/bin/bash


#echo Removing old build stuff if there was...
#rm -rf /export/home/nightlybuild/data/build

echo setting PATH, JAVA HOME and ANT HOME
export PATH=/bin:/usr/bin:/usr/local/bin:/usr/local/sbin:/usr/ccs/bin:/home/nightly/bin:/usr/local/ant/bin

## java 1.4
JAVA_HOME14=/usr/java/jdk
JAVAC14=${JAVA_HOME14}/bin/javac

#$ java 1.5
JAVA_HOME15=/home/nightly/jdk
JAVAC15=${JAVA_HOME15}/bin/javac

export ANT_HOME=/usr/ant
CVS="/usr/bin/cvs -d :pserver:guest@cvs.mmbase.org:/var/cvs"
FILTER="/home/nightly/nin/filterlog"
BUILD_HOME="/home/nightly"

#I hate ant, I hate java
export CLASSPATH=
for i in ~/.ant/lib/* ; do export CLASSPATH=${CLASSPATH}:$i ; done


# settings
antcommand="/usr/bin/ant"
downloaddir="/home/nightly/download"
optdir="/home/nightly/optional-libs"

echo generating version, and some directories

source version-stable.sh

echo $cvsversion

# STABLE branch
builddir="/home/nightly/builds/stable/${version}"
mkdir -p ${builddir}

me=`cd -P -- "$(dirname -- "$0")" && pwd -P`/$0


echo cleaning $0
echo ${antcommand}
#${antcommand} clean > ${builddir}/messages.log 2> ${builddir}/errors.log

STABLE=${BUILD_HOME}/stable/nightly-build/cvs/mmbase
cd ${STABLE}


rm -rf ${builddir}/*

echo cleaning in ${STABLE} | tee  ${builddir}/messages.log
find ${STABLE} -name build | xargs rm -r


echo update cvs to `pwd`  using -r '${cvsversionoption} ${cvsversion}' | tee -a  ${builddir}/messages.log


cvs-stable.sh

stableoptions="-Doptional.lib.dir=${optdir} -Dbuild.documentation=false -Ddestination.dir=${builddir} -Ddownload.dir=${downloaddir}"
echo "options : ${stableoptions}"

echo "Ant Command: ${antcommand} ${stableoptions}  "

if ( true ) ; then
    echo "Starting nightly build" + `pwd` | tee -a ${builddir}/messages.log

    echo "JAVA 14 from now on" | tee -a ${builddir}/messages.log
    export JAVA_HOME=${JAVA_HOME14}
    export JAVAC=${JAVAC14}
    cd ${STABLE}
    ${antcommand} jar ${stableoptions} >> ${builddir}/messages.log 2>> ${builddir}/errors.log

    echo "JAVA 14 from now on" | tee -a ${builddir}/messages.log
    export JAVA_HOME=${JAVA_HOME14}
    export JAVAC=${JAVAC14}
    echo "BINDIST DOWING NOW" | tee -a ${builddir}/messages.log
    cd ${STABLE}
    ${antcommand} bindist ${stableoptions} >> ${builddir}/messages.log 2>> ${builddir}/errors.log
    if ( true ) ; then
    echo "APPS14 building now" | tee -a ${builddir}/messages.log
    cd ${STABLE}/applications
    ${antcommand} all18_14 ${stableoptions} >> ${builddir}/messages.log 2>> ${builddir}/errors.log
    cd ${STABLE}/contributions
    ${antcommand} all18_14 ${stableoptions} >> ${builddir}/messages.log 2>> ${builddir}/errors.log
    cd ${STABLE}
    ${antcommand} srcdist ${stableoptions} >> ${builddir}/messages.log 2>> ${builddir}/errors.log
    ${antcommand} minimalistic.war ${stableoptions} >> ${builddir}/messages.log 2>> ${builddir}/errors.log

    echo "JAVA 15 from now on" | tee -a ${builddir}/messages.log
    export JAVA_HOME=${JAVA_HOME15}
    export JAVAC=${JAVAC15}

    cd ${STABLE}/applications
    pwd >> ${builddir}/messages.log
    ${antcommand} -Djava.source.version=1.5 all18_15 ${stableoptions} >> ${builddir}/messages.log 2>> ${builddir}/errors.log
    cd ${STABLE}/contributions
    pwd >> ${builddir}/messages.log
    ${antcommand} -Djava.source.version=1.5 all18_15 ${stableoptions} >> ${builddir}/messages.log 2>> ${builddir}/errors.log



    fi
fi

cd ${STABLE}
for i in `find . -regex ".*/mmbase.*\.zip"` ; do
    cp  -a $i ${builddir}
done

for i in `find . -regex ".*/.*\.war"` ; do
    cp  -a $i ${builddir}
done
for i in 'documentation/releases/release-notes.txt' $me ; do
    cp  -a $i ${builddir}
done

echo Creating sym for last build
latest=/home/nightly/builds/stable/latest
rm -f ${latest}

ln -s ${version} ${latest}

