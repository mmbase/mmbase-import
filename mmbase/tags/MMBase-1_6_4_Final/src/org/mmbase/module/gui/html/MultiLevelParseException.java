/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.gui.html;

/**
 * This exception gets thrown when a Syntax Error occures in a Multi Level Listing.
 */
public class MultiLevelParseException extends org.mmbase.module.ParseException {
	
	public MultiLevelParseException(String msg) {
		super("MULTILEVEL Syntax error: "+msg);
	}
}
