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

    public Function<String> getFunction(String name) {
        Function func = functions.get(name);
        if (func == null) {
            func = new PatternNodeFunction(name);
            functions.put(name, func);
        }
        return func;
    }

    private static final Pattern requestPattern           = Pattern.compile("\\{REQUEST\\.(.+?)\\}");
    private static final Pattern requestParamPattern      = Pattern.compile("\\{REQUESTPARAM\\.(.+?)\\}");
    private static final Pattern requestAttributePattern  = Pattern.compile("\\{REQUESTATTRIBUTE\\.(.+?)\\}");

    public static Map<String, Method> getRequestMethods(String template) {
        // could use cache here to avoid the reflection overhead
        Matcher matcher = requestPattern.matcher(template);
        if (matcher.find()) {
            matcher.reset();
            Map<String, Method> requestMethods = new HashMap<String, Method>();
            while(matcher.find()) {
                try {
                    requestMethods.put(matcher.group(1), HttpServletRequest.class.getMethod(matcher.group(1)));
                } catch (NoSuchMethodException nsme) {
                    log.error(nsme.getMessage(), nsme);
                }
            }
            return requestMethods;
        } else {
            return null;
        }
    }
    /**
     * @param sb StringBuffer (not a StringBuilder, because there is no appendTail(StringBuilder)...)
     * @since MMBase-1.9
     */
    public static void handleRequest(StringBuffer sb, Parameters parameters, Map<String, Method> requestMethods) {
        {
            Matcher request = requestPattern.matcher(sb.toString());
            if (request.find()) {
                request.reset();
                HttpServletRequest req = parameters.get(Parameter.REQUEST);
                sb.setLength(0);
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
                        Method m = requestMethods.get(request.group(1));
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
                HttpServletRequest req = parameters.get(Parameter.REQUEST);
                if (req == null) {
                    log.error("Did't find the request among the parameters");
                } else {
                    requestParam.reset();
                    sb.setLength(0);
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
                HttpServletRequest req = parameters.get(Parameter.REQUEST);
                if (req == null) {
                    log.error("Did't find the request among the parameters");
                } else {
                    requestAttribute.reset();
                    sb.setLength(0);
                    while(requestAttribute.find()) {
                        String paramName = requestAttribute.group(1);
                        String value = Casting.toString(req.getAttribute(paramName));
                        requestAttribute.appendReplacement(sb, value);
                    }
                    requestAttribute.appendTail(sb);
                }
            }
        }
    }

    private static final Pattern fieldsPattern            = Pattern.compile("\\{NODE\\.(.+?)\\}");
    private static final Pattern paramPattern             = Pattern.compile("\\{PARAM\\.(.+?)\\}");
    private static final Pattern initParamPattern         = Pattern.compile("\\{INITPARAM\\.(.+?)\\}");

    private static int counter = 0;

    protected static class PatternNodeFunction extends NodeFunction<String> {

        final String template;
        final Map<String, Method>   requestMethods;
        PatternNodeFunction(String template) {
            super("string" + (counter++), getParameterDef(template), ReturnType.STRING);
            this.template = template;
            this.requestMethods = getRequestMethods(template);

        }
        protected static Parameter[] getParameterDef(String template) {
            List<Parameter> params = new ArrayList<Parameter>();
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
            return params.toArray(new Parameter[] {});
        }

        protected String getFunctionValue(final Node node, final Parameters parameters) {
            StringBuffer sb = new StringBuffer(); // those guys from Sun forgot to supply Matcher#appendTail(StringBuilder)
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
            handleRequest(sb, parameters, requestMethods);
            {
                Matcher params = paramPattern.matcher(sb.toString());
                if (params.find()) {
                    params.reset();
                    sb.setLength(0);
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
                    sb.setLength(0);
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
