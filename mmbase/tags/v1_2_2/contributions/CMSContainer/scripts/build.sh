set -e

[ -d cmsc ] || cd ..

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

case "$1" in
clean)
    doClean
    ;;
war)
    doWar
    ;;
deploy)
    doDeploy
    ;;
*)
    echo "$0 clean|war" >&2
    exit 1
esac
