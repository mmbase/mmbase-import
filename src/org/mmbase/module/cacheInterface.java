package org.mmbase.module;
 

import java.util.*;

public interface cacheInterface {
	public void init();
	public Hashtable lines();
	public boolean clear();
	public cacheline get(Object key);
	public cacheline put(Object key,Object value);
}
