/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
/*
	$Id: ImageRequest.java,v 1.1 2000-06-05 14:42:14 wwwtech Exp $

	$Log: not supported by cvs2svn $
*/
package org.mmbase.module.builders;

import java.util.*;

/**
 * @author Rico Jansen
 * @version $Id: ImageRequest.java,v 1.1 2000-06-05 14:42:14 wwwtech Exp $
 */
public class ImageRequest {
	String ckey;
	Vector params;
	byte[] in;
	byte[] out;
	int id;
	int count=0;

	public ImageRequest(int id,String ckey,Vector params,byte[] in) {
		this.id=id;
		this.ckey=ckey;
		this.in=in;
		this.out=null;
		this.params=params;
		count=0;
	}

	public Vector getParams() {
		return(params);
	}

	public String getKey() {
		return(ckey);
	}

	public byte[] getInput() {
		return in;
	}

	public int getId() {
		return id;
	}

	public synchronized byte[] getOutput() {
		if (out==null) {
			count++;
			try {
				wait();
			} catch (InterruptedException e) { }
		}
		return out;
	}

	public synchronized void setOutput(byte[] output) {
		out=output;
		count=0;
		notifyAll();
	}

	public int count() {
		return(count);
	}
}
