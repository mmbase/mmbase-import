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

    private TagType type;
    private Element element;
    private List    subTags;

    public Tag() {
    }

    public Tag (TagType t, Element e) throws ParseException {
        type = t;
        element = e;
        if (! e.isLeaf()) {
            subTags = new Vector();
        }
    }
   
    public TagType getType() {
        return type;
    }
    
    public XMLDocument.XMLBranchElement getElement() {
        return (XMLDocument.XMLBranchElement) element;
    }

    public void addSubTag(Tag t) {
        subTags.add(t.getElement());
    }
    public Element[] getSubElements() {
        return (Element[]) subTags.toArray(new Element[0]);
    }
    

    public String toString() {
        return type != null ? type.toString() + ".instance" : "NULL";
    }
    
}
