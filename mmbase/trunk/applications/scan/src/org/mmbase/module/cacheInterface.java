/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
/*  
$Log: not supported by cvs2svn $
*/
package org.mmbase.module;
 
import org.mmbase.util.LRUHashtable;

/**
 * The interface class for the cache module.
 *
 * @author  $Author: wwwtech $
 * @version $Revision: 1.6 $
 */
public interface cacheInterface {
	public void init();
	public LRUHashtable lines();
	public boolean clear();
	public cacheline get(Object key);
	public cacheline put(Object key,Object value);
	public cacheline remove(Object key);
}
