/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.implementation;
import org.mmbase.bridge.BridgeError;
import org.mmbase.util.logging.*;

/**
 * This exception gets thrown when something goes wronmg on the MMCI.
 */
public class BasicBridgeException extends BridgeException {

	/**
	* Logger routine
	*/
	private static Logger log = Logging.getLoggerInstance(BridgeException.class.getName());

	/**
	 * Create the exception.
 	 */
	public BasicBridgeException() {
	}
	
	/**
	 * Create the exception.
	 * @param message a description of the exception
 	 */
	public BasicBridgeException (String message) {
		super(message);
		log.error(message);
	}
}
