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
