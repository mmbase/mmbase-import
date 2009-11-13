/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util;

import java.util.Enumeration;
import java.util.Vector;

import org.mmbase.module.ProcessorModule;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * Generates from the MACRO Strings a HTML FORM.
 *<br />
 * <strong>input</strong> Macro Vector of Strings, Proc Processor.<br />
 * <strong>output</strong> HTML FORM Element(String).<br />
 * <p>
 * How should a macro look like in your HTML file?<br />
 * &lt;MACRO ELEMENT NAME VALUE [TAG(S)]&gt;<br />
 * The "MACRO" word will be stripped in de servscan Servlet and does not enters this object.<br />
 * ELEMENT: (if you are fimiliar with HTML FORMs this should be known stuff   <br />
 * TEXTAREA, SELECT, PASSWORD, RADIO, CHECKBOX  <br />
 * NAME:<br />
 * The name of this HTML Element <br />
 * VALUE:<br />
 * This can do 2 things: <br />
 * 1. if the PROC TAG is present, this value will be sent to the processor <br />
 * 2. else ( no PROC TAG) this will be the VALUE of the HTML Element <br />
 * TAG(S) <br />
 * there can be more TAGS (some TYPES does support them and some don't ...)<br />
 * some need an argument some don't (some can handle both :-)<br />
 * <pre>
 * TAG                     WHAT DOES IT?                                                 SUPPORTED BY WHICH ELEMENT?
 * <hr />
 * ROWS=                   The number of ROWS                                            TEXTAREA
 * COLS=                   The number of COLS                                            TEXTAREA
 * SIZE=                   The SIZE of this ELEMENT                                      SELECT TEXT PASSWORD
 * MULTIPLE                We want MULTIPLE selection                                    SELECT
 * CHECKED(=)/SELECTED(=)  The SELECTED element (if there is no PROC tag
 *                         you don't need an argument)                                   SELECT RADIO CHECKBOX
 * EXCLUDE=                The EXCLUDED element (only use with PROC)                     SELECT RADIO CHECKBOX
 * VERTICAL                Add a &lt;BR&gt; after a item                                 SELECT RADIO CHECKBOX
 * HORIZONTAL              Don't add a &lt;BR&gt; after a item                           SELECT RADIO CHECKBOX
 * EMPTY                   Add an EMPTY element                                          SELECT
 * PROC                    Use the processor to get a list (Vector) of values (Strings)  TEXTAREA TEXT and PASSWORD (only the first of the Vector will be used) SELECT RADIO CHECKBOX
 * DOUBLE                  Tell the processor to get a paired list of values             SELECT RADIO CHECKBOX
 * </pre>
 *
 * <strong>example</strong>: SELECT name ProcessorTag SELECTED="selected_item" MULTIPLE SIZE=10 PROC VERTICAL.<br />
 * This generates a SELECT HTML FORM with NAME=name the OPTIONs are filled with<br />
 * the list which is received from the Processor.getList("ProcessorTag") call<br />
 * If the "selected_item" is presend in the list it is "&lt;OPTION SELECTED&gt;" item" <br />
 * The SIZE is the number of displayed items and MULTIPLE tells that multiple <br />
 * selections are posible. <br />
 *  VERTICAL tells that you want a &lt;BR&gt; after every &lt;OPTION&gt;<br />
 * <br /> <strong> Generated</strong>  from <strong>example</strong>:<br />
 * &lt;SELECT NAME=name SIZE=10 MULTIPLE&gt; <br />
 * &lt;OPTION&gt; item1 &lt;BR&gt;<br />
 * &lt;OPTION SELECTED&gt; sleceted_item &lt;BR&gt;<br />
 * &lt;OPTION&gt; item3 &lt;BR&gt;<br />
 * &lt;/SELECT&gt; <br />
 *<P>
 *
 *
 * @application SCAN
 * @author Jan van Oosterom
 * @version $Id$
 */
public class HTMLFormGenerator {
    // logger
    private static Logger log = Logging.getLoggerInstance(HTMLFormGenerator.class.getName());

    /**
    *  TEXTAREA Element
    */
    protected HTMLElementTextArea textArea;

    /**
    * RADIO Element
    */
    protected HTMLElementRadio     radio;
    /**
    * SELECT Element
    */
    protected HTMLElementSelect     select;
    /**
     * CHECKBOX Element
    */
    protected HTMLElementCheckBox checkBox;
    /**
    * TEXT
    */
    protected HTMLElementText     text;
    /**
    * PASSWORD
    */
    protected HTMLElementPassword password;

    /**
    *    Contructs the HTMLElements
    */
    public HTMLFormGenerator() {
        textArea = new HTMLElementTextArea();
        radio      = new HTMLElementRadio();
        select      = new HTMLElementSelect();
        checkBox = new HTMLElementCheckBox();
        text     = new HTMLElementText();
        password = new HTMLElementPassword();
    }

    /**
    * Gets the first element of the Vector and selects the correspondending HTMLElement
    * to handle this element, and passes the tail elements to that Element.
    * <br />
    * Output String: HTML FORM Element (TEXTAREA, RADIO, SELECT, CHECKBOX, TEXT or PASSWORD).
    * @param proc The Processor to handle the getList (2nd Element from the Vector marco)
    * @param macro The Vector with Strings
    */
    public String getHTMLElement (scanpage sp,ProcessorModule proc, Vector macro) {
        String type = getFirstElement(macro);
        Vector params = getTailElements(macro);

        if (type.equalsIgnoreCase("TEXTAREA")) {
            // we want a TEXTAREA .....
            return textArea.generateHTML(sp,proc,params);
        }

        if (type.equalsIgnoreCase("RADIO")) {
            //We want a RADIO ......
            String radioHTML = radio.generateHTML(sp,proc,params) ;
            return radioHTML;
        }

        if (type.equalsIgnoreCase("SELECT")) {
            //We want a SELECT ......
            String selectHTML = select.generateHTML(sp,proc,params) ;
            return selectHTML;
        }

        if (type.equalsIgnoreCase("CHECKBOX")) {
            //We want a CHECKBOX......
            String checkBoxHTML = checkBox.generateHTML(sp,proc,params) ;
            return checkBoxHTML;
        }

        if (type.equalsIgnoreCase("TEXT")) {
            //We want a TEXT input......
            String textHTML = text.generateHTML(sp,proc,params) ;
            return textHTML;
        }

        if (type.equalsIgnoreCase("PASSWORD")) {
            //We want a PASSWORD input......
            String passwordHTML = password.generateHTML(sp,proc,params) ;
            return passwordHTML;
        }
        log.error("HTMLFormGenerator: Unknown HTML type re quested: " + type);
        return null;
    }

    /*
    * Returns the tail elements from the vector. (All but the first).
    */
    protected Vector getTailElements(Vector vector) {
        Enumeration e = vector.elements();
        if (e.hasMoreElements()) {
            //We don't want the first one ....
            // Object dummy =
            e.nextElement();

            Vector tailToReturn = new Vector();
            //We only want the tail

            while(e.hasMoreElements()) {
                tailToReturn.addElement(e.nextElement());
            }
            return tailToReturn;
        } else {
            log.error("Empty Vector in HTMLFormGenerator");
            return null;
        }

    }
    /*
    * Returns the first element of the vector
    */
    protected String getFirstElement(Vector vector) {
        return (String) vector.elementAt(0);
    }
}
