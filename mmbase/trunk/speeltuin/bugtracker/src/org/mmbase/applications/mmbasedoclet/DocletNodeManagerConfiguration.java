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
    String maintainer = "mmbase.org";
    String version = "1";
    String classFile = "Dummy";
    String searchAge = "360";
    
    /** Creates a new instance of DocletNodeManagerConfiguration */
    public DocletNodeManagerConfiguration() {
        fieldConfigurations = new FieldConfigurations();
    }
    
    public void setName(String name){
        this.name = name;
    }
    
    public void setMaintainer(String maintainer){
	    this.maintainer = maintainer;
    }
    public String getMaintainer(){
	    return maintainer;
    }
    public void setVersion(String version){
	    this.version = version;
    }
    public String getVersion(){
	    return version;
    }

    public void setClassFile(String classFile){
	    this.classFile = classFile;
    }

    public String getClassFile(){
	    return classFile;
    }

    public void setSearchAge(String searchAge){
	    this.searchAge = searchAge;
    }

    public String getSearchAge(){
	    return searchAge;
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
