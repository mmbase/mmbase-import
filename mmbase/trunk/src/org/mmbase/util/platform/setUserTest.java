/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util.platform;

import org.mmbase.util.logging.Logging;
import org.mmbase.util.logging.Logger;

/**
 * Class setUserTest
 * 
 * @javadoc
 * @rename SetUserTest
  */

public class setUserTest {

    static private Logger log = Logging.getLoggerInstance(setUserTest.class.getName()); 
    setUser setit=new setUser();
    boolean b=false;
    
    public setUserTest() {
        b=setit.setUserGroup("wwwtech","www");
        log.debug("setUser : "+b);
    }

    public static void main(String args[]) {
      setUserTest srv=new setUserTest();
		System.exit(0);
    }
}
