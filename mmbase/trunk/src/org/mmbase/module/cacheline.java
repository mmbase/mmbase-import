/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module;
 

import java.util.Date;

public class cacheline {

	public String classname = getClass().getName();

	public Date lastmod;
	public byte buffer[]=null;
	public int filesize;
	public String mimetype;

	public cacheline(int len) {
		buffer = new byte[len];
		filesize=len;
	}

	public cacheline() {
	}

	public void debug( String msg )
	{
		System.out.println( classname +":"+ msg );
	}

	public String toString()
	{
		String result = null;
		result = classname + "( "+mimetype+","+lastmod+","+filesize+","+buffer.length+")";
		return result;
	}
}

