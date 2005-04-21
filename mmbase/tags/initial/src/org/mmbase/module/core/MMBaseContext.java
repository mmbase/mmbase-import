/*

VPRO (C)

This source file is part of mmbase and is (c) by VPRO until it is being
placed under opensource. This is a private copy ONLY to be used by the
MMBase partners.

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
