/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util.platform;

import java.util.Vector;

public class setUserTest {
setUser setit=new setUser();
boolean b=false;

	public setUserTest() {
		b=setit.setUserGroup("wwwtech","www");
		System.out.println("setUser : "+b);
	}

    public static void main(String args[]) {
		setUserTest srv;
		srv=new setUserTest();
		System.exit(0);
    }
}
