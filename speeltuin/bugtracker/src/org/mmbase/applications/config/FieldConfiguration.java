package org.mmbase.applications.config;

/**
 * very simple java interface representing 
 * the configuration of a field
 * @author Kees Jongenburger
 **/
public interface FieldConfiguration{
    /**
     * @return the name of the field
     **/
    public String getName();
    
    /**
     * @return the mmbase-type of the field
     **/
    public String getType();
    
    /**
     * if applicable this methods returns the size of the field
     * @return the size of the field
     **/
    public String getSize();
    
    /**
     * some applications need to know how to display the field
     * this method returns a "hint" on how to display the field
     * @return a "hint" on howto display a field
     **/
    public String getGUIType();
    
    /**
     * @return a human readable fieldname
     **/
    public String getGUIName();
}
