/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util;

import java.util.*;

/**
 * Generates a HTML Element: INPUT PASSWORD, uses this variables which are set in the<br>
 * super class (HTMLElement) to generate HTML:
 * <ul>
 * <li>boolean moreValues : if true it will take the first value of a list of items.</li>
 * <li>Vector valuesList  : The list of items. </li>
 * <li>String size        : if not null the HTML tag SIZE=size is added </li>
 * </ul>
 *
 * @version 26-Sep-1996
 * @author Jan van Oosterom
 */
public class HTMLElementPassword  extends HTMLElement {
    // Note: more appropriate would be to extend from HTMLElementText

    /**
     * Creates a HTMLElementCheckbox
     */
    public HTMLElementPassword() {
    }

    /**
     * Generates the HTML code.
     */
    protected String generate() {
        String html = "";
        if (moreValues) {
            Enumeration e = valuesList.elements();
            if (e.hasMoreElements()) {
                String val = (String) e.nextElement();
                html += name + "<input type=\"password\" name=\"" + name + "\" ";
                html += "value=\"" + val + "\"";
                if (size != null) html += "size=\""+size+"\"";
                html += ">" ;
            }
        } else {
            html += name + "<input type=\"password\" name=\"" + name + "\" ";
            if (values != null) html +="value=\"" +  values + "\"";
            if (size != null) html += "size=\""+size+"\"";
            html += ">";
        }
        return html;
    }
}
