mmbaseversion=1.9-SNAPSHOT

version=`date -u '+%Y-%m-%d'`
cvsversionoption="-D"
cvsversion=`date  '+%Y-%m-%d %H:%M'`
revision="-A"

#version="MMBase-1.9.0.final"
#cvsversion=
#cvsversionoption="-r"
#revision="MMBase-1_9_0_Final"

dir=${version}
builddir="/home/nightly/builds/${dir}"
mkdir -p ${builddir}


