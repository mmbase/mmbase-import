
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

    public MMXFEditorKit() {
        super();
        // System.out.println("MMXF EditorKit constructor");
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

    private static final XMLViewFactory defaultFactory = new XMLViewFactory();
    public ViewFactory getViewFactory() {
	return defaultFactory;
    }

    public void read(Reader in, Document doc, int pos) throws IOException, BadLocationException {
        System.out.println("reading mmxfe");
        ((MMXFDocument) doc).read(in, pos);        
    }


    public void write(Writer out, Document doc, int pos, int len) throws IOException, BadLocationException {
        MMXFWriter w = new MMXFWriter(out, (MMXFDocument) doc, pos, len);
        w.write();
    }

   
}
