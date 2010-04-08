/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib.pageflow;

import org.mmbase.bridge.jsp.taglib.WriterTEI;
import javax.servlet.jsp.tagext.VariableInfo;

/**
 * The TEI class for UrlTag. If 'jspvar' attribute is defined, then a
 * UrlTag will not output an url but set a variable with it. That
 * variable cannot be used in jsp:include, so it's probably not that usefull.
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 */

public class UrlTEI extends  WriterTEI {
    
    protected int scope() {
        return VariableInfo.AT_END;
    }
    protected String defaultType() {
        return "String";
    }
        
}
