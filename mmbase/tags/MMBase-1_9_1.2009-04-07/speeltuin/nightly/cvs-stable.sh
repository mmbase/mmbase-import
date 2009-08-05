
source $HOME/bin/version-stable.sh
export CVS="/usr/bin/cvs -d :pserver:guest@cvs.mmbase.org:/var/cvs"
BUILD_HOME="/home/nightly"

cd ${BUILD_HOME}/stable/nightly-build/cvs/mmbase
for i in '.' 'applications' 'contributions'; do
    echo updating `pwd`/$i using     ${CVS} -q update -d -P -l ${cvsversionoption} ${cvsversion} -r "${revision}"  $i | tee -a ${builddir}/messages.log;
    ${CVS} -q update -d -P -l ${cvsversionoption} "${cvsversion}" -r "${revision}"  $i | tee -a  ${builddir}/messages.log 2>> ${builddir}/errors.log
done
for i in 'applications/build.xml' 'contributions/build.xml' 'download.xml' ; do
    echo updating `pwd`/$i to  HEAD  using -l ${cvsversionoption} ${cvsversion} ${headrevision} | tee -a ${builddir}/messages.log 2>> ${builddir}/errors.log
    ${CVS} -q update -d -P -l ${cvsversionoption} "${cvsversion}" ${headrevision}  $i | tee -a  ${builddir}/messages.log 2>> ${builddir}/errors.log
done
echo "Build from ${revision} ${cvsversionoption} ${cvsversion} against java 1.4 are" > ${builddir}/README
for i in 'src' 'maven-base' 'applications/app-base' 'documentation' 'tests' 'config' 'html' \
    'applications/resources' 'applications/cloudsecurity' 'applications/mynews' 'applications/xmlimporter' \
    'applications/taglib' 'applications/editwizard' 'applications/dove' 'applications/cloudcontext' \
    'applications/rmmci' 'applications/vwms' 'applications/scan' 'applications/clustering' 'applications/oscache-cache' \
    'applications/media' 'applications/packaging' 'applications/community' 'applications/largeobjects' \
    'contributions/aselect' 'contributions/mmbar'  \
    'contributions/principletracker' \
    ; do
    echo updating `pwd`/$i | tee -a ${builddir}/messages.log
    echo $i >> ${builddir}/README
    ${CVS} -q update -d -P ${cvsversionoption} "${cvsversion}" -r "${revision}" $i | tee  -a  ${builddir}/messages.log 2>> ${builddir}/errors.log
done
echo "==========UPDATING TO HEAD========" >> ${builddir}/messages.log
echo "Build from HEAD ${cvsversionoption} ${cvsversion} against java 1.5 are" >> ${builddir}/README
for i in 'applications/email' 'contributions/lucene' 'contributions/mmbob' 'contributions/thememanager' 'contributions/didactor2' 'applications/richtext' \
    'applications/jumpers' 'applications/commandserver' 'applications/notifications' 'applications/crontab' 'contributions/poll' 'contributions/calendar' 'contributions/multilanguagegui' \
    ; do
    echo updating to HEAD `pwd`/$i | tee -a   ${builddir}/messages.log
    echo $i >> ${builddir}/README
    ${CVS} -q update -d -P ${cvsversionoption} "${cvsversion}" ${headrevision} $i | tee -a   ${builddir}/messages.log 2>> ${builddir}/errors.log
done

