mmbaseversion=1.9.0-SNAPSHOT

version=`date -u '+%Y-%m-%d'`
cvsversionoption="-D"
cvsversion=`date  '+%Y-%m-%d %H:%M'`
revision="-A"

#version="MMBase-1.9.0.beta2"
#cvsversion=
#cvsversionoption="-r"
#revision="MMBase-1_9_0_beta2"

dir=${version}
builddir="/home/nightly/builds/${dir}"
mkdir -p ${builddir}


