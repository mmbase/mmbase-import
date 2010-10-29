/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.applications.vprowizards.spring;

import java.util.Locale;

import junit.framework.TestCase;

public class ErrorTest extends TestCase {

	public void testGlobalErrors(){
		GlobalError globalError = new GlobalError("error.create.node",Locale.getDefault());
		assertEquals("Can not create node.", globalError.getMessage());
		
		globalError = new GlobalError("error.create.node",Locale.FRENCH);
		assertEquals("Can not create node.", globalError.getMessage());
	}
}
