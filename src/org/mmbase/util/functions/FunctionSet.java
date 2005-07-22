/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util.functions;

import java.util.*;

/**
 * The implementation of one set ('namespace') of functions. Objects of this type are managed by {@link org.mmbase.util.functions.FunctionSets}.
 *
 * @javadoc
 * @author Daniel Ockeloen
 * @version $Id: FunctionSet.java
 * @since MMBase-1.8
 */
public class FunctionSet {

    private String name, status, version, description;

    /**
     * String -> Function
     */
    private Map functions = new HashMap();

    public FunctionSet(String name, String version, String status, String description) {
        this.name        = name;
        this.version     = version;
        this.status      = status;
        this.description = description;
    }

    /**
     * Adds a Function to this set.
     * @param fun The to-be-added Function object
     */
    public void addFunction(Function fun) {
        functions.put(fun.getName(), fun);
    }

    /**
     * Gets a Function from this set. Used by {@link FunctionSets} which manages all set-functions.
     */
    public Function getFunction(String name) {
        Object o = functions.get(name);
        if (o != null) {
            return (Function)o;
        }
        return null;
    }


    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

    public String getStatus() {
        return status;
    }

    public String getDescription() {
        return description;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return An unmodifiable Map (String -> {@link Function}) containing all functions of this set.
     */
    public Map getFunctions() {
        return Collections.unmodifiableMap(functions);
    }

}
