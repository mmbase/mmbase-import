/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.applications.templateengine;

/**
 * A template container is as it names sugest a container for templates
 * the template container displays the templates using the selected layout manager
 * @author Kees Jongenburger
 */
public interface Container extends Component {
	public Components getComponents();
	/**
	 * add a component to the container
	 * this method sould call setParentComponent of the caller
	 * @param component the component to add
	 */
	public void addComponent(Component component);
	public void addComponent(Component component,String hint);
	public LayoutManager getLayoutManager();
	public void setLayoutManager(LayoutManager layoutManager);
}
