/*

VPRO (C)

This source file is part of mmbase and is (c) by VPRO until it is being
placed under opensource. This is a private copy ONLY to be used by the
MMBase partners.

*/
package org.mmbase.module;

import java.util.*;


/**
 * The module which provides access to a filesystem residing in
 * a database
 *
 * @author Daniel Ockeloen
 */
public class pageInfo {
	Hashtable values = new Hashtable();

	public String getValue(String wanted) {
		return((String)values.get(wanted));
	}

	public String setValue(String key,String value) {
		return((String)values.put(key,value));
	}
}

