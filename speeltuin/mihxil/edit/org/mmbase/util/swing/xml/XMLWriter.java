package org.mmbase.util.swing.xml;
import javax.swing.text.*;
import java.io.Writer;
import java.io.IOException;

/**
 * Writer for XMLDocuments.
 *
 * @author Michiel Meeuwissen
 */

public class XMLWriter extends AbstractWriter {
    public XMLWriter(Writer w, XMLDocument doc) {
	this(w, doc, 0, doc.getLength());
    }
    public XMLWriter(Writer w, XMLDocument doc, int pos, int len) {
	super(w, doc, pos, len);
    }

    protected void write(Element e, int start, int end) throws IOException, BadLocationException {
        Writer writer = getWriter();;    
        if (e.isLeaf()) {
            try {
                writer.write(getDocument().getText(start, end - start));
            } catch (Exception f) {
                // writer.write("{" + e.toString() + "}");
            }
        } else {
            String o = e.getAttributes().getAttribute("xmltag").toString();
            writer.write("<" +  o + ">");
        }
    }


    public void write() throws IOException, BadLocationException {

	ElementIterator it = getElementIterator();
	Element next = null;
	  
	while ((next = it.next()) != null) {
            int start = next.getStartOffset();
            int end   = next.getEndOffset();
            write(next, start, end);
	}
        
    }

}
