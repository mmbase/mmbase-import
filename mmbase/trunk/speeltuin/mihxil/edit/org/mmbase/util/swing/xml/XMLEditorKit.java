
package org.mmbase.util.swing.xml;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.text.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import java.util.*;

abstract public class XMLEditorKit extends StyledEditorKit {

    protected void debug(String s) {
        System.out.println("LOG XMLEDITORKIT " + s);
    }

    public String getContentType() {
	return "text/xml";
    }

    /**
     *  The view factory used for XML.
     *
     */

    private static final XMLViewFactory defaultFactory = new XMLViewFactory();

    /**
     *
     */
    public ViewFactory getViewFactory() {
	return defaultFactory;
    }

    /**
     * 
     */
    public void read(Reader in, Document doc, int pos) throws IOException, BadLocationException {
        debug("reading xml");
        ((XMLDocument) doc).read(in, pos);        
    }

    /**
     * Write the content of this editor to a Writer.
     */

    public void write(Writer out, Document doc, int pos, int len) throws IOException, BadLocationException {
        XMLWriter w = new XMLWriter(out, (XMLDocument) doc, pos, len);
        w.write();
    }

   
}
