/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.core;

import java.net.*;
import java.util.*;
import org.mmbase.util.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * Builds a MultiCast Thread to receive  and send
 * changes from other MMBase Servers.
 * @javadoc
 *
 * @author Daniel Ockeloen
 * @author Rico Jansen
 * @version $Id: MMBaseMultiCast.java,v 1.13 2002-03-11 10:42:36 pierre Exp $
 */
public class MMBaseMultiCast implements MMBaseChangeInterface,Runnable {

    /**
     * @javadoc
     */
    public static String multicastaddress="ALL-SYSTEMS.MCAST.NET";
    /**
     * @javadoc
     * @bad-constant should be made configurable
     */
    public static int dpsize=64*1024;
    /**
     * @javadoc
     */
    public static int mport=4243;

    // logging
    private static Logger log = Logging.getLoggerInstance(MMBaseMultiCast.class.getName());

    /**
     * @javadoc
     * @scope private
     */
    Thread kicker = null;
    /**
     * @javadoc
     * @scope private
     */
    MMBase parent=null;
    /**
     * @javadoc
     * @scope private
     */
    int follownr=1;

    /**
     * @javadoc
     * @scope private
     */
    public int incount=0;
    /**
     * @javadoc
     * @scope private
     */
    public int outcount=0;
    /**
     * @javadoc
     * @scope private
     */
    public int spawncount=0;

    private Vector waitingNodes = new Vector();
    private Queue nodesTosend=new Queue(64);
    private Queue nodesTospawn=new Queue(64);
    private MultiCastChangesSender mcs;
    private MultiCastChangesReceiver mcr;

    /**
     * @javadoc
     */
    public MMBaseMultiCast(MMBase parent) {
        this.parent=parent;
        if (parent.multicasthost!=null) multicastaddress=parent.multicasthost;
        if (parent.multicastport!=-1) mport=parent.multicastport;
        init();
    }

    /**
     * @javadoc
     */
    public void init() {
        this.start();
    }

    /**
     * Starts the admin Thread.
     */
    public void start() {
        /* Start up the main thread */
        if (kicker == null) {
            kicker = new Thread(this,"MMBaseMultiCast");
            kicker.start();
            mcs=new MultiCastChangesSender(this,nodesTosend);
            mcr=new MultiCastChangesReceiver(this,nodesTospawn);
        }
    }

    /**
     * Stops the admin Thread.
     */
    public void stop() {
        /* Stop thread */
        kicker.setPriority(Thread.MIN_PRIORITY);
        kicker = null;
    }

    /**
     * admin probe, try's to make a call to all the maintainance calls.
     */
    public void run() {
        try {
            kicker.setPriority(Thread.NORM_PRIORITY+1);
            doWork();
        } catch(Exception e) {
            log.error(Logging.stackTrace(e));
        }
    }

    /**
     * admin probe, try's to make a call to all the maintainance calls.
     * @todo determine what encoding to use on receiving packages
     */
    public void doWork() {
        InetAddress ia=null;
        String s;
        StringTokenizer tok;

        log.debug("MMBaseMultiCast started");
        try {
            ia = InetAddress.getByName(multicastaddress);
            MulticastSocket ms = new MulticastSocket(mport);
            ms.joinGroup(ia);
            while (kicker!=null) {
                DatagramPacket dp = new DatagramPacket(new byte[dpsize], dpsize);
                try {
                    ms.receive(dp);
                    // maybe we should use encoding here?
                    s=new String(dp.getData(),0,dp.getLength());
                    nodesTospawn.append(s);
                } catch (Exception f) {
                    log.error(Logging.stackTrace(f));
                }
            }
        } catch(Exception e) {
            log.error(Logging.stackTrace(e));
        }
    }

