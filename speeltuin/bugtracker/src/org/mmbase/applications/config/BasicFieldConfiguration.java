package org.mmbase.applications.config;

/**
 *
 * @author Kees Jongenburger
 */
public class BasicFieldConfiguration implements FieldConfiguration{
    String name;
    String type;
    String size;
    
    /** Creates a new instance of BasicFieldConfiguration */
    public BasicFieldConfiguration(String name,String type , String size) {
        this.name = name;
        this.type = type;
        this.size = size;
    }
    
    public String getName(){
        return name;
    }
    
    public String getType(){
        return type.toUpperCase();
    }
    
    public String getSize(){
	if (size == null && type.equalsIgnoreCase("string")){
		return "127";
	}
        return size;
    }
    
    public String getGUIName() {
        return getName();
    }
    
    public String getGUIType() {
        return getType().toLowerCase();
    }
}
