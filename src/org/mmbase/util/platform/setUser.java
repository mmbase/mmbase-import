/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util.platform;

import org.mmbase.util.logging.Logging;
import org.mmbase.util.logging.Logger;


public class setUser {
    
    static private Logger log = Logging.getLoggerInstance(setUser.class.getName()); 

    boolean needSetUser;
    String osname;

    public setUser() {
        //log.debug("java.library.path="+System.getProperty("java.library.path"));
        needSetUser=needSetUser();
        if (needSetUser) {
            System.loadLibrary("setUser");
        }
    }

    private boolean needSetUser() {
        boolean rtn=true;
        /* Figure out if need setuid/setgid  * @rename SetUser
 */
        osname=System.getProperty("os.name");
        log.debug("OS "+osname);
        if (osname.equals("Windows 95") || osname.equals("Windows NT")) {
            rtn=false;
        }
        return(rtn);
    }

    public boolean setUserGroup(String user,String group) {
        if (needSetUser) {
            return(setUserGroupNative(user,group));
        } else {
            return(true);
        }
    }

    private native boolean setUserGroupNative(String user,String group);

}
