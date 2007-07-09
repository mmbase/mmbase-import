/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util.functions;

import org.mmbase.cache.Cache;

import java.lang.reflect.*;
import java.util.*;
import org.mmbase.util.logging.*;

/**
 *
 * @author Michiel Meeuwissen
 * @version $Id: BeanFunctionProvider.java,v 1.1 2006-09-12 18:38:51 michiel Exp $
 * @see org.mmbase.util.functions.BeanFunction
 * @since MMBase-1.9
 */
public class BeanFunctionProvider extends FunctionProvider{
    private static final Logger log = Logging.getLoggerInstance(BeanFunctionProvider.class);


    public BeanFunctionProvider(Class clazz) {
        for (Method m : clazz.getMethods()) {
            if (m.getParameterTypes().length == 0) {
                try {
                    addFunction(BeanFunction.getFunction(clazz, m.getName()));
                } catch (Exception e) {
                    log.error("For " + m.getName() + " of " + clazz + " : " + e.getMessage());
                }
            }
        }
    }

}
