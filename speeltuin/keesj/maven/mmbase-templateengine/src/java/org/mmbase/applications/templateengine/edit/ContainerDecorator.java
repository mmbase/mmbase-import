/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.applications.templateengine.edit;
import org.mmbase.applications.templateengine.*;
import org.mmbase.applications.templateengine.jsp.*;
/**
 * @author keesj
 * @version $Id: ContainerDecorator.java,v 1.1.1.1 2004-04-02 14:58:47 keesj Exp $
 */
public class ContainerDecorator extends JSPContainer{
	Container container;
	public ContainerDecorator(Container container){
		super("/te/edit/containerdecorator.jsp",container.getLayoutManager());
		this.container = container;
	}
	
	public Container getDecoratedContainer(){
		return container;
	}
	
	public String getName(){
		return container.getName();
	}
}
