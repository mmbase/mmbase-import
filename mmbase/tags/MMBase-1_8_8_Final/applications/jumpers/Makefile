
.PHONY: deploy

deploy:
	mvn deploy:deploy-file -DgroupId=org.mmbase -DartifactId=mmbase-jumpers -Dversion=1.8.7-SNAPSHOT -Dpackaging=jar -DuniqueVersion=false -Dfile=build/mmbase-jumpers.jar -Durl=scp://mmbase.org/home/mmweb/web/snapshots -DrepositoryId=mmbase-snapshots

