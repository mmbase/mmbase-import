rm -rf  build.log
(
        killall -9 java
	java -version
	rm -rf  mmbase-1_7
        cvs -d:ext:keesj@cvs.mmbase.org:/var/cvs co -r MMBase-1_7 -d mmbase-1_7 all
	cd  mmbase-1_7
	echo "download.dir=/home/keesj/dev/mmbase/download" > build.properties
	echo "expand.dir=/home/keesj/dev/mmbase/download/expanded" >> build.properties
	echo "optimize=on" >> build.properties
	echo "debug=on" >>  build.properties
	echo "deprecation=on"  >>  build.properties
	echo "distro=MMBase 1.7.4-rc2" >> build.properties

	#
	# add sources to 
	#
        echo '450a451' > buid.xml.patch
        echo '>         <include name="**/*.java"/>' >>  buid.xml.patch
	patch build.xml buid.xml.patch
	ant bindist
) 2>&1 | tee build.log
exit
(
 rm -rf server
 tar zxvf /home/keesj/download/jakarta-tomcat-5.5.9.tar.gz
 mv jakarta-tomcat-5.5.9 server
 cd server
 cp -r ../mmbase-1_7/build/mmbase/mmbase-webapp webapps/
 cd bin 
 ./startup.sh
) 2>&1 | tee install.tomcat.log
