package org.mmbase.applications.config;

import org.mmbase.applications.config.*;

/**
 *
 * @author Kees Jongenburger
 */
public class BasicNodeManagerConfiguration implements NodeManagerConfiguration{
    String parentNodeManagerName ="object";
    FieldConfigurations fieldConfigurations;
    String name;
    String maintainer = "mmbase.org";
    String version = "1";
    String classFile = null;
    String searchAge = "360";
    String description ;
    
    /** Creates a new instance of BasicNodeManagerConfiguration */
    public BasicNodeManagerConfiguration() {
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
    
    public void setDescription(String description){
        this.description = description;
    }
    
    public String getDescription() {
        if (description == null){
            return getName();
        }
        return description;
    }

    public void addFieldConfiguration(FieldConfiguration fieldConfiguration){
        fieldConfigurations.add(fieldConfiguration);
    }
    public void setExtends(String extendsString){
        this.parentNodeManagerName = extendsString;
    }
    public String getExtends() {
        return parentNodeManagerName;
    }
    
    public FieldConfigurations getFieldConfigurations() {
        return fieldConfigurations;
    }
    
    public String getName() {
        return name;
    }
    
}
