/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib.pageflow;

import org.mmbase.bridge.jsp.taglib.Writer;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspException;


import org.mmbase.util.Casting;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;


/**
 * True write version of mm:url (named mm:link). Meaning that in it's body ${_} is available (and that the body is evaluated).
 * Can be used with EL. ${_} is only evaluated when used.
 *
 * @author Michiel Meeuwissen
 * @version $Id: UrlWriterTag.java,v 1.6 2005-01-05 22:25:05 michiel Exp $
 * @since MMBase-1.8
 */

public class UrlWriterTag extends UrlTag  implements Writer {

    private static final Logger log = Logging.getLoggerInstance(UrlWriterTag.class);

    public int doStartTag() throws JspTagException {
        super.doStartTag();
        helper.setValue(
                        new Comparable() {
                            final UrlWriterTag t = UrlWriterTag.this;
                            public String toString() {
                                try {
                                    String string = t.getUrl();
                                    return string;  
                                } catch (Throwable e){
                                    return e.toString();
                                }
                                
                            }
                            public int compareTo(Object o) {
                                return toString().compareTo(Casting.toString(o));
                            }
                        });
        return EVAL_BODY;
    }


    public int doEndTag() throws JspTagException {
        if (getId() != null) {
            getContextProvider().getContextContainer().register(getId(), getUrl(false, false));  // write it as cleanly as possible in the context.
        } 
        helper.doEndTag();
        extraParameters = null;
        return javax.servlet.jsp.tagext.BodyTagSupport.EVAL_PAGE;
        
    }

    public int doAfterBody() throws JspException {
        return helper.doAfterBody();
    }


}
