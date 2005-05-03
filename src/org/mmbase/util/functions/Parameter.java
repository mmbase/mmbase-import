/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.util.functions;

import org.mmbase.bridge.DataType;
import java.util.*;

/**
 * Each (function) argument is specified by a Parameter object.
 * A Parameter contains a name and type (it does <em>not</em> contain a value). An array of this is returned by
 * {@link Function#getParameterDefinition}, and this same array is used to create new empty {@link Parameters}
 * object (by {@link Function#createParameters}), which can contain actual values for each Parameter.
 *
 * @author Michiel Meeuwissen
 * @author Daniel Ockeloen (MMFunctionParam)
 * @since  MMBase-1.7
 * @version $Id: Parameter.java,v 1.15 2005-05-03 19:56:34 michiel Exp $
 * @see Parameters
 */

public class Parameter extends org.mmbase.bridge.implementation.AbstractDataType {

    /**
     * Parameters which might be needed in lots of Parameter definition arrays.
     */
    public static final Parameter LANGUAGE = new Parameter("language", String.class);
    public static final Parameter LOCALE   = new Parameter("locale",   Locale.class);
    public static final Parameter USER     = new Parameter("user",     org.mmbase.security.UserContext.class);
    public static final Parameter RESPONSE = new Parameter("response", javax.servlet.http.HttpServletResponse.class);
    public static final Parameter REQUEST  = new Parameter("request",  javax.servlet.http.HttpServletRequest.class);
    public static final Parameter CLOUD    = new Parameter("cloud",    org.mmbase.bridge.Cloud.class);
    public static final Parameter NODE     = new Parameter("node",     org.mmbase.module.core.MMObjectNode.class);

    public static final String STRINGS = "org.mmbase.util.functions.resources.parameters";


    static {
        try {
            LANGUAGE.setBundle(STRINGS);
            LOCALE.setBundle(STRINGS);
            USER.setBundle(STRINGS);
            REQUEST.setBundle(STRINGS);
            RESPONSE.setBundle(STRINGS);
            CLOUD.setBundle(STRINGS);
        } catch (Exception e) {
            // should not happen
        }
    }

    /**
     * An empty Parameter array.
     */
    public static final Parameter[] EMPTY  = new Parameter[0];

    // package for Parameters (direct access avoids function calls)
    Object defaultValue = null;
    boolean required  = false;

    protected Parameter() {}

    /**
     * Create a Parameter object
     * @param name the name of the parameter
     * @param type the class of the parameter's possible value
     */
    public Parameter(String name, Class type) {
        super(name, type);
    }

    /**
     * Create a Parameter object
     * @param name the name of the parameter
     * @param type the class of the parameter's possible value
     * @param required whether the parameter requires a value (default is <code>false</code>)
     */
    public Parameter(String name, Class type, boolean required) {
        super(name, type);
        this.required = required;
    }

    /**
     * Create a Parameter object
     * @param name the name of the parameter
     * @param type the class of the parameter's possible value
     * @param defaultValue the value to use if the parameter has no value set (default is <code>null</code>)
     */
    public Parameter(String name, Class type, Object defaultValue) {
        super(name, type);
        this.defaultValue = defaultValue;
    }


    /**
     * Copy-constructor, just to copy it with different requiredness
     */
    public Parameter(DataType p, boolean required) {
        super(p.getName(), p.getTypeAsClass());
        this.required = required;
        if (! required) { // otherwise it makes no sense
            this.defaultValue = p.getDefaultValue();
        }
    }


    /**
     * Copy-constructor, just to copy it with different defaultValue (which implies that it is not required now)
     */
    public Parameter(DataType p, Object defaultValue) {
        super(p.getName(), p.getTypeAsClass());
        this.defaultValue = defaultValue;
        // not need to copy 'required', it should be 'false'.
    }

    /**
     * Returns the default value of this parameter.
     * @return the default value
     */
    public Object getDefaultValue() {
        return defaultValue;
    }

    /**
     * Sets the default value of this parameter.
     * @param def the default value
     */
    public void setDefaultValue(Object def) {
        defaultValue = def;
    }

    /**
     * Returns whether the parameter requires a value.
     * @return <code>true</code> if a value is required
     */
    public boolean isRequired() {
        return required;
    }

    /**
     * A Parameter.Wrapper wraps one Parameter around a Parameter[] (then you can put it in a
     * Parameter[]).  Parameters will recognize this. This can be used when you 'extend'
     * functionality, and add more parameters. The Parameter array can contain such a
     * Parameter.Wrapper object containing the original Parameter array.
     */
    public static class Wrapper extends Parameter {
        DataType[] arguments;

        public Wrapper(DataType[] arg) {
            super("[ARRAYWRAPPER]",Parameter[].class);
            arguments = arg;
        }

        public String toString() {
            return "WRAPPED" + Arrays.asList(arguments).toString();
        }
    }

}
