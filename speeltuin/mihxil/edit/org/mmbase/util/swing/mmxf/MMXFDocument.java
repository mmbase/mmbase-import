package org.mmbase.util.swing.mmxf;

import java.io.Reader;
import java.util.*;
import org.mmbase.util.swing.xml.parse.*;
import org.mmbase.util.swing.xml.*;

import javax.swing.text.*;
import org.mmbase.util.swing.xml.Tag;
import org.mmbase.util.swing.xml.TagType;


/**
 * This class describes how an MMXF xml document is represented in an swing text Document
 * The Inner Class implements an XMLResponder for this. 
 *
 * It is instantiated with an MMXFStyle object.
 *
 * @author  Michiel Meeuwissen
 * @version $Id: MMXFDocument.java,v 1.1 2003-01-08 22:58:53 michiel Exp $
 */

public class MMXFDocument extends XMLDocument {

    /**
     */
    public MMXFDocument() { 
        super(new MMXFStyle()); 
    }
    /**
     * Required by XMLDocument.
     */
    public Responder getResponder(Reader in, int pos) {
        return new MMXFResponder(in, pos, this);
    }


    //--------------------------------------------------------------------------------
    // Follows the implementation of Responder
    //
    /**
     * How to respond when reading in an `MMXF' XML document.
     * 
     * This in fact is a description of the MMXF format.
     **/

    private class MMXFResponder extends Responder {
        
        // Just for development.
        private void debug(String d) {
            System.out.println("LOG MMXFDocument.MMXFResponder " + d);
        }
        
        // current position.
        private int         pos;
        // document to write to.
        private MMXFDocument doc;

        MMXFResponder(Reader r, int p, MMXFDocument d) {
            reader = r;
            pos = p;
            doc = d;
        }

        private Tag parent() {
            // returns the current parent.
            if (! stack.empty()) {
                Tag top = (Tag) stack.peek();
                debug("getting parent: " + top);
                return top;
            } else {
                debug("Stack empty");
                return null;
            }
        }

        /**
         * Implementation of Responder, what to do on an open-tag.
         */
        public void recordElementStart(String name, Hashtable attr) throws ParseException {

            debug("-------start " + name);
            TagType tagType = MMXF.getTagType(name);

            debug("found tag " + tagType);            

            Tag p = parent();
            Element pe = null;
            if (p != null) {
                pe = p.getElement();
            }

            debug("parent " + p);

            Tag tag = null; // the tag that will be started.
            
            try {
                if (tagType == MMXF.MMXF) {
                    doc.writeLock(); // does not compile with JAVAC?
                    tag = new Tag(tagType, doc.createBranchElement(pe, getStyle("root")));
                    doc.writeUnlock();
                }  else if (tagType == MMXF.SECTION) {
                    doc.writeLock();
                    tag = new Tag(tagType, doc.createBranchElement(pe, getStyle("section")));
                    doc.writeUnlock();

                    String t = (String) attr.get("title");
                    doc.insertString(pos, t, getStyle("label"));
                    //doc.writeLock();
                    //new LeafElement(tag.getElement(),    getStyle("sectiontitle"), pos, t.length());
                    //doc.writeLock();
                    pos += t.length();

                } else if (tagType == MMXF.P) {
                    debug("create the branch for p");
                    // create the branch..
                    doc.writeLock();
                    tag = new Tag(tagType, doc.createBranchElement(pe, getStyle("p")));
                    doc.writeUnlock();
                } else {
                    debug("Don't know what to do for this tag");
                    doc.writeLock();
                    tag = new Tag(tagType, doc.createBranchElement(pe, getStyle("p")));
                    doc.writeUnlock();
                    //tag = new Tag(tagType, null);
                }
                                
                if (tag != null) {
                    stack.push(tag);
                    if (p != null) {
                        p.addSubTag(tag);                    
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new ParseException(e.toString());
            }
        }
        
        public void recordElementEnd(String name) throws ParseException {
            debug("end " + name);
            Tag openTag = parent();
            if (openTag == null) {
                System.out.println("Closing on empty stack (unbalanced tags)");
                return;
            }
            TagType tag = MMXF.getTagType(name);
            if (openTag.getType() != tag) {
                throw new ParseException ("Nesting error; closing tag " + tag + " while " + openTag + " is open");
            } else {                
                XMLDocument.BranchElement topElement = openTag.getElement();
                debug("top element " + topElement);
                Enumeration e = topElement.children();
                if (e != null) {                    
                    while (e.hasMoreElements()) {
                        System.out.println(e.nextElement());
                    }
                }
                debug("replacing in " + topElement);
                int i = 0; // topElement.getStartOffset();
                debug("replacing at " + i);
                topElement.replace(i, 0 , openTag.getSubElements());
                stack.pop();
            }           
            /*
              if (tag == MMXF.P) {
              System.out.println("new p!! " + getStyle("p"));
              doc.insertString(pos, t, getStyle("label"))
              
              doc.writeLock();
              tag = new Tag(tagType, doc.createBranchElement(p.getElement(), getStyle("p")));
              doc.writeUnlock();
              }
            */

        }

        public void recordCharData(String charData) throws ParseException {
            debug("......chardata " + charData + " stacksize: " + stack.size());
            
            Tag parent = parent();
            if (parent == null) {
                System.out.println("NO TAGS ON STACK");
                return;
            }
            try {
                if (parent.getType() == MMXF.EM) {
                    debug("chardata type EM");
                    doc.insertString(pos, charData, getStyle("emphasize"));
                } else {
                    debug("chardata type OTHER");
                    doc.insertString(pos, charData, getStyle("label"));
                }
            } catch (Exception e) {
                throw new ParseException (e);
            }
            debug("inserting " + charData + " at " + pos);
            pos += charData.length();
	}               
    } // MMXFResponder

}
