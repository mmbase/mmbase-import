/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module;

import java.util.*;
import org.mmbase.util.*;


public interface SendMailInterface {
	public boolean sendMail(String from, String to, String data);
	public boolean sendMail(String from, String to, String data, Hashtable headers);
	public boolean sendMail(Mail mail);
	public String verify(String name);
	public Vector expand(String name);
}
