/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib.containers;

import org.mmbase.bridge.jsp.taglib.*;

import java.util.*;
import org.mmbase.util.Argument;
import org.mmbase.util.Arguments;
import javax.servlet.jsp.JspTagException;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * Function Container can be used around Function (-like) Tags
 *
 * @author Michiel Meeuwissen
 * @since  MMBase-1.7
 * @version $Id: FunctionContainerTag.java,v 1.1 2003-07-23 17:46:58 michiel Exp $
 */
public class FunctionContainerTag extends ContextReferrerTag implements FunctionContainer {


    private static Logger log = Logging.getLoggerInstance(FunctionContainerTag.class);

    private Argument[] definition;
    private Object     result;
    private boolean    resultSet;
    private List       parameters;
        

    // javadoc inherited (from FunctionContainer)
    public void setDefinition(Argument[] args) throws JspTagException {
        if (definition != null) {
            throw new JspTagException("Duplicate definition");
        }
        definition = args;
        parameters = new Arguments(definition);
    }


    // javadoc inherited (from FunctionContainer)
    public Object getResult() throws JspTagException {
        if (! resultSet) {
            throw new JspTagException("No result was set");
        }
        return result;
    }

    // javadoc inherited (from FunctionContainer)
    public void   setResult(Object result) throws JspTagException {
        if (! resultSet) {
            throw new JspTagException("Result was set already");
        }
        this.result = result;
        resultSet = true;
    }


    // javadoc inherited (from ParamHandler)
    public void addParameter(String key, Object value) throws JspTagException {
        parameters.add(new Parameter(key, value));
    }

    // javadoc inherited (from FunctionContainer)
    public List  getParameters() {
        if (definition == null) {
            List params = new ArrayList();
            Iterator i = parameters.iterator();
            while (i.hasNext()) {
                params.add( ((Parameter) i.next()).value);
            }
            return Collections.unmodifiableList(params);
        } else {
            Arguments params = new Arguments(definition);
            Iterator i = parameters.iterator();
            while (i.hasNext()) {
                Parameter p = (Parameter) i.next();
                params.set(p.key, p.value);
            }
            return Collections.unmodifiableList(params);
        }
    }

    public int doStartTag() throws JspTagException {        
        definition = null;
        resultSet  = false;     
        parameters = new ArrayList();
        return EVAL_BODY_BUFFERED;
    }

    /**
     * Because parameters are stored in order and by key/value, we need this container help class
     */
    private static class Parameter {
        Parameter(String k, Object v) { key = k; value = v; }
        String key;
        Object value;
    }

}
