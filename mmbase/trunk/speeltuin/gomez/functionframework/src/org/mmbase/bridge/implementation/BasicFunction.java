/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.implementation;

import java.util.List;
import org.mmbase.bridge.Cloud;
import org.mmbase.util.functions.*;

/**
 * @javadoc
 * @since MMBase-1.7
 * @author Pierre van Rooden
 * @version $Id: BasicFunction.java,v 1.1 2004-11-24 13:23:03 pierre Exp $
 */
public class BasicFunction extends WrappedFunction {

    protected Cloud cloud;

    /**
     * Constructor for Basic Function
     * @param cloud The user's cloud
     * @param function The function to wrap
     */
    public BasicFunction(Cloud cloud, Function function) {
         super(function);
         this.cloud = cloud;
    }

    public Object getFunctionValue(Parameters parameters) {
        if (cloud != null) {
            parameters.setIfDefined("cloud", cloud);
        }
        return new BasicFunctionValue(cloud, super.getFunctionValue(parameters));
    }
}
