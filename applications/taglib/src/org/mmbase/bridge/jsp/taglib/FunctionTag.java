/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib;

import org.mmbase.bridge.jsp.taglib.util.Attribute;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspException;

import org.mmbase.bridge.NotFoundException;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.Field;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;
import org.mmbase.util.Arguments;

import java.util.*;

/**
 * The Function tag can be used as a child of a 'NodeProvider' tag (but not on clusternodes?). It
 * can call functions on the node.
 *
 * @author  Michiel Meeuwissen
 * @since   MMBase-1.7
 * @version $Id: FunctionTag.java,v 1.2 2003-06-27 12:58:27 michiel Exp $
 */
public class FunctionTag extends NodeReferrerTag implements Writer, ParamHandler, FunctionContainerReferrer {

    private static Logger log = Logging.getLoggerInstance(FunctionTag.class);

    protected Attribute container = Attribute.NULL;
    protected Attribute name      = Attribute.NULL;

    protected List      parameters;

    public void setName(String n) throws JspTagException {
        name = getAttribute(n);
    }

    public void setContainer(String c) throws JspTagException {
        container = getAttribute(c); // not yet implemented
    }

    /**
     * Returns the parameter list 
     */
    protected List getParameters() { // not yet implemented
        return parameters;
    }


    public void addParameter(String key, Object value) throws JspTagException {
        if (log.isDebugEnabled()) {
            log.debug("adding parameter " + key + "/" + value);
        }
        if (parameters instanceof Arguments) { // not yet implemented
            ((Arguments) parameters).set(key, value);
        } else {
            parameters.add(value); // order is important!
        }
    }

    protected Object getFunctionValue() throws JspTagException {
        if (name == Attribute.NULL) {
            //List params = getParameters();            
            //            functionName = (String) ((Map) params.get(params.size() - 1)).get("functionname");
            throw new JspTagException("not implemented");
        }
        Object value = getNode().getFunctionValue(name.getString(this), getParameters());
        helper.setValue(value);
        if (getId() != null) {
            getContextProvider().getContainer().register(getId(), helper.getValue());
        }
        return value;
    }

    public int doStartTag() throws JspTagException {        
        if (name == Attribute.NULL) { // 'referid' was used.
            if (getReferid() != null) { // referid
                parameters = (List) getObject(getReferid());
            } else {
                throw new JspTagException("No function name specified");
            }
        } else {        
            // need some way to instantiate as Arguments...
            parameters = new ArrayList();
        }
        helper.setTag(this);
        getFunctionValue();
        log.debug("end of doStartTag");
        return EVAL_BODY_BUFFERED;
    }


    public int doAfterBody() throws JspException {
        helper.setBodyContent(getBodyContent());
        return super.doAfterBody();
    }
       
    public int doEndTag() throws JspTagException {
        return helper.doEndTag();
    }
}
