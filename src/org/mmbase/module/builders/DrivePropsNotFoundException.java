/*

VPRO (C)

This source file is part of mmbase and is (c) by VPRO until it is being
placed under opensource. This is a private copy ONLY to be used by the
MMBase partners.

*/
package org.mmbase.module.builders;

/**
 * &author David V van Zeventer
 * @version 11 Dec 1998
 */
public class DrivePropsNotFoundException extends Exception {
	public String errval;
	public String explanation;

	public DrivePropsNotFoundException(String errval,String explanation){
		this.errval = errval;
		this.explanation = explanation;
	}
}

