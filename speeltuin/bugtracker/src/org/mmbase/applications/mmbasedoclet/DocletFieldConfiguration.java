/*
 * DocletFieldConfiguration.java
 *
 * Created on June 13, 2002, 12:59 PM
 */

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
        return type;
    }
    
    public String getSize(){
        return size;
    }
}
