/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib;

import java.util.List;

import javax.servlet.jsp.JspTagException;

import org.mmbase.bridge.jsp.taglib.containers.*;
import org.mmbase.bridge.jsp.taglib.util.Attribute;
import org.mmbase.util.logging.*;

/**
 * The Function tag can be used as a child of a 'NodeProvider' tag (but not on clusternodes?). It
 * can call functions on the node.
 *
 * @author  Michiel Meeuwissen
 * @since   MMBase-1.7
 * @version $Id: AbstractFunctionTag.java,v 1.3 2003-08-29 12:12:25 keesj Exp $
 */
public class AbstractFunctionTag extends NodeReferrerTag implements FunctionContainerReferrer {

    private static final Logger log = Logging.getLoggerInstance(AbstractFunctionTag.class);

    protected Attribute container   = Attribute.NULL;
    protected Attribute name        = Attribute.NULL;
    protected Attribute parameters  = Attribute.NULL;


    public void setName(String n) throws JspTagException {
        name = getAttribute(n);
    }

    public void setContainer(String c) throws JspTagException {
        container = getAttribute(c); 
    }

    public void setParameters(String p)  throws JspTagException {
        parameters = getAttribute(p);
    }



    protected Object getFunctionValue() throws JspTagException {
        FunctionContainer functionContainer = (FunctionContainer) findParentTag(FunctionContainer.class, (String) container.getValue(this), false);

        List arguments;
        String functionName;

        if (functionContainer != null) {
            arguments    = functionContainer.getParameters();
        } else {    
            arguments =  parameters.getList(this);
            if (name == Attribute.NULL) {
                throw new JspTagException("Should give name of function");
            }
         }

        functionName = name.getString(this);
        Object value = getNode().getFunctionValue(functionName, arguments).get();
        if (getId() != null) {
            getContextProvider().getContextContainer().register(getId(), value);
        }
        return value;
    }


}
