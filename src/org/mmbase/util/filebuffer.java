/*

VPRO (C)

This source file is part of mmbase and is (c) by VPRO until it is being
placed under opensource. This is a private copy ONLY to be used by the
MMBase partners.

*/
package org.mmbase.util;

import java.util.Date;

public class filebuffer {

	public Date lastmod;
	public byte data[]=null;
	public Object obj;
	public int filesize=0;
	public String mimesuper;
	public String mimesub;
	public String mimetype;

	public filebuffer(Object o) {
		obj=o;
	}

	public filebuffer(byte[] data) {
		this.data=data;
	}

	public filebuffer(int len) {
		data = new byte[len];
		filesize=len;
	}
}

