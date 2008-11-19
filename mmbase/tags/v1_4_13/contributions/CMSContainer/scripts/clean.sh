#!/bin/bash
# clean.sh removes all 'target' directories in the directories specified. 
#
for A in $*
        do
find $A -type d -name target -print|xargs rm -rf 
        done
exit 1
}
