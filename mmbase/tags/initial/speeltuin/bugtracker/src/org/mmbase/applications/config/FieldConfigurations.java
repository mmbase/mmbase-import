/*
 * FieldConfigurations.java
 *
 * Created on June 13, 2002, 12:53 PM
 */

package org.mmbase.applications.config;

/**
 *
 * @author  mmbase
 */
public class FieldConfigurations extends java.util.Vector {
    
    /** Creates a new instance of FieldConfigurations */
    public FieldConfigurations() {
    }
    
    public FieldConfiguration getFieldConfiguration(int index){
        return (FieldConfiguration)get(index);
    }
}
