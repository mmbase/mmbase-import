/*
 * DocletFieldConfiguration.java
 *
 * Created on June 13, 2002, 12:59 PM
 **/

package org.mmbase.applications.mmbasedoclet;

import org.mmbase.applications.config.*;
/**
 *
 * @author  mmbase
 */
public class DocletFieldConfiguration implements FieldConfiguration{
    String name;
    String type;
    String size;
    
    /** Creates a new instance of DocletFieldConfiguration */
    public DocletFieldConfiguration(String name,String type , String size) {
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
