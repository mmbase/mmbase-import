/*

VPRO (C)

This source file is part of mmbase and is (c) by VPRO until it is being
placed under opensource. This is a private copy ONLY to be used by the
MMBase partners.

*/
package org.mmbase.module;
 
import java.util.*;

public interface UsersInterface {
	public String getProperty(String name, String property);

	public String getServletProperty(String table,String name, String property,int type);
	public String delServletProperty(String table,String name, String property,int type);
	public Hashtable getServletProperties(String table,String name,int type);
	public boolean setServletProperty(String table,String name, String property,String value,int type);

	public String getModuleProperty(String table,String name, String property,int type);
	public boolean setModuleProperty(String table,String name, String property,String value,int type);

	// not clear yet
	public boolean removeUser(String name);
	public boolean storeUser(String name);
	public boolean addUser(String name);
	public boolean addUser(String name,requestInfo req);
	public void addListen(UsersCallBackInterface wanted);
	public void removeListen(UsersCallBackInterface wanted);
	public void signalActive(String name);
}
