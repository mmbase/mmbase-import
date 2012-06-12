
.PHONY: deploy-for-tests

deploy-for-tests:
	mvn deploy:deploy-file -DgroupId=org.mmbase.tests -DartifactId=mmbase-rmmci -Dversion=1.9.2.0 -Dclassifier=client-skinny -Dpackaging=jar \
	-Dfile=target/mmbase-rmmci-1.9.6-client-skinny.jar \
	-Durl=scp://mmbase.org/home/mmweb/web/maven2 -DrepositoryId=mmbase
