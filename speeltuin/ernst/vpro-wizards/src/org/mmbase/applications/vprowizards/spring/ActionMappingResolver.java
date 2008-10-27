/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/ 
package org.mmbase.applications.vprowizards.spring;

/**
 * The purpose of this interface is to assist the Command instance with mapping 
 * 
 * @author ebunders
 *
 */
public interface ActionMappingResolver {
	public String getName(Class clazz);
}
