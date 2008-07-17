export SCRIPTLOCATION=`pwd`
export RETURN_CODE=0
# [ -d cmsc ] || cd ..

doWar() {
    (
        cd cmsc
        maven $MAVEN_BUILD_OPTS multiproject:install
    )
    (
        cd cmscImpl
        maven $MAVEN_BUILD_OPTS war
    )
}

doClean() {
    (
        cd cmsc
        maven $MAVEN_BUILD_OPTS multiproject:clean
    )
    (
        cd cmscImpl
        maven $MAVEN_BUILD_OPTS clean
    )
}

doDeploy() {
    if [ -z "$CATALINA_HOME" -o ! -f "$CATALINA_HOME/bin/catalina.sh" ]; then
        echo 'CATALINA_HOME not (properly) set' >&2
        exit 1
    fi

    (
        cd cmscImpl
        rm -rf $CATALINA_HOME/webapps/cmsc*
        cp -v target/*.war $CATALINA_HOME/webapps/

        echo "war files gedeployed naar $CATALINA_HOME/webapps/"
    )
}
processApplications() {
# echo alle argumenten = "$*"
# echo 1 = $1
# echo sterretje = $*
for A in $*
	do
	# echo A = $A
	# echo 1 = $1
	if [ $1 != $A ]; then 
		processApp "$1" "$A"
	fi
	done
exit 1
}

processApp() {
# stop when error occured in loop
# echo processApp = $?
if [ "$RETURN_CODE" != "0" ]; then
	echo Error occured
	exit 2
fi
# setlocal
# setlocal checked of je environment variables goed staan

# check of dir bestaat ff weggelaten
#
doBuild() {
cd $SCRIPTLOCATION
cd $1
maven $BUILD_OPTS multiproject:install
export RETURN_CODE=$?
}

doClean() {
cd $SCRIPTLOCATION
cd $1
maven $BUILD_OPTS multiproject:clean
}

doCleanBuild() {
cd $SCRIPTLOCATION
cd $1
maven $BUILD_OPTS multiproject:clean multiproject:install
}

doDeployTomcat() {
echo Not Yet Implemented
}

case "$1" in
build)
doBuild $2
;;
clean)
doClean $2
;;
cleanbuild)
doCleanBuild $2
;;
deploy-tomcat)
doDeployTomcat $2
;;
*)
exit 1
esac

}

case "$1" in
clean)
    doClean $*
    ;;
war)
    doWar $*
    ;;
deploy)
    doDeploy $*
    ;;
build)
	processApplications $*
	;;
*)
    echo "$0 clean|war|deploy|build" >&2
    exit 1
esac
