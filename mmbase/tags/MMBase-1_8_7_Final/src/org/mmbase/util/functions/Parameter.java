/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.util.functions;

import org.mmbase.bridge.*;
import org.mmbase.core.AbstractDescriptor;
import org.mmbase.datatypes.*;
import org.mmbase.util.*;
import org.mmbase.util.logging.*;
import java.util.*;
import java.io.*;

/**
 * Each (function) argument is specified by a Parameter object.
 * A Parameter contains a name and type (it does <em>not</em> contain a value). An array of this is returned by
 * {@link Function#getParameterDefinition}, and this same array is used to create new empty {@link Parameters}
 * object (by {@link Function#createParameters}), which can contain actual values for each Parameter.
 *
 * @author Daniel Ockeloen (MMFunctionParam)
 * @author Michiel Meeuwissen
 * @since  MMBase-1.7
 * @version $Id$
 * @see Parameters
 */

public class Parameter extends AbstractDescriptor implements java.io.Serializable {
    private static final Logger log = Logging.getLoggerInstance(Parameter.class);

    private static final long serialVersionUID = 1L;
    /**
     * Parameter which might be needed in lots of Parameter definitions. These parameters are
     * 'standard' parameters, which can be filled in by the system. E.g. the mmbase taglib uses
     * these constants, and if it has a cloud ('mm:cloud is used'), then cloud-parameters are filled
     * automaticly.
     */
    public static final Parameter LANGUAGE = new Parameter("language", String.class);
    public static final Parameter LOCALE   = new Parameter("locale",   Locale.class);
    public static final Parameter USER     = new Parameter("user",     org.mmbase.security.UserContext.class);
    public static final Parameter RESPONSE = new Parameter("response", javax.servlet.http.HttpServletResponse.class);
    public static final Parameter REQUEST  = new Parameter("request",  javax.servlet.http.HttpServletRequest.class);
    public static final Parameter CLOUD    = new Parameter("cloud",    org.mmbase.bridge.Cloud.class);

    /**
     * 'system' parameter set for nodefunctions.
     * @since MMBaes-1.8
     */    
    public static final Parameter NODE     = new Parameter("_node",     org.mmbase.bridge.Node.class);
    public final static Parameter CORENODE = new Parameter("_corenode", Object.class); // object because otherwise problems with RMMCI which doesn't have MMObjectNode.



    public static final Parameter FIELD    = new Parameter("field",    String.class);

    /**
     * An empty Parameter array.
     */
    public static final Parameter[] EMPTY  = new Parameter[0];


