/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib.containers;

import org.mmbase.bridge.jsp.taglib.*;
import org.mmbase.bridge.jsp.taglib.util.Attribute;

import java.util.*;
import org.mmbase.util.Argument;
import org.mmbase.util.Arguments;
import javax.servlet.jsp.JspTagException;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import java.lang.reflect.*;

/**
cd a * Function Container can be used around Function (-like) Tags
 *
 * @author Michiel Meeuwissen
 * @since  MMBase-1.7
 * @version $Id: FunctionContainerTag.java,v 1.3 2003-08-12 17:10:21 michiel Exp $
 */
public class FunctionContainerTag extends ContextReferrerTag implements FunctionContainer {
    private static Logger log = Logging.getLoggerInstance(FunctionContainerTag.class);

    private List       parameters;
    protected Attribute argumentsDefinition = Attribute.NULL;
        

    /**
     * Temporary? Using reflection to get the right definition constant in the tag
     */
    public void setArgumentsdefinition(String a) throws JspTagException {
        argumentsDefinition = getAttribute(a);
    }


    // javadoc inherited (from ParamHandler)
    public void addParameter(String key, Object value) throws JspTagException {
        if (parameters instanceof Arguments) {
            Arguments a = (Arguments) parameters;
            a.set(key, value);
        } else {
            parameters.add(value);
        }
    }

    // javadoc inherited (from FunctionContainer)
    public List  getParameters() {
        return Collections.unmodifiableList(parameters);
    }


    public int doStartTag() throws JspTagException { 
        if (argumentsDefinition != Attribute.NULL) {
            Argument[] definition;    
            String def = argumentsDefinition.getString(this);
            // find the class it is in.
            int i = def.lastIndexOf('.');
            String className = def.substring(0, i);
            String fieldName = def.substring(i + 1);

            try {
                Class definingClass = Class.forName(className);
                Field constant      = definingClass.getField(fieldName);
                
                definition = (Argument[]) constant.get(null);
            } catch (Exception e) {
                throw new JspTagException(e.toString());
            }
            parameters = new Arguments(definition);
        } else {
            parameters = new ArrayList();
        }
        return EVAL_BODY_BUFFERED;
    }

    public int doAfterBody() throws JspTagException {
        try {
            if (bodyContent != null) {
                bodyContent.writeOut(bodyContent.getEnclosingWriter());
            }
        } catch (java.io.IOException ioe){
            throw new JspTagException(ioe.toString());
        } 
        return SKIP_BODY;        
    }


}
