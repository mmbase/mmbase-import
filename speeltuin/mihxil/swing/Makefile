#
# Makefile for a kit test. Make sure '.' is in your CLASSPATH.
#
# @author Michiel Meeuwissen 

XMLCLASSES  = xml/XMLDocument.class xml/Tag.class xml/TagType.class xml/parse/Responder.class xml/XMLViewFactory.class
XMLPARSER   = xml/parse/Parser.class xml/parse/Responder.class
MMXFCLASSES = mmxf/MMXF.class mmxf/MMXFDocument.class mmxf/MMXFEditorKit.class mmxf/MMXFStyle.class mmxf/MMXFWriter.class
CLASSES     = KitTest.class MMEditorPane.class $(XMLCLASSES) $(XMLPARSER) $(MMXFCLASSES)

test: $(CLASSES)
	java org.mmbase.util.swing.KitTest text/mmxf

testhtml: $(CLASSES)
	java org.mmbase.util.swing.KitTest text/html


test2: $(CLASSES) Test.class
	java org.mmbase.util.swing.Test


%.class: %.java
	jikes $<

clean:
	rm $(CLASSES)