/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.builders;

/**
 * &author David V van Zeventer
 * @version $Id: DrivePropsNotFoundException.java,v 1.5 2003-03-10 11:50:18 pierre Exp $
 */
public class DrivePropsNotFoundException extends Exception {
	public String errval;
	public String explanation;

	public DrivePropsNotFoundException(String errval,String explanation){
		this.errval = errval;
		this.explanation = explanation;
	}
}

