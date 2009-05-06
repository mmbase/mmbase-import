/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util;

import java.util.*;

/**
 * Generates a HTML Element: INPUT TEXT.
 * Uses these variables which are set in the
 * super class (HTMLElement) to generate HTML:
 * <ul>
 * <li>boolean moreValues : if true it will take the first value of a list of items.</li>
 * <li>Vector valuesList  : The list of items. </li>
 * <li>String size        : if not null the HTML tag SIZE=size is added </li>
 * </ul>
 *
 * @application SCAN
 * @author Jan van Oosterom
 * @version $Id: HTMLElementText.java,v 1.7 2008-09-03 15:23:39 michiel Exp $
 */
public class HTMLElementText  extends HTMLElement {
    /**
     * Creates a HTMLElementText
     */
    public HTMLElementText() {
    }

    /**
     * Generates the HTML code.
     */
    protected String generate() {
        String html = "";
        if (moreValues) {
            Enumeration e = Collections.enumeration(valuesList);
            if (e.hasMoreElements()) {
                String val = (String) e.nextElement();
                if (val.equals("null")) {
                    val = "";
                }
                html += " <input type=\"text\" name=\"" + name + "\" ";
                html += "value=\"" + val + "\"";
                if (size != null) html += "size=\"" + size+"\"";
                html += ">" ;
            }
        } else {
            if (values.equals("null")) {
                values = "";
            }
            html += " <input type=\"text\" name=\"" + name + "\" ";
            if (values != null) html += "value=\"" + values + "\"";
            if (size != null) html += "size=\"" + size+"\"";
            html += ">";
        }
        return html;
    }
}
