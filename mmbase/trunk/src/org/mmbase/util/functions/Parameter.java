/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.util.functions;

import org.mmbase.bridge.DataType;
import org.mmbase.bridge.implementation.datatypes.*;
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
 * @version $Id: Parameter.java,v 1.17 2005-06-28 14:01:42 pierre Exp $
 * @see Parameters
 */

public class Parameter extends org.mmbase.bridge.implementation.AbstractDataType {

    /**
     * Parameters which might be needed in lots of Parameter definition arrays.
     */
    public static final Parameter LANGUAGE = (Parameter) DataType.LANGUAGE;
    public static final Parameter LOCALE   = (Parameter) DataType.LOCALE;
    public static final Parameter USER     = (Parameter) DataType.USER;
    public static final Parameter RESPONSE = (Parameter) DataType.RESPONSE;
    public static final Parameter REQUEST  = (Parameter) DataType.REQUEST;
    public static final Parameter CLOUD    = (Parameter) DataType.CLOUD;
    public static final Parameter NODE     = (Parameter) DataType.NODE;

    /**
     * An empty Parameter array.
     */
    public static final Parameter[] EMPTY  = new Parameter[0];

    /**
     * Create a Parameter object
     * @param name the name of the parameter
     * @param type the class of the parameter's possible value
     */
    public Parameter(String name, int type) {
        super(name, type);
    }

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
     * @param dataType the parent datatype whose properties to inherit
     */
    public Parameter(String name, DataType dataType) {
        super(name, dataType);
    }

    /**
     * Create a Parameter object
     * @param name the name of the parameter
     * @param type the class of the parameter's possible value
     * @param required whether the parameter requires a value
     */
    public Parameter(String name, Class type, boolean required) {
        super(name, type);
        setRequired(required);
    }

    /**
     * Create a Parameter object
     * @param name the name of the parameter
     * @param type the class of the parameter's possible value
     * @param defaultValue the value to use if the parameter has no value set
     */
    public Parameter(String name, Class type, Object defaultValue) {
        super(name, type);
        setDefaultValue(defaultValue);
    }

    /**
     * Copy-constructor, just to copy it with different requiredness
     * @param dataType the parent datatype whose properties to inherit
     * @param required whether the parameter requires a value
     */
    public Parameter(DataType dataType, boolean required) {
        super(dataType.getName(), dataType);
        setRequired(required);
    }

    /**
     * Copy-constructor, just to copy it with different defaultValue (which implies that it is not required now)
     * @param dataType the parent datatype whose properties to inherit
     * @param defaultValue the value to use if the parameter has no value set
     */
    public Parameter(DataType dataType, Object defaultValue) {
        super(dataType.getName(), dataType);
        setDefaultValue(defaultValue);
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
            super("[ARRAYWRAPPER]", Parameter[].class);
            arguments = arg;
        }

        // this toString makes the wrapping invisible in the toString of a wrapping Parameter[]
        public String toString() {
            StringBuffer buf = new StringBuffer();
            for (int i = 0 ; i < arguments.length; i++) {
                if (i > 0) buf.append(", ");
                buf.append(arguments[i].toString());

            }
            return buf.toString();

        }
    }

}
