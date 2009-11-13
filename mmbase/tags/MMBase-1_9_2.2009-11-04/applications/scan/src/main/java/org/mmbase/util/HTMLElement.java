/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util;

import java.util.*;


import org.mmbase.module.ProcessorModule;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
* Class which is the super-class for ALL the HTMLElements.
*
* Includes a parser wich fills all the variables
* and calls the abstract function generate which will be
* implemented in a HTMLElement
*
* @application SCAN
* @author Jan van Oosterom
* @version $Id$
*
*/
public abstract class HTMLElement {
    // logger
    private static Logger log = Logging.getLoggerInstance(HTMLElement.class.getName());

    /**
    * The processor which will be called by the parse routine.
    */
    protected ProcessorModule processor = null;

    /**
    * The value what will be put after the NAME= tag
    */
    protected String name = null;

    /**
    * The value what will be passed to the Processor or will be the value
    * of the item. (PROC -&gt;Processor else the value)
    */
    protected String values = null;

    /**
    * contains the Vector with the items returned by the ProcessorInteface.getList(value) if the PROC tag is present
    */
    protected List valuesList = null;

    /**
    * added to the SIZE= tag
    */
    protected String size = null;

    /**
    * The element which will be selected (e.g.: &lt;OPTION SELECTED&gt;)
    */
    protected String selected = null;

    /**
    * added to ROWS=
    */
    protected String rows = null;

    /**
    * added to COLS=
    */
    protected String cols = null;

    /**
    * added to MAX=
    */
    protected int max = -1;

    /**
    * added to Sorted=
    */
    protected String sorted = null;

    /**
    * if this element is in the ValueList from the Processor it should be excluded
    */
    protected String exclude = null;

    /**
    * if true there is an excluded item
    */
    protected boolean ex = false;

    /**
    * if true it the Element should allow MULTIPLE selection
    */

    protected boolean multiple = false;
    /**
    * There is a (single) list of items (from the Processor)
    */
    protected boolean moreValues = false;

    /**
    * There is a double list of items (from the Processor)
    */
    protected boolean moredouble = false;

    /**
    * if true there is a Selected item
    */
    protected boolean sel = false;

    /**
    * The Item list should be VERTICAL (a &lt;BR&gt; tag should be added)
    */
    protected boolean vertical = false;

    /**
    * The Item list should be HORIZONTAL This is default
    */
    protected boolean horizontal = false;

    /**
    * The EMPTY tag is present in the macro.
    * There should be a EMPTY SELECTION OPTION item added to the list
    */
    protected boolean empty = false;

    /**
    * The PROC tag is present in the macro.
    * The processor will be called and should return a Vector with items
    */
    protected boolean proc = false;

    /**
    * The DOUBLE tag is present in the macro.
    * The processor will be called and should return a Vector with items pairs
    * 1st = item1a, 2nd= item1b, 3th=item2a, 4th=item 2b, etc
    */
    protected boolean procdouble = false;

    /**
    * The user name that is currently acessing this page
    */
    protected String user =null;

    /**
    * page from this request
    */
    //protected HttpServletRequest rq =null;
    protected scanpage sp=null;

    /**
    * empty
    */
    public HTMLElement() {
    }

    /**
    * Calls the parser and returns the String returned by generate.
    */
    protected String generateHTML(scanpage sp,ProcessorModule proc, Vector macro) {
        log.debug("generateHTML");
        processor=proc;
        //this.user = user;
        this.sp = sp;
        log.debug("marco: " + macro);
        boolean ok = parse(macro);
        //log.debug("after parse");
        if (!ok) return "";
        return generate();
    }
    /**
    * Should be implemented by the subclass and and should return the generated HTML.
    */
    protected abstract String generate();

