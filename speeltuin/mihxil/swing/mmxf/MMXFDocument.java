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
     **/

    private class MMXFResponder extends Responder {

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

            System.out.println("start " + name);
            TagType tagType = MMXF.getTagType(name);

            System.out.println("found tag " + tagType);            


            Tag p = parent();
            Element pe = null;
            if (p != null) {
                pe = p.getElement();
            }

            System.out.println("parent " + p);

            Tag tag; // the tag that will be started.
            
            try {
                if (tagType == MMXF.MMXF) {
                    doc.writeLock();
                    tag = new Tag(tagType, doc.createBranchElement(pe, getStyle("root")));
                    doc.writeUnlock();
                }  else if (tagType == MMXF.SECTION) {            
                    System.out.println("new section!!");
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
                    System.out.println("create the branch for p");
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
            System.out.println("end " + name);
            Tag top = (Tag) stack.peek();
            TagType tag = MMXF.getTagType(name);
            if (top.getType() != tag) {
                throw new ParseException ("Nesting error at pos " + pos);
            } else {                
                XMLDocument.BranchElement topElement = top.getElement();
                System.out.println("hoii" + topElement);
                Enumeration e = topElement.children();
                if (e != null) {                    
                    while (e.hasMoreElements()) {
                        System.out.println(e.nextElement());
                    }
                }
                System.out.println("replacing in " + topElement);
                int i = 0; // topElement.getStartOffset();
                System.out.println("replacing at " + i);
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
            System.out.println("chardata " + charData);
            Tag tag = (Tag) stack.peek();
            try {
                if (tag.getType() == MMXF.EM) {
                    System.out.println("chardata EM");
                    doc.insertString(pos, charData, getStyle("emphasize"));
                    doc.writeLock();
                    doc.createLeafElement(parent().getElement(), getStyle("emphasize"), pos, charData.length());
                    doc.writeUnlock();
                } else {
                    System.out.println("chardata OTHER");
                    doc.insertString(pos, charData, getStyle("label"));
                }
            } catch (Exception e) {
                throw new ParseException (e.toString());
            }
            System.out.println("inserting " + charData + " at " + pos);
            pos += charData.length();
	}
        
        
    }


    
}
