/*

VPRO (C)

This source file is part of mmbase and is (c) by VPRO until it is being
placed under opensource. This is a private copy ONLY to be used by the
MMBase partners.

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
