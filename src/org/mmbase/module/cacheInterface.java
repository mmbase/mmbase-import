/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
/*  
$Log: not supported by cvs2svn $
Revision 1.6  2000/06/20 15:21:03  wwwtech
Davzev: added cvs comments.

*/
package org.mmbase.module;
 
import org.mmbase.util.LRUHashtable;

/**
 * The interface class for the cache module.
 *
 * @rename CacheInterface
  * @author  $Author: pierre $
 * @version $Revision: 1.7 $
 */
public interface cacheInterface {
	public void init();
	public LRUHashtable lines();
	public boolean clear();
	public cacheline get(Object key);
	public cacheline put(Object key,Object value);
	public cacheline remove(Object key);
}
