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

import org.mmbase.bridge.NotFoundException;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.jsp.taglib.*;
import org.mmbase.bridge.jsp.taglib.containers.*;
import org.mmbase.bridge.jsp.taglib.util.*;
import java.util.Map;
import org.mmbase.util.functions.*;
import org.mmbase.util.functions.Functions;
import org.mmbase.util.logging.*;

/**
 * The function tags can be used as a child of a 'NodeProvider' tag (though posisbly
 * not on clusternodes).
 * It can also be used stand alone, when using the attributes to specify on which object the
 * function must be called (besides nodes, it can be called on node-manager, modules, sets).
 *
 * This is the absctract implementation, providing only the result of the function. The several
 * extensions cast to the right type, and handle it on a specific way.
 *
 *
 * @author  Michiel Meeuwissen
 * @since   MMBase-1.7
 * @version $Id: AbstractFunctionTag.java,v 1.27.2.3 2008-01-24 12:06:54 michiel Exp $
 */
abstract public class AbstractFunctionTag extends NodeReferrerTag {

    private static final Logger log = Logging.getLoggerInstance(AbstractFunctionTag.class);

    public static final String THISPAGE = "THISPAGE";

    protected Attribute container   = Attribute.NULL;
    protected Attribute name        = Attribute.NULL;
    protected Attribute parametersAttr  = Attribute.NULL;

    protected Attribute module      = Attribute.NULL;
    protected Attribute nodeManager = Attribute.NULL;

    protected Attribute functionSet = Attribute.NULL;

    protected Attribute functionClass = Attribute.NULL;

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

