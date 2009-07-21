/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.util.functions;

import org.mmbase.core.AbstractDescriptor;
import org.mmbase.datatypes.*;
import org.mmbase.datatypes.util.xml.*;
import org.mmbase.util.*;
import org.mmbase.util.logging.*;
import java.util.*;
import java.io.*;
import org.w3c.dom.*;

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

public class Parameter<C> extends AbstractDescriptor implements java.io.Serializable {
    private static final Logger log = Logging.getLoggerInstance(Parameter.class);

    private static final long serialVersionUID = 1L;
    /**
     * Parameter which might be needed in lots of Parameter definitions. These parameters are
     * 'standard' parameters, which can be filled in by the system. E.g. the mmbase taglib uses
     * these constants, and if it has a cloud ('mm:cloud is used'), then cloud-parameters are filled
     * automaticly.
     */
    public static final Parameter<String>                                  LANGUAGE = new Parameter<String>("language", String.class);
    public static final Parameter<Locale>                                  LOCALE   = new Parameter<Locale>("locale",   Locale.class);
    public static final Parameter<org.mmbase.security.UserContext>         USER     = new Parameter<org.mmbase.security.UserContext>("user", org.mmbase.security.UserContext.class);
    public static final Parameter<javax.servlet.http.HttpServletResponse>  RESPONSE = new Parameter<javax.servlet.http.HttpServletResponse>("response", javax.servlet.http.HttpServletResponse.class);
    public static final Parameter<javax.servlet.http.HttpServletRequest>   REQUEST  = new Parameter<javax.servlet.http.HttpServletRequest>("request",  javax.servlet.http.HttpServletRequest.class);
    public static final Parameter<org.mmbase.bridge.Cloud>                 CLOUD    = new Parameter<org.mmbase.bridge.Cloud> ("cloud",  org.mmbase.bridge.Cloud.class);

    /**
     * 'system' parameter set for nodefunctions.
     * @since MMBase-1.8
     */
    public static final Parameter<org.mmbase.bridge.Node>  NODE     = new Parameter<org.mmbase.bridge.Node>("_node",     org.mmbase.bridge.Node.class);
    public final static Parameter CORENODE = new Parameter("_corenode", Object.class); // object because otherwise problems with RMMCI which doesn't have MMObjectNode.



    public static final Parameter<String> FIELD    = new Parameter<String>("field",    String.class);

    /**
     * An empty Parameter array.
     */
    @SuppressWarnings("unchecked")
    public static final Parameter[] EMPTY  = new Parameter[0];

    @SuppressWarnings({ "unchecked", "cast" })
    public static final <C> Parameter<C>[] emptyArray() {
        return (Parameter<C>[]) EMPTY;
    }

    /**
     * @since MMBase-1.9
     */
    public static Parameter<?>[] readArrayFromXml(Element element) {
        List<Parameter<?>> list = new ArrayList<Parameter<?>>();
        org.w3c.dom.NodeList params = element.getChildNodes();
        for (int i = 0 ; i < params.getLength(); i++) {
            Node n = params.item(i);
            if (n instanceof Element && "param".equals(n.getNodeName())) {
                Parameter<?> parameter = readFromXml((Element) n);
                list.add(parameter);
            }
        }
        return  list.toArray(Parameter.emptyArray());
    }

    /**
     * @since MMBase-1.9
     */
    public static <C> Parameter<C> readFromXml(Element element) {
        String name = element.getAttribute("name");
        String regex = element.getAttribute("regex");

        String type = element.getAttribute("type");
        String required = element.getAttribute("required");
        String description   = element.getAttribute("description"); // actually description as attribute is not very sane

        boolean dataTypeDefined;
        DataType dataType;
        if (! "".equals(type)) {
            dataTypeDefined = false;
            Class<C> clazz = (Class<C>) getClassForName(type);
            dataType = DataTypes.createDataType(name, clazz);
        } else {
            dataTypeDefined = true;
            NodeList dataTypeElements = element.getElementsByTagNameNS(DataType.XMLNS, "datatype");
            Element dataTypeElement = (Element) dataTypeElements.item(0);
            String base = dataTypeElement.getAttribute("base");
            BasicDataType baseDataType = DataTypes.getSystemCollector().getDataType(base);
            try {
                dataType = DataTypeReader.readDataType(dataTypeElement, baseDataType, new DataTypeCollector(new Object())).dataType;
            } catch (DependencyException de) {
                throw new IllegalArgumentException(de);
            }
        }


        Parameter<C> parameter =
            ! "".equals(regex) ?
            new PatternParameter<C>(java.util.regex.Pattern.compile(regex), dataType) :
            new Parameter<C>(name, dataType);
        if (! "".equals(description)) {
            parameter.getLocalizedDescription().set(description, null); // just set it for the default locale...
        }
        if (required.length() > 0 || ! dataTypeDefined) {
            parameter.dataType.setRequired("true".equals(required));
        }

        // check for a default value
        if (element.getFirstChild() != null && ! dataTypeDefined) {
            parameter.setDefaultValue(parameter.autoCast(org.mmbase.util.xml.DocumentReader.getNodeTextValue(element)));
        }
        return parameter;
    }

