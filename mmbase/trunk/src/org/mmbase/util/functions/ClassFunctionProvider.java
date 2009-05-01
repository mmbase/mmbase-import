/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package org.mmbase.util.functions;

import java.lang.reflect.*;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * Maintains all methods of a certain class as function objects (as long as they have unique names).
 *
 * @since MMBase-1.9
 * @author Michiel Meeuwissen
 * @version $Id$
 */
public class ClassFunctionProvider extends FunctionProvider {
    private static final Logger log = Logging.getLoggerInstance(ClassFunctionProvider.class);

    public ClassFunctionProvider(Class<?> clazz, Object instance) {
        for (Method m : clazz.getMethods()) {
            if (! functions.containsKey(m.getName())) {
                try {
                    addFunction(MethodFunction.getFunction(MethodFunction.getMethod(clazz, m.getName()), m.getName(), instance));
                } catch (Exception e) {
                    log.error("For " + m.getName() + " of " + clazz + " : " + e.getMessage());
                }
            }
        }
    }

}
