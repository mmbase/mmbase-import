#!/bin/bash
echo $HOME

source $HOME/bin/version.sh

echo Copying todays artifacts to ${builddir} | tee -a ${builddir}/messages.log

# only files which name ends in these:
jar=-$mmbaseversion.jar
war=-$mmbaseversion.war
mm=-$mmbaseversion.mmbase-module

for i in `/usr/bin/find $HOME/.maven/repository/mmbase -mtime -1` ; do
    if [ ${i%$jar} != $i -o ${i%$war} != $i -o ${i%$mm} != $i ]; then
	echo copy $i to ${builddir} | tee -a ${builddir}/messages.log
        cp $i ${builddir}
    fi
done
