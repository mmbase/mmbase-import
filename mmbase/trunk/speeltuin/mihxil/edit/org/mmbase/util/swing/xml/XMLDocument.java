
package org.mmbase.util.swing.xml;

import java.io.Reader;
import java.io.IOException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.BadLocationException;

import org.mmbase.util.swing.xml.parse.*;

import javax.swing.text.*;
import java.util.*;

import org.mmbase.util.logging.*;

/**
 * Base class for XML Document (to use in EditorKits). 
 *
 * 
 * A document represents formatted text.  The text itself is in a
 * string (getText()), and the connection with the format is made my
 * offset numbers.  This is different from a DOM Document, where the
 * real data is in the tree itself.
 *
 * @author Michiel Meeuwissen
 */


abstract public class XMLDocument extends DefaultStyledDocument {
  
    private static Logger log = Logging.getLoggerInstance(XMLDocument.class.getName());
    private void debug(String d) {
        //log.info("XMLDocument " + d);

    }

    public XMLDocument(StyleContext s) {
        super(s);
    }

    public Element createBranchElement(Element parent, AttributeSet a) {
        Element ret = new XMLBranchElement(parent, a);
        return ret;
    }

    public Element createLeafElement(Element parent, AttributeSet a, int pos, int len) {
        Element ret = new LeafElement(parent, a, pos, len);
        return ret;
    }
  
    protected AbstractElement createDefaultRoot() {      	
	writeLock();
        debug("Creating default root");
	BranchElement root      = (BranchElement) createBranchElement(null, getStyle("root"));
        //root style supplies the basic box view to contain everyting

	BranchElement paragraph = (BranchElement) createBranchElement(root, getStyle("section"));

	LeafElement brk = (LeafElement) createLeafElement(paragraph, getStyle("section"), 0, 1);
	Element[] buff = new Element[1];
	buff[0] = brk;
	paragraph.replace(0, 0, buff);

	buff[0] = paragraph;
	root.replace(0, 0, buff);
	writeUnlock();
	return root;
    }
    

    /**
     * An XMLBranch is a Branch, but it is fashioned for XML elements.
     */
    public class XMLBranchElement extends BranchElement {
        private String name;
        XMLBranchElement(Element parent, AttributeSet a) {
            super(parent, a);
            debug("creating branch. Children: " + this.children());
            name = "xmlbranch";
        }
        public String getName() {
            AttributeSet a = getAttributes();
            if (a instanceof StyleContext.NamedStyle) {
                debug("Creating branchelement in with "  + a);
                return ((StyleContext.NamedStyle) getAttributes()).getName();
            } else {
                debug("Creating branchelement NOT NAMEDSTYLE ");
                return super.getName();
            }
        }
        public String toString() {
            return getName();
        }
        
    }


    /**
     * An XMLBranch is a Branch, but it is fashioned for XML elements.
     */
    /*
    public class XMLLeafElement extends LeafElement {
        private String name;
        XMLLeafElement(Element parent, AttributeSet a, int pos, int l) {
            super(parent, a, pos, l);
            name = "xmlleaf";
        }
        public String getName() {
            AttributeSet a = getAttributes();
            if (a instanceof StyleContext.NamedStyle) {
                return ((StyleContext.NamedStyle) getAttributes()).getName();
            } else {
                return super.getName();
            }
        }
        
    }
    */
    /**
     * Implement the XML responder.
     */
    abstract public Responder getResponder(Reader in, int pos);

    public void read(Reader in, int pos) throws IOException, BadLocationException {        
        debug("XMLRESPONDERReading at " + pos);
        Parser parser = new Parser();
        Responder res = getResponder(in, pos);
        parser.parseXML(res );        
       
        
    }

}
