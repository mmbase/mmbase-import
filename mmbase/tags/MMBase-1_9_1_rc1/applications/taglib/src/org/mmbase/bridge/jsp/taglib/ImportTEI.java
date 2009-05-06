/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib;

import javax.servlet.jsp.tagext.VariableInfo;

/**
 * TEI class for the ImportTag. Only differs from WriterTEI in scope and default.
 *
 * @author Michiel Meeuwissen
 * @version $Id: ImportTEI.java,v 1.6 2005-05-02 22:23:35 michiel Exp $ 
 */
public class ImportTEI extends WriterTEI {
    
    protected int scope() {
        return VariableInfo.AT_END;
    }
    protected String defaultType() {
        return "string";
    }
}
