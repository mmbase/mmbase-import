
.PHONY: deploy

deploy:
	mvn deploy:deploy-file -DgroupId=org.mmbase -DartifactId=mmbase-rmmci -Dversion=1.8.8-SNAPSHOT -Dpackaging=jar -DuniqueVersion=false -Dfile=build/mmbase-rmmci-server.jar -Durl=scp://mmbase.org/home/tomcat/download/snapshots -DrepositoryId=mmbase-snapshots
	mvn deploy:deploy-file -DgroupId=org.mmbase -DartifactId=mmbase-rmmci -Dclassifier=client -Dversion=1.8.8-SNAPSHOT -Dpackaging=jar -DuniqueVersion=false -Dfile=build/mmbase-rmmci.jar -Durl=scp://mmbase.org/home/tomcat/download/snapshots -DrepositoryId=mmbase-snapshots
