/*

VPRO (C)

This source file is part of mmbase and is (c) by VPRO until it is being
placed under opensource. This is a private copy ONLY to be used by the
MMBase partners.

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
