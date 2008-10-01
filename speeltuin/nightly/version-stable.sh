version=`date -u '+%Y-%m-%d'`
cvsversionoption="-D"
cvsversion=`date  '+%Y-%m-%d %H:%M'`
revision="MMBase-1_8"
headrevision="-A"

#version="MMBase-1.8.6"
#cvsversion=
#cvsversionoption="-r"
#revision=MMBase-1_8_6_Final

dir=${version}
builddir="/home/nightly/builds/stable/${dir}"
mkdir -p ${builddir}

