package org.mmbase.util.swing.xml;

import javax.swing.text.*;

/**
 *
 * @author Michiel Meeuwissen.
 */


public class TagType {

    private String  name;    
    private String style;
    
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
