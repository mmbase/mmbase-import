package org.mmbase.util.swing.xml;

import java.util.*;
import javax.swing.text.Element;
import org.mmbase.util.swing.xml.parse.*;
import org.mmbase.util.swing.xml.XMLDocument;

/**
 * A Tag to store in stacks. You must also store the corresponding
 * swing Element with it.
 *
 * @author Michiel Meeuwissen.
 */


public class Tag {

    protected TagType type;        // the type of this Tag.
    protected Element element;     // The swing Element associated with this tag.
    private   List    subElements; //

    /** 
     * Instantatiate
     */
    public Tag(TagType t, Element e) throws ParseException {
        type    = t;
        if (e.isLeaf()) throw new ParseException("Content Elements are not associated with XML tags");
        element = e;
        subElements = new ArrayList();
    }
   
    public TagType getType() {
        return type;
    }
    
    public XMLDocument.XMLBranchElement getElement() {
        return (XMLDocument.XMLBranchElement) element;
    }

    public void addSubTag(Tag t) {
        subElements.add(t.getElement());
    }
    public Element[] getSubElements() {
        return (Element[]) subElements.toArray(new Element[0]);
    }
    

    public String toString() {
        return type != null ? type.toString() + ".instance" : "NULL";
    }
    
}
