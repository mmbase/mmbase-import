/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
/*
$Id: MMBaseContext.java,v 1.6 2000-10-15 22:50:25 gerard Exp $

$Log: not supported by cvs2svn $
Revision 1.5  2000/03/30 13:11:39  wwwtech
Rico: added license

Revision 1.4  2000/03/29 10:48:19  wwwtech
Rob: Licenses changed

Revision 1.3  2000/02/24 14:40:44  wwwtech
Davzev added CVS again

Revision 1.2  2000/02/24 13:57:38  wwwtech
Davzev added CVS comment.

*/
package org.mmbase.module.core;

import java.util.*;
import java.sql.*;
import java.io.*;
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
 * @$Revision: 1.6 $ $Date: 2000-10-15 22:50:25 $
 */
public class MMBaseContext {
    private static String   classname   = "org.mmbase.module.core.MMBaseContext";
    private static boolean debug = false;
    private static void     debug( String msg ) {
        System.out.println( classname +":"+ msg );
    }

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
        boolean returnValue=true;
        
        // the config dir has to contain the following files:
        // - accounts.properties
        // - modules.xml
        // - magic.xml  (dont know if i have 2 check this one 4 sure)
        File accounts= new File(c + "/accounts.properties");
        File modules= new File(c + "/modules.xml");
        File mmbaseroot= new File(c + "/modules/mmbaseroot.xml");
        File jdbc = new File(c + "/modules/jdbc.xml");
        File modulesdir = new File(c + "/modules");
        File builders = new File(c + "/builders");
                
        // if all missing, great change that config path is wrong
        boolean allMissing= !(accounts.exists() || modules.exists() || mmbaseroot.exists() 
                              || jdbc.exists() || modulesdir.exists() || builders.exists());

        if(allMissing) {
            debug("ERROR: wrong configdirectory");                                            
            returnValue = false;
        } else {
            if(! accounts.exists()) {
                debug("ERROR: file 'accounts.properties' missing in mmbase.config dir");                                           
                returnValue = false;
            }
            if(! modules.exists()) {
                debug("ERROR: file 'modules.xml' missing in mmbase.config dir");                                         
                returnValue = false;
            }
            if(! mmbaseroot.exists()) {
                debug("ERROR: file 'modules/mmbaseroot.xml' missing in mmbase.config dir");                                         
                returnValue = false;
            }                    
            if(! jdbc.exists()) {
                debug("ERROR: file 'modules/jdbc.xml' missing in mmbase.config dir");                                         
                returnValue = false;
            }  
            if(! modulesdir.exists()) {
                debug("ERROR: dir 'modules' missing in mmbase.config dir");                                         
                returnValue = false;
            }
            if(! builders.exists()) {
                debug("ERROR: dir 'builders' missing in mmbase.config dir");                                         
                returnValue = false;
            }
        }
        configpath=c;
        return(returnValue);
    }

	public static String getConfigPath() {
		return(configpath);
	} 

}
