echo "CATALINA_HOME=$CATALINA_HOME"

rm -fr $CATALINA_HOME/webapps/*
rm -fr $CATALINA_HOME/work/*
cp ../../CMSContainer_Demo/demo.cmscontainer.org/war-community/target/*.war $CATALINA_HOME/webapps 
