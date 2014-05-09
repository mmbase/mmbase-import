/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util.functions;

import java.util.*;
import java.util.regex.*;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import org.mmbase.bridge.Node;
import org.mmbase.util.Casting;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * This Function provider creates function objects , which can create a String function based on a
 * pattern. Several kind of patterns are recognized. {PARAM.abc} creates a parameter 'abc' and puts the
 * value of it on that place in the result. {NODE.title}, puts the title field of the node on which
 * the function was applied on that place, and {REQUEST.getContextPath} applies that method to the
 * request parameter (and the result is added). {INITPARAM.xyz} access the servletcontext init parameters xyz.
 *
 * It is also possible to use request parameters and attributes with {REQUESTPARAM.xxx} and {REQUESTATTRIBUTE.yyy}.
 *
 * The functions which are created have silly names like string0, string1 etc, so you want to wrap
 * them in a function with a reasonable name (this is done when specifying this thing in the builder
 * xml).
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 * @since MMBase-1.8
 */
public class PatternNodeFunctionProvider extends FunctionProvider {

    private static final Logger log = Logging.getLoggerInstance(PatternNodeFunctionProvider.class);
    private static final PatternNodeFunctionProvider instance = new PatternNodeFunctionProvider();

    public PatternNodeFunctionProvider() {
    }

    public static PatternNodeFunctionProvider getInstance() {
        return instance;
    }

    public Function getFunction(String name) {
        Function func = (Function) functions.get(name);
        if (func == null) {
            func = new PatternNodeFunction(name);
            functions.put(name, func);
        }
        return func;
    }

    private static final Pattern fieldsPattern            = Pattern.compile("\\{NODE\\.(.+?)\\}");
    private static final Pattern requestPattern           = Pattern.compile("\\{REQUEST\\.(.+?)\\}");
    private static final Pattern requestParamPattern      = Pattern.compile("\\{REQUESTPARAM\\.(.+?)\\}");
    private static final Pattern requestAttributePattern  = Pattern.compile("\\{REQUESTATTRIBUTE\\.(.+?)\\}");
    private static final Pattern paramPattern             = Pattern.compile("\\{PARAM\\.(.+?)\\}");
    private static final Pattern initParamPattern         = Pattern.compile("\\{INITPARAM\\.(.+?)\\}");

    private static int counter = 0;

    protected static class PatternNodeFunction extends NodeFunction {

        String template;
        Map   requestMethods = null;
        PatternNodeFunction(String template) {
            super("string" + (counter++), getParameterDef(template), ReturnType.STRING);
            this.template = template;
            Matcher matcher = requestPattern.matcher(template);
            if (matcher.find()) {
                matcher.reset();
                requestMethods = new HashMap();
                while(matcher.find()) {
                    try {
                        requestMethods.put(matcher.group(1), HttpServletRequest.class.getMethod(matcher.group(1), new Class[] {}));
                    } catch (NoSuchMethodException nsme) {
                        log.error(nsme.getMessage(), nsme);
                    }
                }
            }

        }
        protected static Parameter[] getParameterDef(String template) {
            List params = new ArrayList();
            if (requestPattern.matcher(template).find() ||
                requestParamPattern.matcher(template).find() ||
                requestAttributePattern.matcher(template).find()
                ) {
                params.add(Parameter.REQUEST);
            }
            Matcher args = paramPattern.matcher(template);
            while(args.find()) {
                params.add(new Parameter(args.group(1), String.class, ""));
            }
            return (Parameter[]) params.toArray(new Parameter[] {});
        }

        protected Object getFunctionValue(final Node node, final Parameters parameters) {
            StringBuffer sb = new StringBuffer();
            {
                Matcher fields = fieldsPattern.matcher(template);
                while (fields.find()) {
                    String s = node.getStringValue(fields.group(1));
                    if (s == null) {
                        // I think getStringValue should perhaps never return null, but if it does,
                        // avoid the NPE
                        s = "";
                    }
                    fields.appendReplacement(sb, s);
                }
                fields.appendTail(sb);
            }
            {
                Matcher request = requestPattern.matcher(sb.toString());
                if (request.find()) {
                    request.reset();
                    HttpServletRequest req = (HttpServletRequest) parameters.get(Parameter.REQUEST);
                    sb = new StringBuffer();
                    while(request.find()) {
                        if(request.group(1).equals("getContextPath")) {
                            String r = req == null ? org.mmbase.module.core.MMBaseContext.getHtmlRootUrlPath() : req.getContextPath() + '/';
                            request.appendReplacement(sb, r.substring(0, r.length() - 1));
                            continue;
                        }

                        if (req == null) {
                            log.error("Did't find the request among the parameters");
                            continue;
                        }
                        try {
                            Method m =  (Method) requestMethods.get(request.group(1));
                            if (m == null) {
                                log.error("Didn't find the method " + request.group(1) + " on request object");
                                continue;
                            }
                            request.appendReplacement(sb, "" + m.invoke(req, new Object[] {}));
                        } catch (IllegalAccessException iae) {
                            log.error(iae.getMessage(), iae);
                        } catch (java.lang.reflect.InvocationTargetException ite) {
                            log.error(ite.getMessage(), ite);
                        }
                    }
                    request.appendTail(sb);
                }
            }
            {
                Matcher requestParam = requestParamPattern.matcher(sb.toString());
                if (requestParam.find()) {
                    HttpServletRequest req = (HttpServletRequest) parameters.get(Parameter.REQUEST);
                    if (req == null) {
                        log.error("Did't find the request among the parameters");
                    } else {
                        requestParam.reset();
                        sb = new StringBuffer();
                        while(requestParam.find()) {
                            String paramName = requestParam.group(1);
                            String value = req.getParameter(paramName);
                            if (value == null) value = "";
                            requestParam.appendReplacement(sb, value);
                        }
                        requestParam.appendTail(sb);
                    }
                }
            }
            {
                Matcher requestAttribute = requestAttributePattern.matcher(sb.toString());
                if (requestAttribute.find()) {
                    HttpServletRequest req = (HttpServletRequest) parameters.get(Parameter.REQUEST);
                    if (req == null) {
                        log.error("Did't find the request among the parameters");
                    } else {
                        requestAttribute.reset();
                        sb = new StringBuffer();
                        while(requestAttribute.find()) {
                            String paramName = requestAttribute.group(1);
                            String value = Casting.toString(req.getAttribute(paramName));
                            requestAttribute.appendReplacement(sb, value);
                        }
                        requestAttribute.appendTail(sb);
                    }
                }
            }
            {
                Matcher params = paramPattern.matcher(sb.toString());
                if (params.find()) {
                    params.reset();
                    sb = new StringBuffer();
                    while(params.find()) {
                        params.appendReplacement(sb, (String) parameters.get(params.group(1)));
                    }
                    params.appendTail(sb);
                }
            }
            {

                Matcher initParams = initParamPattern.matcher(sb.toString());
                if (initParams.find()) {
                    initParams.reset();
                    sb = new StringBuffer();
                    while(initParams.find()) {
                        String s = org.mmbase.module.core.MMBaseContext.getServletContext().getInitParameter(initParams.group(1));
                        if (s == null) s = "";
                        initParams.appendReplacement(sb, s);
                    }
                    initParams.appendTail(sb);
                }
            }
            return sb.toString();

        }



    }
}
