/*
 * DocletNodeManagerConfiguration.java
 *
 * Created on June 13, 2002, 12:48 PM
 */

package org.mmbase.applications.mmbasedoclet;

import org.mmbase.applications.config.*;

/**
 *
 * @author  mmbase
 */
public class DocletNodeManagerConfiguration implements NodeManagerConfiguration{
    String parentNodeManagerName ="object";
    FieldConfigurations fieldConfigurations;
    String name;
    
    /** Creates a new instance of DocletNodeManagerConfiguration */
    public DocletNodeManagerConfiguration() {
        fieldConfigurations = new FieldConfigurations();
    }
    
    public void setName(String name){
        this.name = name;
    }
    
    public void addFieldConfiguration(FieldConfiguration fieldConfiguration){
        fieldConfigurations.add(fieldConfiguration);
    }
    
    public String getExtends() {
        return parentNodeManagerName;
    }
    
    public FieldConfigurations getFieldConfigurations() {
        return fieldConfigurations;
    }
    
    public String getNodeManagerName() {
        return name;
    }
    
}
