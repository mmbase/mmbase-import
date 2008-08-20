#!/bin/bash
echo $HOME
source version.sh

echo Copying todays artifacts | tee -a ${builddir}/messages.log
echo $HOME
for i in `/usr/bin/find $HOME/.maven/repository/mmbase -mtime -1` ; do
    #echo copy $i to ${builddir} | tee -a ${builddir}/messages.log
    cp $i ${builddir}
done
