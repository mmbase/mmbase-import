/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.remote;

import java.util.*;

public interface MMProtocolDriver {
	public boolean commitNode(String nodenr,String tableName,String xml);
	public boolean addListener(String buildername,String nodenr,RemoteBuilder serv);
	public boolean getNode(String nodenr,String tableName);
	public int getLocalPort();
	public String getLocalHost();
	public String getProtocol();
	public String toString();
}
