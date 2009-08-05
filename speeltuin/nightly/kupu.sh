#!/bin/bash
cd $HOME/kupu
svn up
make kupu-i18n.jar
cd mmbase
make mmbase-kupu-i18n.jar
make
cd ..
find . -name '*.jar' -exec cp \{\} ~/.maven/repository/oscom/jars \;