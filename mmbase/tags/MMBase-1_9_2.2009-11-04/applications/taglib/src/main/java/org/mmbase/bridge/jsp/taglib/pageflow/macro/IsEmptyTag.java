/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib.pageflow.macro;

import javax.servlet.jsp.JspTagException;
import org.mmbase.bridge.jsp.taglib.pageflow.CompareTag;

/**
 * Checks if the parent writer is not empty.
 * 
 * @author Michiel Meeuwissen
 * @version $Id$
 */

public class IsEmptyTag extends CompareTag  {
    protected Object getCompare2() throws JspTagException {
        return "";
    }
}
