/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util.functions;

/**
 * @javadoc
 *
 * @since MMBase-1.8
 * @author Pierre van Rooden
 * @version $Id: ProviderFunction.java,v 1.2 2004-11-29 12:45:07 pierre Exp $
 */
public class ProviderFunction extends AbstractFunction {

    /**
     * The function container on which this function must be executed.
     */
    private FunctionProvider provider;

    /**
     * @javadoc
     */
    protected ProviderFunction(String name, Parameter[] def, ReturnType returnType, FunctionProvider provider) {
        super(name, def, returnType);
        this.provider = provider;
    }

    /**
     * @javadoc
     */
    public FunctionProvider getFunctionProvider() {
        return provider;
    }

    /**
     * {@inheritDoc}
     * Simply wraps {@link ContainerFunction#getFunctionValue(String, List)}
     */
    public Object getFunctionValue(Parameters arguments) {
        return provider.executeFunction(name, arguments);
    }

}
