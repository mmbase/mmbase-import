/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util.functions;


import java.lang.reflect.*;
import java.lang.annotation.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;


/**
 * A function based on an abritrary method. Use the annotation {@link Name} to attribute the
 * parameter names. A method function can e.g. be defined like so in the builder xml:
<pre><![CDATA[
  <function key="canCloseLesson" name="canCloseLesson">
      <class>nl.didactor.component.assessment.LessonChecker</class>
</function>]]>
 </pre>
And be implemented like so:
<pre>
  public static boolean canCloseLesson(&#064;Required &#064;Name("node") Node user,
                                       &#064;Required &#064;Name("lesson") Node lesson) {
   ...
 }
</pre>

 * @author Michiel Meeuwissen
 * @version $Id$
 * @see org.mmbase.module.core.MMObjectBuilder#executeFunction
 * @see org.mmbase.bridge.Node#getFunctionValue
 * @see org.mmbase.util.functions.BeanFunction
 * @since MMBase-1.7
 */
public class MethodFunction extends AbstractFunction<Object> {
    private static final Logger log = Logging.getLoggerInstance(MethodFunction.class);


    public static Function<Object> getFunction(Method method, String name) {
        return new MethodFunction(method, name); // could be cached...
    }

    /**
     * @since MMBase-1.9
     */
    public static Function<Object> getFunction(Method method, String name, Object instance) {
        return new MethodFunction(method, name, instance); // could be cached...
    }

    /**
     * Returns the MethodFunction representing the method 'name' in class 'clazz'. If there are more
     * methods whith that name, the one with the largest number of by name annotated parameters is taken.
     * @since MMBase-1.9
     */
    public static Function<Object> getFunction(Class<?> clazz, String name) {
        // Finding method to use
        Method method = getMethod(clazz, name);
        return getFunction(method, method.getName());
    }
    public static Method getMethod(Class<?> clazz, String name) {
        // Finding method to use
        Method method = null;
        float score = -1.0f;
        for (Method m : clazz.getMethods()) {
            String methodName = m.getName();
            if (methodName.equals(name)) {
                Annotation[][] annots = m.getParameterAnnotations();
                int found = 0;
                int total = 1; // avoids division by zero and ensures that methods with more parameters are better.
                for (Annotation[] anot : annots) {
                    for (Annotation a : anot) {
                        if (a.annotationType().equals(Name.class)) {
                            found ++;
                        }
                    }
                    total++;
                }
                if ((float) found / total > score) {
                    method = m;
                    score = (float) found / total;
                }
            }
        }
        return method;
    }

    private final Method method;
    private final Object instance;
    /**
     * @since MMBase-1.9
     */
    public MethodFunction(Method method) {
        this(method, method.getName(), null);
    }
    public MethodFunction(Method method, String name) {
        this(method, name, null);
    }
    /**
     * @since MMBase-1.9
     */
    public MethodFunction(Method method, Object instance) {
        this(method, method.getName(), instance);
    }
    /**
     * @since MMBase-1.9
     */
    public MethodFunction(Method method, String name, Object instance) {
        super(name, null, null);
        this.method = method;
        this.instance = instance;
        if (instance == null) {
            if (! Modifier.isStatic(method.getModifiers())) {
                throw new IllegalArgumentException("The method " + method + " is not static"); // otherwise NPE in getFunctionValue
            }
        } else {
            if (! method.getDeclaringClass().isInstance(instance)) {
                throw new IllegalArgumentException("The object " + instance + " is not an instance of the class of  " + method);
            }
        }

        Annotation[][] annots = method.getParameterAnnotations();
        Class[] parameters = method.getParameterTypes();
        Parameter<?>[] def = new Parameter[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            String paramName = null;
            boolean required = false;
            for (Annotation annot : annots[i]) {
                // no other way to find the name of the parameter than with annotations
                if (annot.annotationType().equals(Name.class)) {
                    paramName = ((Name) annot).value();
                }
                if (annot.annotationType().equals(Required.class)) {
                    required = true;
                }
            }
            if (paramName == null) paramName = "parameter" + (i + 1);

            // make it possible to default also NodeFunction as MethodFunctions.
            if (paramName.equals("node") && org.mmbase.bridge.Node.class.isAssignableFrom(parameters[i])) {
                def[i] = Parameter.NODE;
            } else {
                def[i] = new Parameter<Object>(paramName, parameters[i], required);
            }
        }

        setParameterDefinition(def);

        ReturnType returnType = ReturnType.getReturnType(method.getReturnType());
        setReturnType(returnType);

    }

    public Object getFunctionValue(Parameters parameters) {
        try {
            return method.invoke(instance, parameters.subList(0, getParameterDefinition().length).toArray());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
