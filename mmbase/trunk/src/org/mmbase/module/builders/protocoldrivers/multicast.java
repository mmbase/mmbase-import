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
 * @rename MultiCast
 * @author Daniel Ockeloen
 * @version $Id: multicast.java,v 1.7 2004-10-08 11:49:06 pierre Exp $
 */
public class multicast implements ProtocolDriver {


    String remoteHost;
    int remotePort;

    public multicast() {
    }

    public void init(String remoteHost,int remotePort) {
        this.remoteHost=remoteHost;
        this.remotePort=remotePort;
    }


    public boolean commitNode(String nodename,String tableName,String xml) {
        return true;
    }


    public String getProtocol() {
        return "multicast";
    }


    public boolean signalRemoteNode(String number, String builder, String ctype) {
        return true;
    }
}
