/*
$Id: MMBaseContext.java,v 1.4 2000-03-29 10:48:19 wwwtech Exp $

$Log: not supported by cvs2svn $
Revision 1.3  2000/02/24 14:40:44  wwwtech
Davzev added CVS again

Revision 1.2  2000/02/24 13:57:38  wwwtech
Davzev added CVS comment.

*/
package org.mmbase.module.core;

import java.util.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;

import org.mmbase.module.*;
import org.mmbase.module.database.*;

/**
 * Using MMBaseContext class you can retrieve the servletContext from anywhere using the get method.
 * Currently the servletContext is set by class servscan in the init() method.
 * 
 * @version 23 December 1999
 * @author Daniel Ockeloen
 * @author David van Zeventer
 * @$Revision: 1.4 $ $Date: 2000-03-29 10:48:19 $
 */
public class MMBaseContext {

	static ServletContext servletContext;
	static String configpath;

	public static boolean setServletContext(ServletContext sx) {
		servletContext=sx;
		return(true);
	} 

	public static ServletContext getServletContext() {
		return(servletContext);
	} 


	public static boolean setConfigPath(String c) {
		configpath=c;
		return(true);
	} 

	public static String getConfigPath() {
		return(configpath);
	} 

}