    /**
     * @since MMBase-1.9
     */
    public static Class<?> getClassForName(String type) {
        Class<?> clazz;
        try {
            boolean fullyQualified = type.indexOf('.') > -1;
            if (!fullyQualified) {
                if (type.equals("int")) { // needed?
                    clazz = int.class;
                } else if (type.equals("NodeList")) {
                    clazz = org.mmbase.bridge.NodeList.class;
                } else if (type.equals("Node")) {
                    clazz =  org.mmbase.bridge.Node.class;
                } else {
                    clazz = Class.forName("java.lang." + type);
                }
            } else {
                clazz = Class.forName(type);
            }
        } catch (ClassNotFoundException cne) {
            log.warn("Cannot determine parameter type : '" + type + "', using Object as type instead.");
            clazz = Object.class;
        }
        return clazz;
    }


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
        dataType     = (DataType<C>) in.readObject();
    }

    /**
     * The parameter's data type
     * @since MMBase-1.8
     */
    protected DataType<C> dataType;

    /**
     * Create a Parameter object
     * @param name the name of the parameter
     * @param dataType the datatype of the parameter to copy
     * @since MMBase-1.8
     */
    public Parameter(String name, DataType<C> dataType) {
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
    public Parameter(String name, DataType<C> dataType, boolean copy) {
        super(name);
        if (copy) {
            this.dataType = dataType.clone(name);
        } else {
            this.dataType = dataType;
        }
    }


    /**
     * Create a Parameter object
     * @param name the name of the parameter
     * @param type the class of the parameter's possible value
     */
    public Parameter(String name, Class<C> type) {
        super(name);
        dataType = DataTypes.createDataType(name, type);
    }

    /**
     * Create a Parameter object
     * @param name the name of the parameter
     * @param type the class of the parameter's possible value
     * @param required whether the parameter requires a value
     */
    public Parameter(String name, Class<C> type, boolean required) {
        this(name, type);
        dataType.setRequired(required);
    }

    /**
     * Create a Parameter object
     * @param name the name of the parameter
     * @param type the class of the parameter's possible value
     * @param defaultValue the value to use if the parameter has no value set
     */
    public Parameter(String name, Class<C> type, C defaultValue) {
        this(name, type);
        dataType.setDefaultValue(defaultValue);
    }

    private Parameter(String name) {
        super();
        key = name;
        dataType = null;
    }

    @SuppressWarnings("unchecked")
    public Parameter(String name, C defaultValue) {
        this(name, (Class<C>) defaultValue.getClass());
        dataType.setDefaultValue(defaultValue);
    }

    @SuppressWarnings("unchecked")
    protected static <C> Class<C> getClass(C v) {
        return (Class<C>) (v == null ? Object.class : v.getClass());
    }

    /**
     * Create Parameter definition by example value
     * @since MMBase-1.9
     */
    public Parameter(Map.Entry<String, C> entry) {
        this(entry.getKey(), getClass(entry.getValue()));
    }

    /**
     * Copy-constructor, just to copy it with different requiredness
     */
    public Parameter(Parameter<C> p, boolean required) {
        this(p.key, p.getDataType());
        dataType.setRequired(required);
    }

    /**
     * Copy-constructor, just to copy it with different defaultValue (which implies that it is not required now)
     */
    public Parameter(Parameter<C> p, C defaultValue) {
        this(p.key, p.getDataType());
        dataType.setDefaultValue(defaultValue);
    }

    /**
     * Returns the default value of this parameter (derived from the datatype).
     * @return the default value
     */
    public C getDefaultValue() {
        return dataType.getDefaultValue();
    }

    /**
     * Sets the default value of this parameter.
     * @param defaultValue the default value
     */
    public void setDefaultValue(C defaultValue) {
        dataType.setDefaultValue(defaultValue);
    }

    /**
     * Returns the data type of this parameter.
     * @return the datatype
     * @since MMBase-1.8
     */
    public DataType<C> getDataType() {
        return dataType;
    }

    /**
     * Returns the type of values that this parameter accepts.
     * @return the type as a Class
     */
    public Class<C> getTypeAsClass() {
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
     * @param value the value whose type (class) to check
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
    protected C autoCast(Object value) {
        return dataType.cast(value, null, null);
    }

    /**
     * @since MMBase-1.9
     */
    public boolean matches(String key) {
        return getName().equals(key);
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
            Parameter<?> a = (Parameter<?>) o;
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

        public Wrapper(Parameter... arg) {
            super("[ARRAYWRAPPER]");
            arguments = arg;
        }

        /**
         * @since MMBase-1.9
         */
        public Parameter[] getArguments() {
            return arguments;
        }

        // this toString makes the wrapping invisible in the toString of a wrapping Parameter[]
        public String toString() {
            StringBuilder buf = new StringBuilder();
            for (Parameter p : arguments) {
                if (buf.length() > 0) buf.append(", ");
                buf.append(p.toString());

            }
            return buf.toString();

        }
    }


}
