all:
	mkdir -p classes
	javac -d classes src/nanoxml/*.java
	javac -d classes -classpath lib/rmmci.jar src/org/mmbase/applications/bugtracker/*.java
	javac -d classes -classpath classes src/org/mmbase/applications/config/*.java
	javac -d classes -classpath .:$(JAVA_HOME)/lib/tools.jar:classes src/org/mmbase/applications/mmbasedoclet/*.java
app:
	mkdir -p docs
	javadoc \
		-classpath $(JAVA_HOME)/lib/tools.jar \
		-sourcepath src \
		-docletpath classes \
		-doclet org.mmbase.applications.mmbasedoclet.MMBaseDoclet \
		org.mmbase.applications.bugtracker
javadoc:
	javadoc -d docs \
		-sourcepath src \
		-classpath $(JAVA_HOME)/lib/tools.jar:classes:lib/rmmci.jar \
		nanoxml \
		org.mmbase.applications.config \
		org.mmbase.applications.mmbasedoclet \
		org.mmbase.applications.bugtracker

clean:
	find . -name *~ -exec rm \{\} \;
	find . -name *.class -exec rm \{\} \;
