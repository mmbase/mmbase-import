all:
	javac org/mmbase/applications/bugtracker/*.java
	javac org/mmbase/applications/config/*.java
	javac -classpath .:$(JAVA_HOME)/lib/tools.jar org/mmbase/applications/mmbasedoclet/*.java
app:
	javadoc \
		-doclet org.mmbase.applications.mmbasedoclet.MMBaseDoclet \
		-classpath $(JAVA_HOME)/lib/tools.jar \
		org/mmbase/applications/bugtracker/*.java

clean:
	find . -name *~ -exec rm \{\} \;
	find . -name *.class -exec rm \{\} \;
