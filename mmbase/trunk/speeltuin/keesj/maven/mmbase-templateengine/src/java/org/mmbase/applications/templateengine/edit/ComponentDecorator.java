package org.mmbase.applications.templateengine.edit;

import org.mmbase.applications.templateengine.*;
import org.mmbase.applications.templateengine.jsp.JSPComponent;


public class ComponentDecorator extends JSPComponent {
	Component component;
	
	
    public ComponentDecorator( Component component){
    	super("/te/edit/componentdecorator.jsp");
    	this.component = component;
    }
    
    public Component getDecoratedComponent(){
    	return component;
    }
    
    public String getName(){
    	return component.getName();
    }
}
