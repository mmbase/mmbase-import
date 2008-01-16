#!/bin/sh
if [ ARG"$1" == ARG ]
	then
		echo "Usage: generate_report.sh configfile workingfolder reportlocation"
	else
		/Applications/apache-ant-1.7.0/bin/ant -lib ./lib report -Dconfigfile="$1" -Dworkingfolder="$2" -Dreportlocation="$3"
fi

