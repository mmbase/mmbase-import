package org.mmbase.util.swing.mmxf;

import javax.swing.text.*;
import java.io.Writer;
import java.io.IOException;

/**
 * Must be moved do xml.Writer as soon as I got this working.
 *
 * @author Michiel Meeuwissen
 */

public class MMXFWriter extends AbstractWriter {
    public MMXFWriter(Writer w, MMXFDocument doc) {
	this(w, doc, 0, doc.getLength());
    }
    public MMXFWriter(Writer w, MMXFDocument doc, int pos, int len) {
	super(w, doc, pos, len);
    }


    public void write() throws IOException, BadLocationException {

	ElementIterator it = getElementIterator();
	Element next = null;
	
        
        Writer writer = getWriter();;
      
        // ((AbstractDocument) getDocument()).dump(System.out);

        writer.write("<mmxf>");  

	while ((next = it.next()) != null) {
            int start = next.getStartOffset();
            int end   = next.getEndOffset();

            if (next.isLeaf()) {
                String o = next.getAttributes().getAttribute("xmltag").toString();
                if ("".equals(o)) { 
                    o = null;
                }
                if (o != null) { 
                    writer.write("<" +  o + ">");
                } else {                    
                    // writer.write("YES");
                }
                //writer.write("+++" + start + " " + end + " " + getDocument().getLength() + "++");
                try {
                    writer.write(getDocument().getText(start, end - start));
                } catch (Exception e) {
                    // writer.write("{" + e.toString() + "}");
                }
                if (o != null) { 
                    writer.write("</" + o + ">");
                }                
                // writer.write(next.toString() + next.getAttributes().getAttribute("tag") + "----\n");
            } else {
                // writer.write("[not a leaf]");
            }

                     
	}
        
        writer.write("</mmxf>");
    }

}
