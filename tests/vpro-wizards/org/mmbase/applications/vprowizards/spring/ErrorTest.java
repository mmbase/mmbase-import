package org.mmbase.applications.vprowizards.spring;

import java.util.*;

import junit.framework.TestCase;

public class ErrorTest extends TestCase {

	public void testGlobalErrors(){
		GlobalError globalError = new GlobalError("error.create.node",Locale.getDefault());
		assertEquals("Can not create node.", globalError.getMessage());
		
		globalError = new GlobalError("error.create.node",Locale.FRENCH);
		assertEquals("Can not create node.", globalError.getMessage());
	}
}
