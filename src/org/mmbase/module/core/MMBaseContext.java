/* -*- tab-width: 4; -*-

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
/*
$Id: MMBaseContext.java,v 1.8 2001-04-10 17:32:05 michiel Exp $

$Log: not supported by cvs2svn $
Revision 1.7  2000/12/24 23:26:25  daniel
removed modules.xml warning

Revision 1.6  2000/10/15 22:50:25  gerard
gerard: added some checks
submitted by Eduard Witteveen

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

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;


/**
 * Using MMBaseContext class you can retrieve the servletContext from anywhere using the get method.
 * Currently the servletContext is set by class servscan in the init() method.
 * 
 * @version 23 December 1999
 * @author Daniel Ockeloen
 * @author David van Zeventer
 * @$Revision: 1.8 $ $Date: 2001-04-10 17:32:05 $
 */
public class MMBaseContext {

    private static Logger log = Logging.getLoggerInstance(MMBaseContext.class.getName()); 

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
            log.error("wrong configdirectory");                                            
            returnValue = false;
        } else {
            if(! accounts.exists()) {
                log.error("file 'accounts.properties' missing in mmbase.config dir");                                           
                returnValue = false;
            }
            if(! modules.exists()) {
                // log.error("file 'modules.xml' missing in mmbase.config dir");                                         
                returnValue = false;
            }
            if(! mmbaseroot.exists()) {
                log.error("file 'modules/mmbaseroot.xml' missing in mmbase.config dir");                                         
                returnValue = false;
            }                    
            if(! jdbc.exists()) {
                log.error("file 'modules/jdbc.xml' missing in mmbase.config dir");                                         
                returnValue = false;
            }  
            if(! modulesdir.exists()) {
                log.error("dir 'modules' missing in mmbase.config dir");                                         
                returnValue = false;
            }
            if(! builders.exists()) {
                log.error("dir 'builders' missing in mmbase.config dir");                                         
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