    /**
    * Reads the Vector and fills all the variables if the corespondending variables.
    */
    protected boolean parse (Vector macro) {
        name = null;
        values = null;
        size = null;
        selected = null;
        rows = null;
        cols = null;
        exclude = null;
        sorted = null;
        max=-1;

        ex = false;
        multiple = false;
        sel = false;
        moreValues = false;
        moredouble = false;
        horizontal = false;
        vertical = false;
        empty = false;
        proc = false;
        procdouble = false;

        Enumeration e = macro.elements();
        if (e.hasMoreElements()) {
            name = (String) e.nextElement();
        } else {
            log.error("HTMLElement:  to few params, no name !!!");
            return false;
        }

        if (e.hasMoreElements()) {
            values = (String) e.nextElement();
            if (this instanceof HTMLElementText) {
                //log.debug("HTMLElement instanceof Text, values: " + values);
                if (values.indexOf("\"")==0) {
                    values= values.substring(1,values.length()-1);
                }
            }
        } else if (!(this instanceof HTMLElementText ||
                     this instanceof HTMLElementPassword)) {
            log.error("HTMLElement:  to few params, no value !!!");
            return false;
        }

        while (e.hasMoreElements()) {
            String str = (String) e.nextElement();

            String type = getType(str);
            if (type == null) {
                return false;
            }
            String value ;
            value = getValue(str);
            if ((type.equalsIgnoreCase("MULTIPLE") && value == null) ||
                (type.equalsIgnoreCase("CHECKED") && value == null) ||
                (type.equalsIgnoreCase("MAX") && value == null) ||
                (type.equalsIgnoreCase("SELECTED") && value == null) ||
                (type.equalsIgnoreCase("VERTICAL") && value == null) ||
                (type.equalsIgnoreCase("EMPTY") && value == null) ||
                (type.equalsIgnoreCase("PROC") && value == null) ||
                (type.equalsIgnoreCase("DOUBLE") && value == null) ||
                (type.equalsIgnoreCase("SORTED") && value == null) ||
                (type.equalsIgnoreCase("HORIZONTAL") && value == null)) {
                value = "whatever";
            }

            if (type != null && value != null ) {
                if (type.equalsIgnoreCase("SIZE")) {
                    size = value;
                } else if ((type.equalsIgnoreCase("CHECKED")) || (type.equalsIgnoreCase("SELECTED"))) {
                    selected = value;
                    if (selected.charAt(0)== '\"') {
                        selected = selected.substring(1,selected.length()-1);
                    }
                    sel = true;
                } else if (type.equalsIgnoreCase("ROWS")) {
                    rows = value;
                } else if (type.equalsIgnoreCase("DOUBLE")) {
                    procdouble = true;
                } else if (type.equalsIgnoreCase("PROC")) {
                    proc = true;
                } else if (type.equalsIgnoreCase("SORTED")) {
                    sorted = value;
                } else if (type.equalsIgnoreCase("EXCLUDE")) {
                    ex = true;
                    exclude = value;
                    if (exclude.charAt(0)== '\"') {
                        exclude = exclude.substring(1,exclude.length()-1);
                    }
                } else if (type.equalsIgnoreCase("MULTIPLE")) {
                    multiple = true;
                } else if (type.equalsIgnoreCase("VERTICAL")) {
                    vertical = true;
                } else if (type.equalsIgnoreCase("HORIZONTAL")) {
                    horizontal = true;
                } else if (type.equalsIgnoreCase("COLS")) {
                    cols = value;
                } else if (type.equalsIgnoreCase("MAX")) {
                    try {
                        max=Integer.parseInt(value);
                    } catch (Exception f) {};
                } else if (type.equalsIgnoreCase("PROCESSOR")) {
                    // get processor not used at this time !!!
                } else if (type.equalsIgnoreCase("EMPTY")) {
                    empty = true;
                } else {
                    log.error("HTMLElement.parse unknow MACRO HTML found: " + type + "=" + value);
                }
            }
        }

        if (proc && !procdouble) {
            //We found a PROC tag so put it in the Processor
            valuesList = null;
            // what is this, should tagger be empty ?
            try {
              valuesList = processor.getList(sp,new StringTagger(""),values);
            } catch (RuntimeException pe) {}
            if (valuesList == null) {
                //moreValues = false;
                //values = "";
                log.error("HTMLElement.parse: The processor return null !!");
                return false;
            } else {
                moreValues = true;
            }
        }

        if (procdouble) {
            //We found a DOUBLE tag so put it in the Processor
            valuesList = null;
            try {
                // what is this, should tagger be empty ?
                valuesList = processor.getList(sp,new StringTagger(""),values);
            } catch (RuntimeException pe) {}
            if (valuesList == null) {
                log.error("HTMLElement.parse: The processor returned null !!");
                return false;
                //moredouble  = false;
                //values = "";
            } else {
                moredouble = true;
            }
        }
        return true;
    }

    /**
    * Returns the part in front of the '=' char of the String.
    */
    protected String getType(String str) {
        StringTokenizer tok = new StringTokenizer(str,"=");
        if (tok.hasMoreTokens()) {
            return (String)tok.nextElement();
        }
        log.error("getType: invalid format ...!!!"+str);
        return null;
    }

    /**
    * Returns the part after the '=' char of the String.
    */
    protected String getValue(String str) {
        StringTokenizer tok = new StringTokenizer(str,"=");
        if (tok.hasMoreTokens()) {
            // nextToken() would only return a Object dummy, becaue we are only
            // interested in the value after the =
            tok.nextElement();
            if (tok.hasMoreTokens()) {
                return (String) tok.nextElement();
            }
        }
        // no value found
        return null;
    }
}