    /**
     * @javadoc
     */
    public boolean handleMsg(String machine,String vnr,String id,String tb,String ctype) {

        // check if MMBase is 100% up and running, if not eat event
        if (!parent.getState()) return true;

        //System.out.println("M='"+machine+"' vnr='"+vnr+"' id='"+id+"' tb='"+tb+"' ctype='"+ctype+"'");
        MMObjectBuilder bul=parent.getMMObject(tb);
        if (bul==null) {
            log.error("Unknown builder=" + tb);
            return false;
        }
        if (machine.equals(parent.machineName)) {
            if (bul!=null) {
                new MMBaseMultiCastProbe(this,bul,machine,id,tb,ctype,false);
            }
        } else {
            try {
                if (!ctype.equals("g")) {
                    new MMBaseMultiCastProbe(this,bul,machine,id,tb,ctype,true);
                } else {
                    if (bul!=null) {
                        MMObjectNode node=bul.getNode(id);
                        if (node!=null) {
                            // well send it back !
                            String chars=parent.machineName+","+(follownr++)+","+id+","+tb+",x,"+node.toXML()+"\n";
                            nodesTosend.append(chars);

                        } else {
                            log.error("can't get node " + id);
                        }
                    } else {
                        log.error("can't find builder " + bul);
                    }
                }
            } catch(Exception e) {
                log.error(Logging.stackTrace(e));
            }
        }
        return true;
    }

    /**
     * @javadoc
     */
    public boolean changedNode(int nodenr,String tableName,String type) {
        String chars=parent.machineName+","+(follownr++)+","+nodenr+","+tableName+","+type;
        nodesTosend.append(chars);
        return true;
    }

    /**
     * @javadoc
     */
    public boolean waitUntilNodeChanged(MMObjectNode node) {
        try {
            MMBaseMultiCastWaitNode wnode = new MMBaseMultiCastWaitNode(node);
            waitingNodes.addElement(wnode);
            wnode.doWait(60*1000);
            waitingNodes.removeElement(wnode);
        } catch(Exception e) {
            log.error(Logging.stackTrace(e));
        }
        return true;
    }

    /**
     * @javadoc
     */
    public void checkWaitingNodes(String snumber) {
        try {
            int number=Integer.parseInt(snumber);
            for (Enumeration e=waitingNodes.elements();e.hasMoreElements();) {
                MMBaseMultiCastWaitNode n=(MMBaseMultiCastWaitNode)e.nextElement();
                if (n.doNotifyCheck(number)) {
                    waitingNodes.removeElement(n);
                    log.debug("waitingNodes size=" + waitingNodes.size());
                }
            }
        } catch(Exception e) {
            log.error("not a valid number " + snumber);
        }
    }

    /**
     * @javadoc
     */
    public boolean commitXML(String machine,String vnr,String id,String tb,String ctype,String xml) {
        try {
        //System.out.println("M='"+machine+"' vnr='"+vnr+"' id='"+id+"' tb='"+tb+"' ctype='"+ctype+"'"+xml);
        MMObjectBuilder bul=parent.getMMObject(tb);
        if (bul==null) {
            log.error("Unknown builder=" + tb);
            return false;
        }
        if (machine.equals(parent.machineName)) {
            // do nothing
        } else {
            MMObjectNode node=bul.getNode(id);
            if (node!=null) {
                // well send it back !
                mergeXMLNode(node,xml);
                node.commit();
            } else {
                log.error("can't get node "+id);
            }
        }
        } catch(Exception e) {
            log.error(Logging.stackTrace(e));
        }
        return true;
    }

    /**
     * @javadoc
     */
    private void mergeXMLNode(MMObjectNode node,String body) {
        StringTokenizer tok = new StringTokenizer(body,"\n\r");
        String xmlline=tok.nextToken();
        String docline=tok.nextToken();

        String builderline=tok.nextToken();
        String endtoken="</"+builderline.substring(1);

        // weird way
        String nodedata=body.substring(body.indexOf(builderline)+builderline.length());
        nodedata=nodedata.substring(0,nodedata.indexOf(endtoken));

        int bpos=nodedata.indexOf("<");
        while (bpos!=-1) {
            String key=nodedata.substring(bpos+1);
            key=key.substring(0,key.indexOf(">"));
            String begintoken="<"+key+">";
            endtoken="</"+key+">";

            String value=nodedata.substring(nodedata.indexOf(begintoken)+begintoken.length());
            value=value.substring(0,value.indexOf(endtoken));

            // set the node
            int dbtype=node.getDBType(key);
            if (!key.equals("number") && !key.equals("otype") && !key.equals("owner"))
                node.setValue( key, dbtype, value );

            nodedata=nodedata.substring(nodedata.indexOf(endtoken)+endtoken.length());
            bpos=nodedata.indexOf("<");
        }
    }
}

