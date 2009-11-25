/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/ 
package org.mmbase.applications.vprowizards.spring;

import java.util.ArrayList;
import java.util.List;

import org.mmbase.applications.vprowizards.spring.action.Action;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * This class will create an instance of BasicCommand and set the action classes on it.
 * It uses a given ActionMappingResolver instance to resolve what name to map the action to, or
 * how the actions wille be accesible through the requests.
 * 
 * 
 * @author ebunders
 * 
 */
public class BasicCommandFactory implements CommandFactory {

	private List<Class<? extends Action>> actionClasses = new ArrayList<Class<? extends Action>>();
	private static final Logger log = Logging.getLoggerInstance(BasicCommandFactory.class);
	private ActionMappingResolver nameResolver = new ClassBasedNameResolver();


	/**
	 * @throws RuntimeException
	 *             when the commandClass field is null or something goes wrong with the instantiation process.
	 */
	public Command getNewInstance() {
		BasicCommand command = new BasicCommand();
		for(Class<? extends Action> action: actionClasses){
			command.addAction(nameResolver.getName(action), action);
		}
		return command;
	}
	
	/**
	 * This method accepts a list of classes that implement {@link Action}
	 * Only the classes that qualify are added.
	 * @param actionClasses
	 */
	public void setActionClasses(List<Class<? extends Action>> actionClasses) {
		for(Class<? extends Action> clazz: actionClasses){
			this.actionClasses.add(clazz);
			log.debug("Action class "+clazz+" added.");
		}
	}
	
	public List<Class<? extends Action>> getActionClasses() {
		return actionClasses;
	}

	public ActionMappingResolver getNameResolver() {
		return nameResolver;
	}

	/**
	 * You can insert your own mapping resolver for your actions. 
	 * Default a ClassBasedNameResolver is used
	 * @param actionMappingResolver
	 */
	public void setActionMappingResolver(ActionMappingResolver actionMappingResolver){
		this.nameResolver = actionMappingResolver;
	}

}
