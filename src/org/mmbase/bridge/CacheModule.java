/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge;
 
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;

public interface CacheModule {
	public String get(String pool,String key,String line);
	public String put(String pool,String key,String value);
}
