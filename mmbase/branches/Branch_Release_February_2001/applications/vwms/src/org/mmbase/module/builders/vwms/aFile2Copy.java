/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.builders.vwms;

import java.lang.String;

public class aFile2Copy {
	public String dstuser,dsthost,dstpath,srcpath,filename,sshpath;

	public aFile2Copy(String dstuser,String dsthost,String dstpath,String srcpath,String filename,String sshpath) {
		this.dstuser=dstuser;
		this.dsthost=dsthost;
		this.dstpath=dstpath;
		this.srcpath=srcpath;
		this.sshpath=sshpath;
		this.filename=filename;
	}

	public int hashCode() {
		return((srcpath+filename).hashCode());
	}
}
