/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib.pageflow;

import java.util.*;
import java.io.*;
import org.mmbase.bridge.jsp.taglib.*;
import org.mmbase.bridge.jsp.taglib.util.Attribute;
import org.mmbase.bridge.jsp.taglib.util.Referids;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspException;


import org.mmbase.util.transformers.Url;
import org.mmbase.util.transformers.CharTransformer;

import org.mmbase.util.Casting;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;


/**
 * A Tag to produce an URL with parameters. It can use 'context' parameters easily.
 *
 * @author Michiel Meeuwissen
 * @version $Id: UrlWriterTag.java,v 1.1 2004-12-10 20:25:11 michiel Exp $
 */

public class UrlWriterTag extends UrlTag  implements org.mmbase.bridge.jsp.taglib.Writer {

    public int doStartTag() throws JspTagException {
        getContextProvider().getContextContainer().register("_", 
                                                            new Object() {
                                                                final UrlWriterTag t = UrlWriterTag.this;
                                                                public String toString() {
                                                                    try {
                                                                        return t.getUrl();  
                                                                    } catch (JspTagException e){
                                                                        throw new RuntimeException(e);
                                                                    }

                                                                }
                                                            });
        return super.doStartTag();
    }


    public int doEndTag() throws JspTagException {
        helper.overrideWrite(false);

        if (getId() != null) {
            getContextProvider().getContextContainer().register(getId(), getUrl(false, false));  // write it as cleanly as possible in the context.
        } 
        doAfterBodySetValue();
        extraParameters = null;
        return helper.doEndTag();
    }

    public int doAfterBody() throws JspException {
        return helper.doAfterBody();
    }


}
