/*

VPRO (C)

This source file is part of mmbase and is (c) by VPRO until it is being
placed under opensource. This is a private copy ONLY to be used by the
MMBase partners.

*/
package org.mmbase.module;

import java.lang.*;
import java.net.*;
import java.util.*;
import java.io.*;

import org.mmbase.util.*;

/**
 * The module which provides access to a filesystem residing in
 * a database
 *
 * @author Daniel Ockeloen
 */
public class pages extends Module implements pagesInterface {
	
	Hashtable pages = new Hashtable();

	public void init() {
	}

	public void reload() {
	}

	public void onload() {
	}

	public void unload() {
	}

	public void shutdown() {
	}
	
	public pageInfo getPage(String wanted) {
		pageInfo page=(pageInfo)pages.get(wanted);
		if (page==null) {
			page=new pageInfo();
			pages.put(wanted,page);
		}
		return(page);	
	}
	
	public String getValue(pageInfo page,String wanted) {
		if (page!=null) {
			return(page.getValue(wanted));
		} else {
			return(null);
		}
	}

	public String setValue(pageInfo page,String key,String value) {
		if (page!=null) {
			return(page.setValue(key,value));
		} else {
			return(null);
		}
	}

	/**
	 * SimpleModule
	 */
	public pages() {
	}

}
