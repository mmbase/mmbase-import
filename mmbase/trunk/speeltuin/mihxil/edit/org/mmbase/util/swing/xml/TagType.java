package org.mmbase.util.swing.xml;

import javax.swing.text.*;

/**
 * Representation of an XML-tag type.
 *
 * @author Michiel Meeuwissen.
 */


public class TagType {

    private String  name;  // the name of this tag
    private String  style; // the style associated with this tag-type
    
    public TagType (String n, String s) {
        name = n;
        style = s;
    }
    
    /**
     * Return the string representation of the
     * tag.
     */
    public String toString() {
        return name;
    }

    public String getStyle() {
        return style;
    }

}
