#!/bin/bash

source $HOME/bin/env.sh
source $HOME/bin/version.sh

#export MAILADDRESS="developers@lists.mmbase.org"
export MAILADDRESS="michiel.meeuwissen@gmail.com"
export BUILD_MAILADDRESS=$MAILADDRESS

 # Using one thread for all mail about failures
parent="<20080906100002.GA1861@james.mmbase.org>";
mutthdr="my_hdr In-Reply-To: ${parent}";


cd $HOME/builds

showtests=1
if [ 1 == 1 ] ; then
    if [ -f latest/messages.log ] ; then
        if (( `cat latest/messages.log  | grep -P '\[javac\]\s+[0-9]+\s+errors' | wc -l` > 0 )) ; then
	    echo Build failed, sending mail to ${BUILD_MAILADDRESS} | tee -a ${builddir}/messages.log
	   (echo -e "Build on ${version} failed:\n\n" | \
		cat latest/messages.log latest/errors.log | grep -B 10 "\[javac\]") | \
		mutt -e "$mutthdr" -s "Build failed" ${BUILD_MAILADDRESS}
	    showtests=0;
        fi
    else
        echo Build failed, sending mail to ${BUILD_MAILADDRESS} | tee -a ${builddir}/messages.log
        (echo -e "No build created on ${version}\n\n" | \
            tail -q -n 20 - latest/errors.log ) | \
            mutt -e "$mutthdr" -s "Build failed" ${BUILD_MAILADDRESS}
	showtests=0;
    fi
fi



if [ 1 == $showtests ] ; then
    cd /home/nightly/builds
    echo Test results | tee -a ${builddir}/messages.log

    if [ -f latest/tests-results.log ] ; then
	if (( `cat latest/tests-results.log  | grep 'FAILED' | wc -l` > 0 )) ; then
	    (echo "Test cases failed to build on ${version}" | cat latest/tests-results.log )  | \
		mutt -e "$mutthdr" -s "Test cases failures" ${MAILADDRESS}
		
	fi
	if (( `cat latest/tests-results.log  | grep 'FAILURES' | wc -l` > 0 )) ; then
	    echo Failures, sending mail to ${MAILADDRESS}  | tee -a ${builddir}/messages.log
	    (echo "Failures on build ${version}" ; echo "See also http://www.mmbase.org/download/builds/latest/tests-results.log" ; \
                cat latest/tests-results.log  | grep -P  '(^Tests run:|^[0-9]+\)|^\tat org\.mmbase|FAILURES|========================|OK)' ) | \
		mutt -e "$mutthdr" -s "Test cases failures" ${MAILADDRESS}
	fi
    fi
fi

