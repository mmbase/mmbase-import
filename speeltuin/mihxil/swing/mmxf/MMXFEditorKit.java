
package org.mmbase.util.swing.mmxf;

import org.mmbase.util.swing.xml.*;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.text.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import java.util.*;

public class MMXFEditorKit extends StyledEditorKit {

    private void debug(String s) {
        System.out.println("LOG MMXFEDITORKIT " + s);
    }

    public MMXFEditorKit() {
        super();
        // debug("MMXF EditorKit constructor");
    }

    public String getContentType() {
	return "text/mmxf";
    }

    /**
     * Create an uninitialized text storage model
     * that is appropriate for this type of editor.
     *
     * @return the model
     */
    public Document createDefaultDocument() {
	return new MMXFDocument();
    }


    /**
     *  The view factory used for this thing.
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
        debug("reading mmxf");
        ((MMXFDocument) doc).read(in, pos);        
    }

    /**
     * Write the content of this editor to a Writer.
     */

    public void write(Writer out, Document doc, int pos, int len) throws IOException, BadLocationException {
        MMXFWriter w = new MMXFWriter(out, (MMXFDocument) doc, pos, len);
        w.write();
    }

   
}
