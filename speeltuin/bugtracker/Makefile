all:
	javac -d classes src/nanoxml/*.java
	javac -d classes src/org/mmbase/applications/bugtracker/*.java
	javac -d classes -classpath classes src/org/mmbase/applications/config/*.java
	javac -d classes -classpath .:$(JAVA_HOME)/lib/tools.jar:classes src/org/mmbase/applications/mmbasedoclet/*.java
app:
	javadoc \
		-classpath $(JAVA_HOME)/lib/tools.jar \
		-sourcepath src \
		-docletpath classes \
		-doclet org.mmbase.applications.mmbasedoclet.MMBaseDoclet \
		org.mmbase.applications.bugtracker
javadoc:
	javadoc -d docs \
		-sourcepath src \
		nanoxml

clean:
	find . -name *~ -exec rm \{\} \;
	find . -name *.class -exec rm \{\} \;
