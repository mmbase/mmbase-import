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

#nightly build:
version=`date '+%Y-%m-%d'`
cvsversionoption="-D"
cvsversion="${version} `date '+%H:%M:%S'`"
echo CVS $cvsversionrevision=MMBase-1_8
revision=MMBase-1_8
headrevision="-A"

#release:
#version=MMBase-1_8_6_Final
#Bcvsversion=
#revision=MMBase-1_8_6_Final
#headrevision="-r MMBase-1_8_6_Final"

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

if ( true ) ; then
    for i in '.' 'applications' 'contributions'; do
    echo updating `pwd`/$i using     ${CVS} -q update -d -P -l ${cvsversionoption} ${cvsversion} -r "${revision}"  $i | tee -a ${builddir}/messages.log;
    ${CVS} -q update -d -P -l ${cvsversionoption} "${cvsversion}" -r "${revision}"  $i | tee -a  ${builddir}/messages.log 2>> ${builddir}/errors.log
    done
    for i in 'applications/build.xml' 'contributions/build.xml' 'download.xml' ; do
    echo updating `pwd`/$i to  HEAD  using -l ${cvsversionoption} ${cvsversion} ${headrevision} | tee -a ${builddir}/messages.log 2>> ${builddir}/errors.log
    ${CVS} -q update -d -P -l ${cvsversionoption} "${cvsversion}" ${headrevision}  $i | tee -a  ${builddir}/messages.log 2>> ${builddir}/errors.log
    done
    echo "Build from ${revision} ${cvsversionoption} ${cvsversion} against java 1.4 are" > ${builddir}/README
    for i in 'src' 'documentation' 'tests' 'config' 'html' \
	'applications/resources' 'applications/cloudsecurity' 'applications/mynews' 'application/xmlimporter' 'contributions/calendar' \
        'applications/taglib' 'applications/editwizard' 'applications/dove' 'applications/cloudcontext' \
        'applications/rmmci' 'applications/vwms' 'applications/scan' 'applications/clustering' 'applications/oscache-cache' \
        'applications/media' 'applications/packaging' 'applications/community' 'applications/largeobjects' \
        'contributions/aselect' 'contributions/mmbar'  'contributions/multilanguagegui' \
        'contributions/principletracker' \
    ; do
      echo updating `pwd`/$i | tee -a ${builddir}/messages.log
      echo $i >> ${builddir}/README
      ${CVS} -q update -d -P ${cvsversionoption} "${cvsversion}" -r "${revision}" $i | tee  -a  ${builddir}/messages.log 2>> ${builddir}/errors.log
    done
    echo "==========UPDATING TO HEAD========" >> ${builddir}/messages.log
    echo "Build from HEAD ${cvsversionoption} ${cvsversion} against java 1.5 are" >> ${builddir}/README
    for i in 'applications/email' 'contributions/lucene' 'contributions/mmbob' 'contributions/thememanager' 'contributions/didactor2' 'applications/richtext' \
        'applications/jumpers' 'applications/commandserver' 'applications/notifications' 'applications/crontab' 'contributions/poll' 'contributions/calendar' \
    ; do
    echo updating to HEAD `pwd`/$i | tee -a   ${builddir}/messages.log
    echo $i >> ${builddir}/README
    ${CVS} -q update -d -P ${cvsversionoption} "${cvsversion}" ${headrevision} $i | tee -a   ${builddir}/messages.log 2>> ${builddir}/errors.log
    done
fi
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

