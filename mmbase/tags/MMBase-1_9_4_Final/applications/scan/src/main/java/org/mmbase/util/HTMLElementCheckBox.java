/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util;

import java.util.*;
import org.mmbase.util.logging.*;

/**
 * Generates a HTML Element: INPUT CHECKBOX.
 * Uses these variables which are set in the super class (HTMLElement) to generate HTML:
 * <ul>
 * <li>boolean sel        : if true it checks if the String selected equals the
 *                          current value if equal the HTML tag CHECKED is added.</li>
 * <li>String selected    : see above   </li>
 * <li>boolean ex         : if true it checks if the String exclude equals the current
 *                          value, if equal this value will be skipped (no HTML
 *                          generated for this item)</li>
 * <li>String exclude     : see above</li>
 * <li>boolean moreValues : if true it will make a list of items.</li>
 * <li>boolean moredouble : if true it will make a paired list of items.
 *                          (first item = VALUE second item=NAME)</li>
 * <li>Vector valuesList  : The list of items. </li>
 * <li>boolean vertical    : if true the various checkboxes are seperated with &lt;brk /&gt; tags.</li>
 * </ul>
 *
 * @application SCAN
 * @author Jan van Oosterom
 * @version $Id$
 */
public class HTMLElementCheckBox  extends HTMLElement {
    // Note: more appropriate would be to extend from HTMLElementSelect

    // logger
    private static Logger log = Logging.getLoggerInstance(HTMLElementCheckBox.class.getName());

    /**
     * Creates a HTMLElementCheckbox
     */
    public HTMLElementCheckBox() {
    }

    /**
     * Generates the HTML code.
     */
    protected String generate() {
        String html = "";
        if (selected != null && selected.equals("null")) {
            sel = false;
        }
        if (exclude != null && exclude.equals("null")) {
            ex = false;
        }
        if (moreValues) {
            String val = null;
            String basic = "<input type=\"checkbox\" name=\"" +name+ "\" value=\"";
            Enumeration e = Collections.enumeration(valuesList);

            Vector list = new Vector();

            String brk="";
            if (vertical) {
              brk="<br />\n";
            }
            while (e.hasMoreElements()) {
                val = (String) e.nextElement();
                if (sel && selected.equalsIgnoreCase(val)) {
                    list.addElement(basic +val + "\" checked >" + val + brk);
                } else if (!ex || (!exclude.equalsIgnoreCase(val))) {
                    list.addElement(basic +val + "\" >" + val + brk);
                }
/**                if (sel) {
                    if (selected.equalsIgnoreCase(val)) {
                        if (vertical) {
                            list.addElement(basic +val + "\" checked> " + val + "<br />\n");
                        } else {
                            list.addElement(basic +val + "\" checked> " + val);
                        }
                    } else {
                        if (ex) {
                            if (!exclude.equalsIgnoreCase(val)) {
                                if (vertical) {
                                    list.addElement(basic + val + "\"> " + val + "<br />\n");
                                } else {
                                    list.addElement(basic + val + "\"> " + val);
                                }
                            }
                        } else {
                            if (vertical) {
                                list.addElement(basic + val + "\"> " + val + "<br />\n");
                            } else {
                                list.addElement(basic + val + "\"> " + val);
                            }
                        }
                    }
                } else {
                    if (ex) {
                        if (!exclude.equalsIgnoreCase(val)) {
                            if (vertical) {
                                list.addElement(basic + val + "\"> " + val + "<br />\n");
                            } else {
                                list.addElement(basic + val + "\"> " + val);
                            }
                        }
                    } else {
                        if (vertical) {
                            list.addElement(basic + val + "\"> " + val + "<br />\n");
                        } else {
                            list.addElement(basic + val + "\"> " + val);
                        }
                    }
                }
*/
            }
            Enumeration le = list.elements();
            int i=0;
            String h = "";
            while(le.hasMoreElements()) {
                while( i<22 && le.hasMoreElements()) {
                    h += (String) le.nextElement();
                    i++;
                }
                html += h;
                h = "";
                i = 0;
            }
        } else if (moredouble) {
            String val = null;
            String basic = "<input type=\"checkbox\" name=\"" +name+ "\" value=\"";
            Enumeration e = Collections.enumeration(valuesList);

            Vector list = new Vector();

            String brk="";
            if (vertical) {
              brk="<br />\n";
            }
            while (e.hasMoreElements()) {
                val = (String) e.nextElement();
                String val2 ;
                if (e.hasMoreElements()) {
                    val2= (String) e.nextElement();
                } else {
                     log.warn("HTMLElementCheckBox.generate: Expecting a double list (the DOUBLE key word was selected");
                     return html;
                }
                if (sel && selected.equalsIgnoreCase(val)) {
                    list.addElement(basic +val2 + "\" checked >" + val + brk);
                } else if (!ex || (!exclude.equalsIgnoreCase(val))) {
                    list.addElement(basic +val2 + "\" >" + val + brk);
                }
/**
                if (sel) {
                    if (selected.equalsIgnoreCase(val)) {
                        if (vertical) {
                            list.addElement(basic +val2 + "\" CHECKED> " + val + "<BR>\n");
                        } else {
                            list.addElement(basic +val + "\" CHECKED> " + val);
                        }
                    } else {
                        if (ex) {
                            if (!exclude.equalsIgnoreCase(val)) {
                                if (vertical) {
                                    list.addElement(basic + val2 + "\"> " + val + "<BR>\n");
                                } else {
                                    list.addElement(basic + val2 + "\"> " + val);
                                }
                            }
                        } else {
                            if (vertical) {
                                list.addElement(basic + val2 + "\"> " + val + "<BR>\n");
                            } else {
                                list.addElement(basic + val2 + "\"> " + val);
                            }
                        }
                    }
                } else {
                    if (ex) {
                        if (!exclude.equalsIgnoreCase(val)) {
                            if (vertical) {
                                list.addElement(basic + val2 + "\"> " + val + "<BR>\n");
                            } else {
                                list.addElement(basic + val2 + "\"> " + val);
                            }
                        }
                    } else {
                        if (vertical) {
                            list.addElement(basic + val2 + "\"> " + val + "<BR>\n");
                        } else {
                            list.addElement(basic + val2 + "\"> " + val);
                        }
                    }
                }
*/            }
            Enumeration le = list.elements();
            int i=0;
            String h = "";
            while(le.hasMoreElements()) {
                while( i < 22 && le.hasMoreElements()) {
                    h += (String) le.nextElement();
                    i++;
                }
                html += h;
                h = "";
                i = 0;
            }
        } else {
            html += "<input  type=\"checkbox\" name=\""+name + "\" ";
            if (sel) html += "checked ";
            html +=    "value=\"" + values + "\">" + values ;
        }
        return html;
    }
}
