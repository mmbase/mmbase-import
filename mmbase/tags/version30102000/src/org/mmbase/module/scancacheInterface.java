/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module;
 
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;

public interface scancacheInterface {
	public void init();
	public String get(String pool,String key);
	public String get(String pool,String key,String line);
	public String getNew(String pool,String key,String line);
	public String put(String pool,String key,String value);
	public String newput(String pool,HttpServletResponse res,String key,String value, String mimeType);
	public String newput2(String pool,String key,String value, int cachetype, String mimeType);
	public void remove(String poolName, String key);
}