    public void setClassname(String c) throws JspTagException {
        functionClass = getAttribute(c);
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
            if (module != Attribute.NULL || functionSet != Attribute.NULL || functionClass != Attribute.NULL || parentNodeId != Attribute.NULL) {
                throw new TaglibException("You can only use one of 'nodemanager', 'module', 'set', 'class' or 'node' on a function tag");
            }
            return  getCloudVar().getNodeManager(nodeManager.getString(this)).getFunction(functionName);
        } else if (module != Attribute.NULL) {
            if (nodeManager != Attribute.NULL || functionSet != Attribute.NULL || functionClass != Attribute.NULL || parentNodeId != Attribute.NULL) {
                throw new TaglibException("You can only use one of 'nodemanager', 'module', 'set', 'class' or 'node' on a function tag");
            }
            return FunctionFactory.getFunction(getCloudContext().getModule(module.getString(this)), functionName); // or:  module.getFunction(functionName);
        } else if (functionSet != Attribute.NULL) {
            if (nodeManager != Attribute.NULL || module != Attribute.NULL || parentNodeId != Attribute.NULL || functionClass != Attribute.NULL) {
                throw new TaglibException("You can only use one of 'nodemanager', 'module', 'set', 'class' or 'node' on a function tag");
            }
            String set = functionSet.getString(this);
            if (set.equals(THISPAGE)) {
                Class jspClass = pageContext.getPage().getClass();
                Method method = Functions.getMethodFromClass(jspClass, functionName);
                return FunctionFactory.getFunction(method, functionName); // or: new MethodFunction(method, functionName);
            } else {
                CloudProvider cp = findCloudProvider(false);
                if (cp != null) {
                    return FunctionFactory.getFunction(cp.getCloudVar(), set, functionName);
                } else {
                    return FunctionFactory.getFunction(set, functionName);
                }
            }
        } else if (functionClass != Attribute.NULL) {
            if (nodeManager != Attribute.NULL || module != Attribute.NULL || parentNodeId != Attribute.NULL || functionSet != Attribute.NULL) {
                throw new TaglibException("You can only use one of 'nodemanager', 'module', 'set', 'class' or 'node' on a function tag");
            }
            String className = functionClass.getString(this);
            try {
                Class clazz;
                if (className.indexOf(".") == -1) {
                    Class jspClass = pageContext.getPage().getClass();
                    clazz   = BeanFunction.getClass(jspClass, className);
                } else {
                    clazz = Class.forName(className);
                }
                return FunctionFactory.getFunction(clazz, functionName); // BeanFunction.getFunction(clazz,functionName)
            } catch (Exception e) {
                // possible execptions thrown when instantiating bean functions:
                // IllegalArgumentException, IllegalAccessException, InstantiationException, InvocationTargetException
                throw new TaglibException(e);
            }

        } else { // working as Node-referrer unless explicitely specified that it should not (a container must be present!)

            log.debug("Node-referrer?");
            if (container != Attribute.NULL || "".equals(parentNodeId.getValue(this)) || functionName == null) { // explicitit container
                log.debug("explicitely not");
                FunctionContainerTag functionContainer = (FunctionContainerTag) findParentTag(FunctionContainer.class, (String) container.getValue(this), false);
                if (functionContainer != null) {
                    function = functionContainer.getFunction(functionName);
                    return function;
                } else {
                    // ignore.
                }
            }
            // it is possible that a 'closer' node provider is meant
            FunctionContainerOrNodeProvider functionOrNode;
            log.debug("Checking for 'closer' node provider");
            if (parentNodeId != Attribute.NULL) {
                log.debug("explicitely specified node");
                functionOrNode = findNodeProvider();
            } else {
                functionOrNode = (FunctionContainerOrNodeProvider) findParentTag(FunctionContainerOrNodeProvider.class, null, false);
            }
            if (log.isDebugEnabled()) {
                log.debug("Found functionOrNode " + functionOrNode);
            }
            if (functionOrNode != null) {
                if (functionOrNode instanceof NodeProvider) { // wow, indeed, that we are going to use
                    log.debug("using node-function!");
                    return ((NodeProvider) functionOrNode).getNodeVar().getFunction(functionName);
                } else { // just use the functioncontainer
                    return ((FunctionContainerTag) functionOrNode).getFunction(functionName);
                }
            } else {
                Node node = getNodeFromPageContext();
                if (node == null) {
                    return null;
                } else {
                    return node.getFunction(functionName);
                }
            }
        }

    }

    protected final Function getFunction() throws JspTagException {
        String functionName = name.getString(this);
        FunctionContainerTag functionContainer = (FunctionContainerTag) findParentTag(FunctionContainer.class, (String) container.getValue(this), false);
        if(log.isDebugEnabled()) {
            log.debug("Getting function value. Container " + functionContainer);
        }

        Function function;
        if ("".equals(functionName)) {  // no name given, certainly must use container.
            if (functionContainer == null) throw new JspTagException("No function name given, and no function container tag found either");
            function = functionContainer.getFunction(functionContainer.getName());
            if (function == null) {
                throw new JspTagException("Could not determine the name of the function to be executed");
            }
        } else {
            log.debug("Trying self for function " + functionName);
            // name given, try self:
            function = getFunction(functionName);
            if (function == null) {
                throw new NotFoundException("Could not find function with the name '" + functionName + "'");
            }
        }
        return function;

    }

    protected final Object getFunctionValue() throws JspTagException {
        return getFunctionValue(true);
    }


    protected final Object getFunctionValue(boolean register) throws JspTagException {
        String functionName = name.getString(this);
        Object value;
        if (getReferid()  != null) {
            if (! "".equals(functionName)) {
                throw new TaglibException("Cannot specify both 'referid' and 'name' attributes on a function tag");
            }
            value = getObject(getReferid());
        } else {
            Function function = null;
            try {
                function = getFunction();
            } catch (RuntimeException e) {
                if ("log".equals(pageContext.getServletContext().getInitParameter("mmbase.taglib.function.nonExistance"))) {
                    log.error("Could not find function with the name '" + functionName + "'");
                    return null;
                }
                throw e;
             }

            if (log.isDebugEnabled()) {
                log.debug("Function to use " + function);
            }
            Parameters params;
            try {
                params = function.createParameters();
            } catch (IllegalStateException ise) {
                log.warn("Undefined parameters for function '" + functionName + "', trying without definition.");
                params = new AutodefiningParameters();
            }
            params.setAutoCasting(true);

            FunctionContainerTag functionContainer = (FunctionContainerTag) findParentTag(FunctionContainer.class, (String) container.getValue(this), false);
            try {
                if (functionContainer != null) {
                    Iterator i = functionContainer.getParameters().iterator();
                    while (i.hasNext()) {
                        Map.Entry entry = (Map.Entry) i.next();
                        params.set((String) entry.getKey(), entry.getValue());
                    }
                }
                if (referids != Attribute.NULL) {
                    params.setAll(Referids.getReferids(referids, this));
                }

                fillStandardParameters(params);

                if (log.isDebugEnabled()) {
                    log.debug("using parameters " + params + " on " + function.getClass() + " " + function);
                }

                params.checkRequiredParameters();
            } catch (Throwable e) {
                throw new IllegalArgumentException("function " + functionName + " " + e.getMessage());
            }

            value =  function.getFunctionValue(params);

        }
        if (register && getId() != null) {
            getContextProvider().getContextContainer().register(getId(), value);
        }
        return value;
    }

}
