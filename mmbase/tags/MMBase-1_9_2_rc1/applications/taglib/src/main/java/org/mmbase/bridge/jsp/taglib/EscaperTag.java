/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib;

import org.mmbase.bridge.jsp.taglib.util.Attribute;
import org.mmbase.util.functions.Parameters;
import org.mmbase.util.transformers.*;
import javax.servlet.jsp.*;
import org.mmbase.util.logging.*;

/**
 * Configures a new Escaper on this page.
 * 
 * @author Michiel Meeuwissen
 * @version $Id$
 * @since MMBase-1.8
 */

public class EscaperTag extends ContextReferrerTag implements ParamHandler {

    private static final Logger log = Logging.getLoggerInstance(EscaperTag.class);
    protected Parameters  parameters;
    protected CharTransformer transformer;
    
    private Attribute type    = Attribute.NULL;
    private Attribute referid = Attribute.NULL;
    private Attribute inverse = Attribute.NULL;
           
    private ParameterizedTransformerFactory factory;

    public void setType(String t) throws JspTagException {
        type = getAttribute(t);
    }
    public void setReferid(String r) throws JspTagException {
        referid = getAttribute(r);
    }

    public void setInverse(String i) throws JspTagException {
        inverse = getAttribute(i);
    }

    public void addParameter(String key, Object value) throws JspTagException {
        parameters.set(key, value);
    }

    public void chain(CharTransformer trans) throws JspTagException {
        if (transformer == null) {
            throw new JspTagException("This Escaper tag is not a chain (it defines a type)");
        }
        if (! (transformer instanceof ChainedCharTransformer)) {
            throw new JspTagException("Transformer " + transformer + " is not a chain");
        }
        ((ChainedCharTransformer) transformer).add(trans);
    }

    public int doStartTag() throws JspException {
        if (referid != Attribute.NULL) {
            transformer = ContentTag.getCharTransformer(referid.getString(this), this);
            if (transformer == null) transformer = ContentTag.COPY;
        } else {
            if (type != Attribute.NULL) {
                factory = ContentTag.getTransformerFactory(type.getString(this));
                parameters = factory.createParameters();
                parameters.setAutoCasting(true);
                fillStandardParameters(parameters);
            } else {
                transformer = new ChainedCharTransformer();
            }
        }
        if (inverse.getBoolean(this, false)) {
            transformer = new InverseCharTransformer(transformer);
        }

        return super.doStartTag();
    }


    public int doEndTag() throws JspTagException {
        if(transformer == null) {
            if (log.isDebugEnabled()) {
                log.debug("Parameters " + parameters);
            }
            transformer = (CharTransformer) factory.createTransformer(parameters);
        }
        if (getId() != null) {
            getContextProvider().getContextContainer().register(getId(), transformer);
        } else {
            EscaperTag parent = findParentTag(EscaperTag.class, null, false);
            if (parent == null) throw new JspTagException("EscaperTag without id must live as child of another Escaper tag");
            parent.chain(transformer);

        }
        factory = null;
        parameters = null;
        transformer = null;
        return super.doEndTag();
    }

    public void doFinally() {
        factory = null;
        parameters = null;
        transformer = null;
        super.doFinally();
    }
}
