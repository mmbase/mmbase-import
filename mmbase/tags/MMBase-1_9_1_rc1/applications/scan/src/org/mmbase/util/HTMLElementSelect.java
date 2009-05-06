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
 * Generates a HTML Element: SELECT.
 * Uses these variables which are set in the
 * super class (HTMLElement) to generate HTML:
 * <ul>
 * <li>boolean sel        : if true it checks if the String selected equals the
 *                          current value if equal the HTML tag CHECKED is added
 *                          after OPTION.</li>
 * <li>String selected    : see above   </li>
 * <li>boolean ex         : if true it checks if the String exclude equals the current
 *                          value, if equal this value will be skipped (no HTML
 *                          generated for this item)</li>
 * <li>String exclude     : see above</li>
 * <li>boolean moreValues : if true it will make a list of items.</li>
 * <li>boolean moredouble : if true it will make a paired list of items.
 *                          (first item = VALUE second item=NAME)</li>
 * <li>Vector valuesList  : The list of items. </li>
 * <li>String size        : if not null the HTML tag SIZE=size is added </li>
 * <li>boolean multiple   : if true the HTML tag MULTIPLE is added.</li>
 * <li>boolean empty      : if true an empty option value is added.</li>
 * </ul>
 *
 * @application SCAN
 * @author Jan van Oosterom
 * @version $Id: HTMLElementSelect.java,v 1.8 2008-09-03 15:23:39 michiel Exp $
 */

public class HTMLElementSelect  extends HTMLElement  {

    // logger
    private static Logger log = Logging.getLoggerInstance(HTMLElementSelect.class.getName());

    /**
     * Creates a HTMLElementSelect.
     */
    public HTMLElementSelect() {
    }

    /**
     * Generates the HTML code.
     */
    protected String generate() {
        //    log.debug("generate");
        if (selected != null && selected.equals("null")) {
            sel = false;
        }
        if (exclude != null && exclude.equals("null")) {
            ex = false;
        }
        String html = "";
        if (moreValues) {
            html += "<select name=\"" + name + "\" ";
            if (size != null) html += "size=\"" + size+"\"";
            if (multiple) {
                html += " multiple";
            }
            html += ">";
            if (empty) html += "<option></option>";

            String val = null;
            Vector list = new Vector();
            List vec = valuesList;
            // log.debug("ServScan->"+vec);
            if (sorted!=null && (sorted.equals("ALPHA") || sorted.equals("\"ALPHA\""))) {
                vec=SortedVector.SortVector(vec);
            }
            Enumeration e = Collections.enumeration(vec);
            int j=0;
            while (e.hasMoreElements() && ((j++<max)||max==-1)) {
                val = (String) e.nextElement();
                if (sel && selected.equalsIgnoreCase(val)) {
                    list.addElement("<option selected>" + val + "</option>\n");
                } else if (!ex || (!exclude.equalsIgnoreCase(val))) {
                    list.addElement("<option>" + val + "</option>\n");
                }
/*
                if (sel) {
                    if (selected.equalsIgnoreCase(val)) {
                        list.addElement ("<OPTION SELECTED>" + val + "\n");
                    } else {
                        if (ex) {
                            if (!exclude.equalsIgnoreCase(val)) {
                                list.addElement("<OPTION>" + val + "\n");
                            }
                        } else {
                            list.addElement("<OPTION>" + val + "\n");
                        }
                    }
                }  else {
                    if (ex) {
                        if (!exclude.equalsIgnoreCase(val))
                            list.addElement("<OPTION>" + val +"\n");
                    } else {
                        list.addElement("<OPTION>" + val +"\n");
                    }
                }
*/
            }
            Enumeration le = list.elements();
            int i=0;
            String h = "";
            while(le.hasMoreElements())
            {
                while( i < 22 && le.hasMoreElements() )
                {
                    h += (String) le.nextElement();
                    i++;
                }
                html += h;
                h = "";
                i = 0;
            }
            html += "</select>" ;
        }
        else if (moredouble)
        {
            //log.debug("moredouble");
            html += "<select name=\"" + name + "\" ";
            if (size != null) html += "size=\"" + size+"\"";
            if (multiple) {
                html += " multiple";
            }
            html += ">";
            if (empty) html += "<option></option>";

            String val = null;
            String val2 = null;
            Vector list = new Vector();
            Enumeration e = Collections.enumeration(valuesList);
            while (e.hasMoreElements()) {
                val = (String) e.nextElement();
                if (e.hasMoreElements()) {
                    val2 = (String) e.nextElement();
                } else {
                    log.error("HTMLElementSelect.generate: Expecting a double list (the DOUBLE key word was selected");
                    return html;
                }
                if (sel && selected.equalsIgnoreCase(val)) {
                    list.addElement("<option value=\""+val2+"\" selected>" + val + "</option>\n");
                } else if (!ex || (!exclude.equalsIgnoreCase(val))) {
                    list.addElement("<option value=\""+val2+"\">" + val + "</option>\n");
                }
/**
                if (sel)
                {
                    if (selected.equalsIgnoreCase(val2))
                    {
                        list.addElement ("<OPTION VALUE=\"" + val2 + "\" SELECTED>" + val + "\n");
                    }
                    else
                    {
                        if (ex)
                        {
                            if (!exclude.equalsIgnoreCase(val2))
                                list.addElement("<OPTION VALUE=\"" + val2 + "\">" + val + "\n");
                        }
                        else
                        {
                                list.addElement("<OPTION VALUE=\"" + val2 + "\">" + val + "\n");
                        }
                    }
                }
                else
                {
                    if (ex)
                    {
                        if (!exclude.equalsIgnoreCase(val2))
                            list.addElement("<OPTION VALUE=\"" + val2 + "\">" + val +"\n");
                    }
                    else
                    {
                        list.addElement("<OPTION VALUE=\"" + val2 + "\">" + val +"\n");
                    }
                }
*/
            }
            //log.debug("after");
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
            html += "</select>" ;
        } else {
            html += "<select name=\"" + name + "\" ";
            if (size != null) html += "size=\"" + size+"\"";
            if (multiple) {
                html += " multiple";
            }
            html += ">";
            html +=    "<option ";
            if (sel) html += " selected";
            html += ">" + values + "</option></select>" ;
        }
        return html;
    }
}
