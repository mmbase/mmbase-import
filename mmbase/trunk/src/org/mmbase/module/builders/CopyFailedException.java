package org.mmbase.module.builders;

/**
 * &author David V van Zeventer
 * @version 5 Jan 1999
 */
public class CopyFailedException extends Exception {
	public String errval;
	public String explanation;

	public CopyFailedException(String errval,String explanation){
		this.errval = errval;
		this.explanation = explanation;
	}
}

