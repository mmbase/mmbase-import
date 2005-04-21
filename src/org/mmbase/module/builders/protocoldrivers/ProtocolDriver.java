/*

VPRO (C)

This source file is part of mmbase and is (c) by VPRO until it is being
placed under opensource. This is a private copy ONLY to be used by the
MMBase partners.

*/
package org.mmbase.module.builders.protocoldrivers;

import java.util.*;

public interface ProtocolDriver {
	public void init(String host, int port);
	public boolean commitNode(String nodenr,String tableName,String xml);
	//public boolean addListener(String buildername,String nodenr,RemoteBuilder serv);
	public String getProtocol();
	public String toString();
	public boolean signalRemoteNode(String number, String builder, String ctype);
}
