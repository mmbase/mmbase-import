/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.builders;

import java.util.*;
import java.sql.*;
import java.io.*;
import java.net.*;

import org.mmbase.module.database.*;
import org.mmbase.module.core.*;
import org.mmbase.util.*;

/**
 * @rename Dropboxes
  * @author Daniel Ockeloen
 */
public class dropboxes extends ServiceBuilder implements MMBaseObserver {

	public dropboxes() {
	}

	public boolean nodeRemoteChanged(String machine,String number,String builder,String ctype) {
		super.nodeRemoteChanged(machine,number,builder,ctype);
		return(nodeChanged(machine,number,builder,ctype));
	}

	public boolean nodeLocalChanged(String machine,String number,String builder,String ctype) {
		super.nodeLocalChanged(machine,number,builder,ctype);
		return(nodeChanged(machine,number,builder,ctype));
	}

	public boolean nodeChanged(String machine,String number,String builder,String ctype) {
		return(true);
	}

     	
}
