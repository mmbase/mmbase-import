package nl.vpro.util;

import junit.framework.TestCase;

public class StringUtilTest extends TestCase {

	public void testStartsWith(){
	
		String a = "dit is een test";
		String b = "dit";
		String c = "test";
		String d = "dit is een dit";
		
		assertTrue(StringUtil.startsWith(a, b));
		assertFalse(StringUtil.startsWith(a, d));
		assertFalse(StringUtil.startsWith(b, c));
		assertTrue(StringUtil.startsWith(d, b));
	}
	
	public void testEndsWith(){
		String a = "dit is een test";
		String b = "dit";
		String c = "\\s{1}test";
		String d = "dit is een dit";
		assertTrue(StringUtil.endsWith(a, c));
		assertTrue(StringUtil.endsWith(b,b));
		assertFalse(StringUtil.endsWith(a, b));
		
	}
}
