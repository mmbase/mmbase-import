
package org.mmbase.util.swing.xml;

import java.io.Reader;
import java.io.IOException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.BadLocationException;

import org.mmbase.util.swing.xml.parse.*;

import javax.swing.text.*;
import java.util.*;

/**
 * Base class for XML Document (to use in EditorKits). 
 *
 * 
 * A document represent formatted text.  The test itself is in a
 * string (getText()), and the connection with the format is made my
 * offset numbers.  This is different from DOM Document, where the
 * real data is in the tree itself.
 * 
 *
 * @author Michiel Meeuwissen
 */


abstract public class XMLDocument extends DefaultStyledDocument {
  
    public XMLDocument(StyleContext s) {
        super(s);
    }

    public Element createBranchElement(Element parent, AttributeSet a) {
        Element ret = new XMLBranchElement(parent, a);
        return ret;
    }
    public Element createLeafElement(Element parent, AttributeSet a, int pos, int len) {
        Element ret = new XMLLeafElement(parent, a, pos, len);
        return ret;
    }
  
    protected AbstractElement createDefaultRoot() {      	
	writeLock();
        System.out.println("Creating default root");
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
            System.out.println("creating branch. Children: " + this.children());
            name = "xmlbranch";
        }
        public String getName() {
            AttributeSet a = getAttributes();
            if (a instanceof StyleContext.NamedStyle) {
                System.out.println("Creating branchelement in with "  + a);
                return ((StyleContext.NamedStyle) getAttributes()).getName();
            } else {
                System.out.println("Creating branchelement NOT NAMEDSTYLE ");
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

    abstract public Responder getResponder(Reader in, int pos);

    public void read(Reader in, int pos) throws IOException, BadLocationException {        
        Parser parser = new Parser();
        Responder res = getResponder(in, pos);
        parser.parseXML(res );        
       
        
    }

}
