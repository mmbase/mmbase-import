/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.applications.templateengine;

import java.io.PrintWriter;
import java.util.Properties;

/**
 * Abstract class implementing the get/set properties of Component 
 * @author Kees Jongenburger
 */
public abstract class AbstractComponent implements Component {
    Properties properties = new Properties();
    Component parent = null;
    String name;
    String desc;

    public abstract void render(WhiteBoard wb, PrintWriter writer) throws Exception;
    public abstract void renderRelative(String path, WhiteBoard wb) throws Exception;

    public String getProperty(String key) {
        return properties.getProperty(key);
    }

    public void setProperty(String key, String value) {
        properties.setProperty(key, value);
    }
    public Properties getProperties() {
        return properties;
    }
    public void setProperties(Properties properties){
    	this.properties = (Properties)properties.clone();
    }
    public void setParentComponent(Component component){
    	this.parent = component;
    }
    
	public Component getParentComponent(){
		return parent;
	}
	
	public void setName(String name ){
		this.name = name;
	}
	
	public String getName(){
		return name;
	}
	
	public void setDescription(String desc){
		this.desc = desc;
	}
	
	public String getDescription(){
		return desc;
	}
	
	public Object clone() throws CloneNotSupportedException{
		throw new CloneNotSupportedException();
	}
}
