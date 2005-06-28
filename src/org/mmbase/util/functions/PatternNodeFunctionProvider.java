package org.mmbase.util.functions;

import java.util.*;
import java.util.regex.*;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import org.mmbase.module.core.MMObjectNode;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * This Function provider creates function objects , which can create a String function base on a
 * pattern. Three kind of pattern are recognized. {PARAM.abc} creates a parameter 'abc' and puts the
 * value of it on that place in the result. {NODE.title}, puts the title field of the node on which
 * the function was applied on that place, and {REQUEST.getContextPath} applies that method to the
 * request parameter (and that parameter is added).
 *
 * The functions which are created have silly names like string0, string1 etc, do you want to wrap
 * them in a function with a reasonable name (this is done when specifying this thing in the builder
 * xml).
 *
 * @author Michiel Meeuwissen
 * @version $Id: PatternNodeFunctionProvider.java,v 1.1 2005-06-28 19:09:21 michiel Exp $
 * @since MMBase-1.8
 */
public class PatternNodeFunctionProvider extends FunctionProvider {

    private static final Logger log = Logging.getLoggerInstance(PatternNodeFunctionProvider.class);
    
    public PatternNodeFunctionProvider() {
    }

    public Function getFunction(String name) {
        Function func = (Function) functions.get(name);
        if (func == null) {
            func = new PatternNodeFunction(name);
            functions.put(name, func);
        }
        return func;
    }

    private static final Pattern fieldsPattern   = Pattern.compile("\\{NODE\\.(.+?)\\}");
    private static final Pattern requestPattern  = Pattern.compile("\\{REQUEST\\.(.+?)\\}");
    private static final Pattern paramPattern    = Pattern.compile("\\{PARAM\\.(.+?)\\}");

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
            Matcher matcher = requestPattern.matcher(template);
            if (matcher.find()) {
                params.add(Parameter.REQUEST);
            }
            Matcher args = paramPattern.matcher(template);
            while(args.find()) {
                params.add(new Parameter(args.group(1), String.class, ""));
            }
            return (Parameter[]) params.toArray(new Parameter[] {});
        }

        protected Object getFunctionValue(final MMObjectNode coreNode, final Parameters parameters) {
            StringBuffer sb = new StringBuffer();
            Matcher fields = fieldsPattern.matcher(template);
            while (fields.find()) {
                fields.appendReplacement(sb, coreNode.getStringValue(fields.group(1)));
            }
            fields.appendTail(sb);
            
            Matcher request = requestPattern.matcher(sb.toString());
            if (request.find()) {
                request.reset();
                HttpServletRequest req = (HttpServletRequest) parameters.get(Parameter.REQUEST);
                sb = new StringBuffer();
                while(request.find()) {
                    if(request.group(1).equals("getContextPath")) {
                        String r = org.mmbase.module.core.MMBaseContext.getHtmlRootUrlPath();
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
                            log.error("Didn't finnd the method " + request.group(1) + " on request object");
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
            log.info("Matching " + paramPattern + " on " + sb);
            Matcher params = paramPattern.matcher(sb);
            if (params.find()) {
                params.reset();
                log.info("Matcheds!!");
                sb = new StringBuffer();
                while(params.find()) {
                    log.info("using " + params.group(1));
                    params.appendReplacement(sb, (String) parameters.get(params.group(1)));
                }
                params.appendTail(sb);
            } else {
                log.info("Didn't match!");
            }
            return sb.toString();
            
        }


        
    }
}
