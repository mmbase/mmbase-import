/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.builders.protocoldrivers;

/**
 * @javadoc
 * @deprecated-now not used anymore
 * @author  vpro
 * @version $Id: ProtocolDriver.java,v 1.5 2004-10-08 11:49:06 pierre Exp $
 */
public interface ProtocolDriver {
    public void init(String host, int port);
    public boolean commitNode(String nodenr,String tableName,String xml);
    //public boolean addListener(String buildername,String nodenr,RemoteBuilder serv);
    public String getProtocol();
    public String toString();
    public boolean signalRemoteNode(String number, String builder, String ctype);
}
