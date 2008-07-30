#!/bin/bash


echo setting PATH, JAVA HOME
export PATH=/bin:/usr/bin:/usr/local/bin:/usr/local/sbin:/usr/ccs/bin:/home/nightly/bin

echo $HOME

export BUILD_HOME="/home/nightly"

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
    echo ${CVS} -q  update -d -P  ${cvsversionoption} ${cvsversion} ${revision} | tee -a ${builddir}/messages.log

    # I realy don't get the deal with the quotes around ${cvsversion}.
    # undoubtly to do with some bash detail. If $cvsversion contains no space, then it seems essential that these quotes are _not_ there
    # otherwise it seems essential _that_ they are. It's maddening.
    ${CVS} -q update -d -P  ${cvsversionoption} "${cvsversion}"  ${revision} | tee -a ${builddir}/messages.log


    echo Starting nightly build | tee -a ${builddir}/messages.log
    echo all:install
    ((${MAVEN} all:install | tee -a ${builddir}/messages.log) 3>&1 1>&2 2>&3 | tee -a ${builddir}/errors.log) 3>&1 1>&2 2>&3

    ${CVS} log -N -d"last week<now" 2> /dev/null | ${FILTER} > ${builddir}/RECENTCHANGES.txt
fi

if [ 1 == 1 ] ; then
    cd maven-site
    echo Creating site `pwd`. | tee -a ${builddir}/messages.log
    ((${MAVEN} multiproject:site | tee -a ${builddir}/messages.log) 3>&1 1>&2 2>&3 | tee -a ${builddir}/errors.log) 3>&1 1>&2 2>&3
fi

echo Copying todays artifacts | tee -a ${builddir}/messages.log
echo $HOME
for i in `/usr/bin/find $HOME/.maven/repository/mmbase -mtime -1` ; do
    #echo copy $i to ${builddir} | tee -a ${builddir}/messages.log
    cp $i ${builddir}
done


if [ 1 == 1 ] ; then
    echo Now executing tests. Results in ${builder}/test-results. | tee -a ${builddir}/messages.log
    cd ${BUILD_HOME}/nightly-build/cvs/mmbase/tests
    # Ant sucks incredibly. This classapth should not be necessary, but really, it is.
    export CLASSPATH=${BUILD_HOME}/.ant/lib/ant-apache-log4j.jar:${BUILD_HOME}/.ant/lib/log4j-1.2.13.jar
    ${antcommand} -quiet -listener org.apache.tools.ant.listener.Log4jListener -lib lib:.  run.all  2>&1 | tee  ${builddir}/tests-results.log
fi


echo Creating symlink for latest build | tee -a ${builddir}/messages.log
rm /home/nightly/builds/latest
cd /home/nightly/builds
ln -s ${dir} latest

showtests=1
if [ 1 == 1 ] ; then
    if [ -f latest/messages.log ] ; then
        if (( `cat latest/messages.log  | grep 'FAILED' | wc -l` > 0 )) ; then
	    echo Build failed, sending mail to ${BUILD_MAILADDRESS} | tee -a ${builddir}/messages.log
	    echo -e "Build on ${version} failed:\n\n" | \
		cat latest/messages.log latest/errors.log | grep -B 10 "FAILED" | \
		mutt -s "Build failed ${version}" ${BUILD_MAILADDRESS}
	    showtests=0;
        fi
    else
        echo Build failed, sending mail to ${BUILD_MAILADDRESS} | tee -a ${builddir}/messages.log
        echo -e "No build created on ${version}\n\n" | \
            tail -q -n 20 - latest/errors.log | \
            mutt -s "Build failed ${version}" ${BUILD_MAILADDRESS}
	showtests=0;
    fi
fi



if [ 1 == $showtests ] ; then
    cd /home/nightly/builds
    echo Test results | tee -a ${builddir}/messages.log

    if [ -f latest/tests-results.log ] ; then
	if (( `cat latest/tests-results.log  | grep 'FAILURES' | wc -l` > 0 )) ; then
	    echo Failures, sending mail to ${MAILADDRESS}  | tee -a ${builddir}/messages.log
	    (echo "See also http://www.mmbase.org/download/builds/latest/tests-results.log" ; \
                cat latest/tests-results.log  | grep -P  '(^Tests run:|^[0-9]+\)|^\tat org\.mmbase|FAILURES|========================|OK)' ) | \
		mutt -s "Test cases failures on build ${version}" ${MAILADDRESS}
	fi
    fi
fi

