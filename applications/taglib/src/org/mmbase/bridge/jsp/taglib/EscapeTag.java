/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib;
import org.mmbase.bridge.jsp.taglib.util.*;
import javax.servlet.jsp.JspTagException;
import javax.servlet.http.*;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import java.util.*;

/**

 * @author Michiel Meeuwissen
 * @version $Id: EscapeTag.java,v 1.1 2007-07-30 16:38:24 michiel Exp $
 */

public class EscapeTag extends ContextReferrerTag {
    private static final Logger log = Logging.getLoggerInstance(EscapeTag.class);

    
    public int doStartTag() throws JspTagException {
        return EVAL_BODY_BUFFERED;
    }

    public int doEndTag() throws JspTagException {        
        helper.setValue(bodyContent != null ? bodyContent.getString() : "");
        helper.doEndTag();
        super.doEndTag();
        return EVAL_PAGE;
    }


}
