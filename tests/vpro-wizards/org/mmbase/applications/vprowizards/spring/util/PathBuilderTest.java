/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.applications.vprowizards.spring.util;

import junit.framework.TestCase;

public class PathBuilderTest extends TestCase {

	public void testPathBuilderNoDubble(){
		PathBuilder pb = new PathBuilder(new String[]{"een","twee","drie"});
		assertEquals("een,twee,drie", pb.getPath());
		assertEquals("een", pb.getStep(0));
		assertEquals("twee", pb.getStep(1));
		assertEquals("drie", pb.getStep(2));
	}
	
	public void testPathBuilderOneDubble(){
		PathBuilder pb = new PathBuilder(new String[]{"een","twee","drie","een"});
		assertEquals("een,twee,drie,een1", pb.getPath());
		assertEquals("een", pb.getStep(0));
		assertEquals("twee", pb.getStep(1));
		assertEquals("drie", pb.getStep(2));
		assertEquals("een1", pb.getStep(3));
	}
	
	public void testPathBuilderMultiDubble(){
		PathBuilder pb = new PathBuilder(new String[]{"een","een","drie","twee","een","drie"});
		assertEquals("een,een1,drie,twee,een2,drie1", pb.getPath());
		assertEquals("een", pb.getStep(0));
		assertEquals("een1", pb.getStep(1));
		assertEquals("drie", pb.getStep(2));
		assertEquals("twee", pb.getStep(3));
		assertEquals("een2", pb.getStep(4));
		assertEquals("drie1", pb.getStep(5));
	}
	
	public void testErrors(){
		PathBuilder pb = new PathBuilder(new String[]{"een","twee","drie"});
		try{
			pb.getStep(100);
			fail();
		}catch(IndexOutOfBoundsException e){
			assertEquals("there are only 3 steps, so index 100 is illegal", e.getMessage());
		}
		
		try{
			pb = new PathBuilder(new String[]{});
			fail();
		}catch (NullPointerException e) {
			assertEquals("the array of steps is null or empty", e.getMessage());
		}
	}
}
