package org.mmbase.util.swing.mmxf;

import java.io.Reader;
import java.util.*;
import org.mmbase.util.swing.xml.parse.*;
import org.mmbase.util.swing.xml.*;

import javax.swing.text.*;
import org.mmbase.util.swing.xml.Tag;
import org.mmbase.util.swing.xml.TagType;


/**
 * This class describes how an MMXF xml document is described in an swing text Document 
 *
 * @author Michiel Meeuwissen
 */

public class MMXFDocument extends XMLDocument {

     public MMXFDocument() { 
          super(new MMXFStyle()); 
    }
    public Responder getResponder(Reader in, int pos) {
        return new MMXFResponder(in, pos, this);
    }


    /**
     * How to respond when reading in an mmxf XML document.
     * 
     * This in fact is a description of the MMXF format.
     **/

    private class MMXFResponder extends Responder {


        private void debug(String d) {
            System.out.println("LOG MMXFDocument.MMXFResponder " + d);
        }

        private int         pos;
        private MMXFDocument doc;

        MMXFResponder(Reader r, int p, MMXFDocument d) {
            reader = r;
            pos = p;
            doc = d;
        }

        private Tag parent() {
            // returnt the current parent.
            Tag  parent = null;
            if (! stack.empty()) {
                parent = (Tag) stack.peek();
            }
            return parent;
        }

        /**
         * 
         */
        public void recordElementStart(String name, Hashtable attr) throws ParseException {

            debug("start " + name);
            TagType tagType = MMXF.getTagType(name);

            debug("found tag " + tagType);            


            Tag p = parent();
            Element pe = null;
            if (p != null) {
                pe = p.getElement();
            }

            debug("parent " + p);

            Tag tag; // the tag that will be started.
            
            try {
                if (tagType == MMXF.MMXF) {
                    doc.writeLock();
                    tag = new Tag(tagType, doc.createBranchElement(pe, getStyle("root")));
                    doc.writeUnlock();
                }  else if (tagType == MMXF.SECTION) {            
                    debug("new section!!");
                    /*
                    String t = (String) attr.get("title");
                    doc.insertString(pos, t, getStyle("label"));

                    doc.writeLock();
                    Element sec  = doc.createBranchElement(p.getElement(), getStyle("section"));
                    tag = new Tag(tagType, doc.createLeafElement  (sec,    getStyle("sectiontitle"), pos, t.length()));
                    doc.writeUnlock();

                    pos += t.length();
                    */
                    tag = new Tag(tagType, null);
                } else if (tagType == MMXF.P) {
                    debug("create the branch for p");
                    // create the branch..
                    doc.writeLock();
                    tag = new Tag(tagType, doc.createBranchElement(pe, getStyle("p")));
                    doc.writeUnlock();
                } else {
                    tag = new Tag(tagType, null);
                }
                                
                if (p != null) {
                    p.addSubTag(tag);
                    
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new ParseException(e.toString());
            }


            stack.push(tag);
        }
        
        public void recordElementEnd(String name) throws ParseException {
            debug("end " + name);
            Tag top = (Tag) stack.peek();
            TagType tag = MMXF.getTagType(name);
            if (top.getType() != tag) {
                throw new ParseException ("Nesting error at pos " + pos);
            } else {                
                XMLDocument.BranchElement topElement = top.getElement();
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
                topElement.replace(i, 0 , top.getSubElements());
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
            debug("chardata " + charData);
            Tag tag = (Tag) stack.peek();
            try {
                if (tag.getType() == MMXF.EM) {
                    debug("chardata EM");
                    doc.insertString(pos, charData, getStyle("emphasize"));
                    doc.writeLock();
                    doc.createLeafElement(parent().getElement(), getStyle("emphasize"), pos, charData.length());
                    doc.writeUnlock();
                } else {
                    debug("chardata OTHER");
                    doc.insertString(pos, charData, getStyle("label"));
                }
            } catch (Exception e) {
                throw new ParseException (e.toString());
            }
            debug("inserting " + charData + " at " + pos);
            pos += charData.length();
	}
        
        
    }


    
}
