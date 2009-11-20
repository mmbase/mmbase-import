version=3.0.1
subversion=0

fversion=2.6.5
fsubversion=0

zip=ckeditor_$(version).tar.gz
fzip=FCKeditor_$(fversion).tar.gz

.PHONY: clean




ckeditor.war: $(zip)
	mkdir -p build/mmbase/
	tar zxf  $(zip) -C build/mmbase
	jar cf $@ -C build/ .

fckeditor.war: $(fzip)
	mkdir -p fbuild/mmbase/
	tar zxf  $(fzip) -C fbuild/mmbase
	jar cf $@ -C fbuild/ .

$(zip):
	wget http://download.cksource.com/CKEditor/CKEditor/CKEditor%20$(version)/$(zip);

$(fzip):
	wget http://downloads.sourceforge.net/project/fckeditor/FCKeditor/$(fversion)/FCKeditor_$(fversion).tar.gz?use_mirror=garr

deploy: ckeditor.war
	mvn deploy:deploy-file \
	 -DgeneratePom.description='CKEditor' -DgroupId=org.mmbase.ckeditor -DartifactId=ckeditor   \
	 -Dversion=$(version)-$(subversion) -Dpackaging=war \
	-Dfile=ckeditor.war \
	-Durl=scp://mmbase.org/home/mmweb/web/maven2 -DrepositoryId=mmbase


fdeploy: fckeditor.war
	mvn deploy:deploy-file \
	 -DgeneratePom.description='FCKEditor' -DgroupId=org.mmbase.ckeditor -DartifactId=fckeditor   \
	 -Dversion=$(fversion)-$(fsubversion) -Dpackaging=war \
	-Dfile=fckeditor.war \
	-Durl=scp://mmbase.org/home/mmweb/web/maven2 -DrepositoryId=mmbase


clean:
	rm -rf build
	rm -rf fbuild
	rm -rf ckeditor.war
	rm -rf fckeditor.war

