/*

VPRO (C)

This source file is part of mmbase and is (c) by VPRO until it is being
placed under opensource. This is a private copy ONLY to be used by the
MMBase partners.

*/
package org.mmbase.module;
 

import java.util.*;

public interface cacheInterface {
	public void init();
	public Hashtable lines();
	public boolean clear();
	public cacheline get(Object key);
	public cacheline put(Object key,Object value);
}
