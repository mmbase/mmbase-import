#
# Makefile for a kit test. 
#
# @author Michiel Meeuwissen 

JAR         = ./build/mihxileditor.jar
CLASSPATH   = $(JAR):../../../build/lib/xml-apis.jar:$(JAVA_HOME)/jre/lib/rt.jar:$(JAVA_HOME)/lib/tools.jar
BASE        = org/mmbase/util/swing/
XML         = $(BASE)xml/*.java $(BASE)xml/parse/*.java
MMXF        = $(BASE)mmxf/*.java
DEPEND      = $(BASE)*.java $(MMXF) $(XML)
JAVA        = java

test: $(JAR)
	$(JAVA) org.mmbase.util.swing.KitTest text/mmxf

testhtml: $(CLASSES)
	$(JAVA) org.mmbase.util.swing.KitTest text/html


test2: $(CLASSES) Test.class
	$(JAVA) org.mmbase.util.swing.Test


$(JAR): $(DEPEND)
	ant jar

clean:
	ant clean