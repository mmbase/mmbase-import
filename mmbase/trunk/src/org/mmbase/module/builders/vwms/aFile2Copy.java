package org.mmbase.module.builders.vwms;

import java.lang.String;

public class aFile2Copy {
	public String dstuser,dsthost,dstpath,srcpath,filename;

	public aFile2Copy(String dstuser,String dsthost,String dstpath,String srcpath,String filename) {
		this.dstuser=dstuser;
		this.dsthost=dsthost;
		this.dstpath=dstpath;
		this.srcpath=srcpath;
		this.filename=filename;
	}
}
