/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module;
 

import java.util.Date;

public class cacheline {

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

	public String toString() {
		String s = new String(buffer);
		return mimetype+","+lastmod+","+filesize+","+buffer.length+", "+s;
	}
}

