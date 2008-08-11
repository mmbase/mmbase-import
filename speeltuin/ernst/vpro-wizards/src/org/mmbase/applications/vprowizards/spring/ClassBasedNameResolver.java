package org.mmbase.applications.vprowizards.spring;

/**
 * This action mapping name resolver takes the class name, turns the first character into lower case,
 * and removes 'Action' if it is part of the class name
 * @author ebunders
 *
 */
public class ClassBasedNameResolver implements ActionMappingResolver {

	public String getName(Class clazz) {
		String className = clazz.getName();
		//remove the path
		className = className.substring(className.lastIndexOf('.') + 1);
		//remove the 'Action' part of the name
		if(className.endsWith("Action")){
			className = className.substring(0, className.length() - 6);
		}
		//make the first character lowercase
		className = className.substring(0,1).toLowerCase() + className.substring(1);
		return className;
	}

}
