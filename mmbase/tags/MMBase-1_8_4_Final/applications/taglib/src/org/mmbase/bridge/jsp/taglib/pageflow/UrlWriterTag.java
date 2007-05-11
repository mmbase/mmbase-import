/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib.pageflow;


import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.Tag;

import org.mmbase.bridge.jsp.taglib.Writer;
import org.mmbase.util.Casting;
import org.mmbase.util.logging.*;


/**
 * True write version of mm:url (named mm:link). Meaning that in it's body ${_} is available (and that the body is evaluated).
 * Can be used with EL. ${_} is only evaluated when used.
 *
 * @author Michiel Meeuwissen
 * @version $Id: UrlWriterTag.java,v 1.11 2006-06-23 15:29:06 michiel Exp $
 * @since MMBase-1.8
 */

public class UrlWriterTag extends UrlTag  implements Writer {
    private static final Logger log                   = Logging.getLoggerInstance(UrlTag.class);

    public int doStartTag() throws JspTagException {
        super.doStartTag();
        helper.setValue(new Comparable() {
                            final UrlWriterTag t = UrlWriterTag.this;
                            public String toString() {
                                try {
                                    String string = t.getUrl();
                                    // this means that it is written to page by ${_} and that consequently there _must_ be a body.
                                    // this is needed when body is not buffered.
                                    haveBody();
                                    return string;
                                } catch (Throwable e){
                                    return e.toString();
                                }
                            }
                            public int compareTo(Object o) {
                                return toString().compareTo(Casting.toString(o));
                            }
                        });
        return EVAL_BODY; // lets try _not_ buffering the body.
        // this may give unexpected results if ${_} is not used (or another tag calling 'haveBody')
        // But the whole goal is to use ${_} and it is a waist to buffer for nothing.
    }


    protected void initDoEndTag() throws JspTagException {

    }

    public int doAfterBody() throws JspException {
        return helper.doAfterBody();
    }


}
