package org.mmbase.applications.config;

/**
 * representation of a list of fields
 * @author Kees Jongenburger
 * @version $Id: FieldConfigurations.java,v 1.2 2002-06-27 19:20:30 kees Exp $
 */
public class FieldConfigurations extends java.util.Vector {
    
    /** Creates a new instance of FieldConfigurations */
    public FieldConfigurations() {
    }
    
    public FieldConfiguration getFieldConfiguration(int index){
        return (FieldConfiguration)get(index);
    }
}
