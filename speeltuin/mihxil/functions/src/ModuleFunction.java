/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util.functions;


import org.mmbase.util.logging.*;
import org.mmbase.bridge.*;

/**
 * Describing a function on a Module (getInfo).
 *
 * Using function sets, perhaps Modules are nearly depreacted?
 *
 * @author Michiel Meeuwissen
 * @version $Id: ModuleFunction.java,v 1.1 2003-11-21 22:01:50 michiel Exp $
 * @since MMBase-1.7
 */
public class ModuleFunction extends Function {

    private static final Logger log = Logging.getLoggerInstance(ModuleFunction.class);

    private Module module;
    public ModuleFunction(String name, Parameter[] def, ReturnType returnType, Module module) {
        super(name, def, returnType);
        this.module = module;
    }

    /**
     * NodeManager actually has two function like methods now (accepting 'command')
     */

    public Object getFunctionValue(Parameters arguments) {
        if (returnType == ReturnType.VOID) {
            module.process(name, arguments.get(0), arguments.toMap());
            return ReturnType.VOID_VALUE;
        } else if (String.class.isAssignableFrom(returnType.getType())) {
            return module.getInfo(name);
        } else if (NodeList.class.isAssignableFrom(returnType.getType())) {
            return module.getList(name, arguments.toMap());
        } else {
            throw new IllegalStateException("Don't know what to do, function has return type which cannot be handled by modules");
        }
    }

}
