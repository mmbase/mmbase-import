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
 *
 * @author Michiel Meeuwissen
 * @version $Id: MMFunction.java
 */
public class FunctionFactory {

    private static final Logger log = Logging.getLoggerInstance(FunctionFactory.class);

    public static Function getFunction(String set, String name) {
        /// get instasnce from XML
        throw new UnsupportedOperationException("");
    }


    public static Function getFunction(Node node, String name) {
        // defined in Builder XML?        
        throw new UnsupportedOperationException("");
        
    }

    public static Function getFunction(NodeManager nodeManager, String name) {
        // defined in Builder XML?
        throw new UnsupportedOperationException("");
    }

    public static Function getFunction(Module module, String name) {
        // defined in Module XML?
        throw new UnsupportedOperationException("");
    }

}
