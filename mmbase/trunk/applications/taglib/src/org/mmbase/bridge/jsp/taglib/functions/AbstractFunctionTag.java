/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib.functions;


import java.lang.reflect.Method;
import java.util.Iterator;

import javax.servlet.jsp.JspTagException;

import org.mmbase.bridge.jsp.taglib.*;
import org.mmbase.bridge.jsp.taglib.containers.*;
import org.mmbase.bridge.jsp.taglib.util.*;
import org.mmbase.util.functions.*;
import org.mmbase.util.logging.*;



/**
 * The function tags can be used as a child of a 'NodeProvider' tag (but not on clusternodes?), but
 * it can also be used stand alone, when using the attributes to specify on which object the
 * function must be called (besides nodes, it can be called on node-manager, modules, sets).
 *
 * This is the absctract implementation, providing only the result of the function. The several
 * extensions cast to the right type, and handle it on a specific way.
 *
 *
 * @author  Michiel Meeuwissen
 * @since   MMBase-1.7
 * @version $Id: AbstractFunctionTag.java,v 1.5 2004-02-11 20:40:13 keesj Exp $
 */
abstract public class AbstractFunctionTag extends NodeReferrerTag { 

    private static final Logger log = Logging.getLoggerInstance(AbstractFunctionTag.class);

    protected Attribute container   = Attribute.NULL;
    protected Attribute name        = Attribute.NULL;
    protected Attribute parametersAttr  = Attribute.NULL;

    protected Attribute module      = Attribute.NULL;
    protected Attribute nodeManager = Attribute.NULL;

    protected Attribute functionSet = Attribute.NULL; 

    protected Attribute referids    = Attribute.NULL; 


    public void setName(String n) throws JspTagException {
        name = getAttribute(n);
    }

    public void setContainer(String c) throws JspTagException {
        container = getAttribute(c); 
    }

    public void setParameters(String p)  throws JspTagException {
        parametersAttr = getAttribute(p);
    }

    public void setModule(String m) throws JspTagException {
        module = getAttribute(m);
    }

    public void setNodemanager(String n) throws JspTagException {
        nodeManager = getAttribute(n);
    }


    public void setSet(String s) throws JspTagException {
        functionSet = getAttribute(s);
    }

    public void setReferids(String r) throws JspTagException {
        referids = getAttribute(r);
    }

    /**
     * Gets function object, and checks consistency of attributes.
     */

    protected final Function getFunction(String functionName) throws JspTagException {
        Function function;
        // now determin on what the object the function must be done.        
        if (nodeManager != Attribute.NULL) {
            if (module != Attribute.NULL || functionSet != Attribute.NULL || parentNodeId != Attribute.NULL) {
                throw new JspTagException("You can only use one of 'nodemanager', 'module', 'set'  or 'node' on a function tag");
            }
            function = FunctionFactory.getFunction(getCloud().getNodeManager(nodeManager.getString(this)), functionName);
        } else if (module != Attribute.NULL) {
            if (nodeManager != Attribute.NULL || functionSet != Attribute.NULL || parentNodeId != Attribute.NULL) {
                throw new JspTagException("You can only use one of 'nodemanager', 'module', 'set'  or 'node' on a function tag");
            }
            function = FunctionFactory.getFunction(getCloudContext().getModule(module.getString(this)), functionName);
        } else if (functionSet != Attribute.NULL) {
            if (nodeManager != Attribute.NULL || module != Attribute.NULL || parentNodeId != Attribute.NULL) {
                throw new JspTagException("You can only use one of 'nodemanager', 'module', 'set'  or 'node' on a function tag");
            }
            String set = functionSet.getString(this);
            if (set.startsWith("THISPAGE")) {
                Class claz = pageContext.getPage().getClass();

                if (set.equals("THISPAGE")) {
                    Method method = MethodFunction.getFirstMethod(claz, functionName);                    
                    function = FunctionFactory.getFunction(method, functionName);
                } else {
                    throw new UnsupportedOperationException("Local beans not yet supported");
                }
            } else {
                function = FunctionFactory.getFunction(functionSet.getString(this), functionName);
            }
        } else { // working as Node-referrer unless explicitely specified that it should not (a container must be present!)
 
            if (container != Attribute.NULL || "".equals(parentNodeId.getValue(this)) || functionName == null) { // explicitit container
                FunctionContainerTag functionContainer = (FunctionContainerTag) findParentTag(FunctionContainer.class, (String) container.getValue(this), false);
                function = functionContainer.getFunction(functionName);
            } else { // it is possible that a 'closer' node provider is meant
                FunctionContainerOrNodeProvider functionOrNode = 
                    (FunctionContainerOrNodeProvider) findParentTag(FunctionContainerOrNodeProvider.class, null, false);
                if (functionOrNode != null) {
                    if (functionOrNode instanceof NodeProvider) { // wow, indeed, that we are going to use
                        function = FunctionFactory.getFunction(getNode(), functionName);
                    } else { // just use the functioncontainer
                        function = ((FunctionContainerTag) functionOrNode).getFunction(functionName);
                    }
                } else {
                    return null;
                }
            }
        }
        return function;
    }



    protected final void fillStandardParameters(Parameters p) throws JspTagException {
        log.debug("Filling standard parameters");
        if (p.hasParameter(Parameter.RESPONSE)) {
            p.set(Parameter.RESPONSE, pageContext.getResponse());
        }
        if (p.hasParameter(Parameter.REQUEST)) {
            p.set(Parameter.REQUEST, pageContext.getRequest());
        }
        if (p.hasParameter(Parameter.LANGUAGE)) {
            LocaleTag localeTag = (LocaleTag)findParentTag(LocaleTag.class, null, false);
            if (localeTag != null) {
                p.set(Parameter.LANGUAGE, localeTag.getLocale().getLanguage());
            }
        }
        if (p.hasParameter(Parameter.CLOUD)) {
            p.set(Parameter.CLOUD, getCloud());
        }
        if (p.hasParameter(Parameter.USER)) {
            p.set(Parameter.USER, getCloud().getUser());
        }
    }
    protected final Object getFunctionValue() throws JspTagException {
        log.debug("Getting function value");
        FunctionContainerTag functionContainer = (FunctionContainerTag) findParentTag(FunctionContainer.class, (String) container.getValue(this), false);

        String functionName = name.getString(this);

        Function function;
        if ("".equals(functionName)) {  // no name given, certainly must use container.
            function = functionContainer.getFunction(functionContainer.getName());
        } else {
            // name given, try self:
            function = getFunction(functionName);
        }
        
        if (function == null) {
            throw new JspTagException("Could not determin the name of the function to be executed");
        }
        Parameters params;
        if (function.getParameterDefinition() == null) {
            log.warn("Could not find parameter definition for function '" + functionName + "', trying without definition.");
            params = new AutodefiningParameters();
        } else {
            params = function.getNewParameters();
        }
        params.setAutoCasting(true);

        if (functionContainer != null) {
            Iterator i = functionContainer.getParameters().iterator();
            while (i.hasNext()) {
                FunctionContainer.Entry entry = (FunctionContainer.Entry) i.next();
                params.set(entry.getKey(), entry.getValue());
            }        
        }
        if (referids != Attribute.NULL) {
            params.setAll(Referids.getReferids(referids, this));
        }

        fillStandardParameters(params);

        if (log.isDebugEnabled()) {
            log.debug("using parameters " + params);
        }

        params.checkRequiredParameters();

        return function.getFunctionValue(params);
    }

}