    // implementation of serializable, I hate java. Cannot make AbstractDescriptor Serializable, so doing it here.... sigh sigh.
    // If you would make AbstractDescriptor Serializable, CoreField will become Serializable and MMObjectBuilder needs to be serializable then (because it is a member of CoreField).
    private void writeObject(ObjectOutputStream out) throws IOException {
        out.writeUTF(key);
        out.writeObject(description);
        out.writeObject(guiName);
        out.writeObject(dataType);
    }
    // implementation of serializable
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        key          = in.readUTF();
        description  = (LocalizedString) in.readObject();
        guiName      = (LocalizedString) in.readObject();
        dataType     = (DataType) in.readObject();
    }

    /**
     * The parameter's data type
     * @since MMBase-1.8
     */
    protected DataType dataType;

    /**
     * Create a Parameter object
     * @param name the name of the parameter
     * @param dataType the datatype of the parameter to copy
     * @since MMBase-1.8
     */
    public Parameter(String name, DataType dataType) {
        this(name, dataType, true);
    }

    /**
     * Create a Parameter object
     * @param name the name of the parameter
     * @param dataType the datatype of the parameter to assign or copy
     * @param copy if <code>true</code>, teh datatype is copied. if not, it is assigned directly,
     *        that is, changing condfiitons on the parameter changes the passed datatype instance.
     * @since MMBase-1.8
     */
    public Parameter(String name, DataType dataType, boolean copy) {
        super(name);
        if (copy) {
            this.dataType = (DataType)dataType.clone(name);
        } else {
            this.dataType = dataType;
        }
    }

    /**
     * Create a Parameter object
     * @param name the name of the parameter
     * @param type the class of the parameter's possible value
     */
    public Parameter(String name, Class type) {
        super(name);
        dataType = DataTypes.createDataType(name, type);
    }

    /**
     * Create a Parameter object
     * @param name the name of the parameter
     * @param type the class of the parameter's possible value
     * @param required whether the parameter requires a value
     */
    public Parameter(String name, Class type, boolean required) {
        this(name,type);
        dataType.setRequired(required);
    }

    /**
     * Create a Parameter object
     * @param name the name of the parameter
     * @param type the class of the parameter's possible value
     * @param defaultValue the value to use if the parameter has no value set
     */
    public Parameter(String name, Class type, Object defaultValue) {
        this(name,type);
        dataType.setDefaultValue(defaultValue);
    }

    /**
     * Copy-constructor, just to copy it with different requiredness
     */
    public Parameter(Parameter p, boolean required) {
        this(p.key, p.getDataType());
        dataType.setRequired(required);
    }

    /**
     * Copy-constructor, just to copy it with different defaultValue (which implies that it is not required now)
     */
    public Parameter(Parameter p, Object defaultValue) {
        this(p.key, p.getDataType());
        dataType.setDefaultValue(defaultValue);
    }

    /**
     * Returns the default value of this parameter (derived from the datatype).
     * @return the default value
     */
    public Object getDefaultValue() {
        return dataType.getDefaultValue();
    }

    /**
     * Sets the default value of this parameter.
     * @param defaultValue the default value
     */
    public void setDefaultValue(Object defaultValue) {
        dataType.setDefaultValue(defaultValue);
    }

    /**
     * Returns the data type of this parameter.
     * @return the datatype
     * @since MMBase-1.8
     */
    public DataType getDataType() {
        return dataType;
    }

    /**
     * Returns the type of values that this parameter accepts.
     * @return the type as a Class
     */
    public Class getTypeAsClass() {
        return dataType.getTypeAsClass();
    }

    /**
     * Returns whether the parameter requires a value.
     * @return <code>true</code> if a value is required
     */
    public boolean isRequired() {
        return dataType.isRequired();
    }

    /**
     * Checks if the passed object is of the correct class (compatible with the type of this Parameter),
     * and throws an IllegalArgumentException if it doesn't.
     * @param value teh value whose type (class) to check
     * @throws IllegalArgumentException if the type is not compatible
     */
    public void checkType(Object value) {
        dataType.checkType(value);
    }


    /**
     * Tries to 'cast' an object for use with this parameter. E.g. if value is a String, but this
     * parameter is of type Integer, then the string can be parsed to Integer.
     * @param value The value to be filled in in this Parameter.
     */
    protected Object autoCast(Object value) {
        return dataType.cast(value, null, null);
    }

    public int hashCode() {
        return getName().hashCode() + 13 * getDataType().hashCode();
    }

    /**
     * Whether parameter equals to other parameter. Only key and type are consided. DefaultValue and
     * required propererties are only 'utilities'.
     * @return true if o is Parameter of which key and type equal to this' key and type.
     */
    public boolean equals(Object o) {
        if (o instanceof Parameter) {
            Parameter a = (Parameter) o;
            return a.getName().equals(getName()) && a.getDataType().equals(getDataType());
        }
        return false;
    }

    public String toString() {
        return getTypeAsClass().getName() + " " + getName();
    }

    /**
     * A Parameter.Wrapper wraps one Parameter around a Parameter[] (then you can put it in a
     * Parameter[]).  Parameters will recognize this. This can be used when you 'extend'
     * functionality, and add more parameters. The Parameter array can contain such a
     * Parameter.Wrapper object containing the original Parameter array.
     */
    public static class Wrapper extends Parameter {
        Parameter[] arguments;

        public Wrapper(Parameter[] arg) {
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
