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
