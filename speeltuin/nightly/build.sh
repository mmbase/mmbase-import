#!/bin/bash

source $HOME/bin/env.sh
source $HOME/bin/version.sh

# UNSTABLE branch

if [ 1 == 1 ] ; then
    cd ${BUILD_HOME}/nightly-build/cvs/mmbase

    echo cwd: `pwd`, build dir: ${builddir}

    echo Cleaning
    echo >  ${builddir}/messages.log 2> ${builddir}/errors.log
    # removes all 'target' directories
    # the same as ${MAVEN} multiproject:clean >>  ${builddir}/messages.log 2>> ${builddir}/errors.log
    find . -type d -name target -print | xargs rm -rf  >> ${builddir}/messages.log

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

    echo ====================================================================== |  tee -a ${builddir}/messages.log
    echo creating RECENTCHANGES |  tee -a ${builddir}/messages.log
    ${CVS} log -N -d"last week<now" 2> /dev/null | ${FILTER} > ${builddir}/RECENTCHANGES.txt

    echo ==================================MAVEN 2 EXPERIMENTAL============== |  tee -a ${builddir}/messages.log
    cd ${BUILD_HOME}/nightly-build/cvs/mmbase
    (cd maven; ${MAVEN2} clean source:jar deploy) | tee -a ${builddir}/messages.log
fi

if [ 1 == 1 ] ; then
    cd maven-site
    echo Creating site `pwd`. | tee -a ${builddir}/messages.log
    ((${MAVEN} multiproject:site | tee -a ${builddir}/messages.log) 3>&1 1>&2 2>&3 | tee -a ${builddir}/errors.log) 3>&1 1>&2 2>&3
fi


$HOME/bin/copy-artifacts.sh


if [ 1 == 1 ] ; then
    echo Now executing tests. Results in ${builddir}/test-results.log | tee -a ${builddir}/messages.log
    cd ${BUILD_HOME}/nightly-build/cvs/mmbase/tests
    ${antcommand} -quiet -listener org.apache.tools.ant.listener.Log4jListener -lib lib:.  run.all  2>&1 | tee  ${builddir}/tests-results.log
fi


echo Creating symlink for latest build | tee -a ${builddir}/messages.log
rm $HOME/builds/latest
cd $HOME/builds
echo 'ln -s ${dir} latest' in `pwd` | tee -a ${builddir}/messages.log
ln -s ${dir} latest

$HOME/bin/mail-results.sh