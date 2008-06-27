/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib;

/**
 * A TEI class for Writer Tags that produce Integer jsp vars.
 *
s * @author Michiel Meeuwissen
 * @version $Id: IntegerWriterTEI.java,v 1.3 2006-04-12 14:51:40 michiel Exp $ 
 */

public class IntegerWriterTEI extends  WriterTEI {
    protected String defaultType() {
        return "Integer";
    }
}
