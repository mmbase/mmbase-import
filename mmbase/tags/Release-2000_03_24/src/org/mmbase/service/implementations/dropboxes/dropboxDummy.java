/*

VPRO (C)

This source file is part of mmbase and is (c) by VPRO until it is being
placed under opensource. This is a private copy ONLY to be used by the
MMBase partners.

*/
package org.mmbase.service.implementations.dropboxes;

import java.net.*;
import java.lang.*;
import java.io.*;
import java.util.*;

import org.mmbase.service.interfaces.*;


/**
 */
public class dropboxDummy implements dropboxInterface {

	private String dir;
	private String cmd;
	


	public String getVersion() {		
		return("0.0");
	}


	public String doDir( String cmds ) {
		return("");
	}

	public void setDir(String dir) {
		this.dir=dir;
	}

	public void setCmd(String cmd) {
		this.cmd=cmd;
	}
}
