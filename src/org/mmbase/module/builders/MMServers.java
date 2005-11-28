/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.module.builders;

import java.util.*;

import org.mmbase.module.core.*;
import org.mmbase.util.logging.*;

/**
 * @javadoc
 * @author  michiel
 * @author  nico
 * @version $Id: MMServers.java,v 1.27.2.2 2005-11-28 18:41:40 pierre Exp $
 */
public class MMServers extends MMObjectBuilder implements MMBaseObserver, Runnable {

    private static final Logger log = Logging.getLoggerInstance(MMServers.class);
    private int serviceTimeout = 60 * 15; // 15 minutes
    private long intervalTime = 60 * 1000; // 1 minute

    public static final int UNKNOWN = -1;
    public static final int ACTIVE = 1;
    public static final int INACTIVE = 2;
    public static final int ERROR = 3;

    private boolean checkedSystem = false;
    private String javastr;
    private String osstr;
    private String host;
    private Vector possibleServices = new Vector();

    /**
     * @javadoc
     */
    public MMServers() {
        javastr = System.getProperty("java.version") + "/" + System.getProperty("java.vm.name");
        osstr   = System.getProperty("os.name") + "/" + System.getProperty("os.version");
    }

    public boolean init() {
        if (oType != -1) return true; // inited already
        if (!super.init()) return false;
        String tmp = getInitParameter("ProbeInterval");
        if (tmp != null) {
            intervalTime = (long) Integer.parseInt(tmp) * 1000;
            log.service("ProbeInterval was configured to be " + intervalTime / 1000 + " seconds");
        } else {
            log.service("ProbeInterval defaults to " + intervalTime / 1000 + " seconds");
        }

    start();
        return true;

    }

    /**
     * Starts the thread for the task scheduler
     */
    protected void start() {
        MMBaseContext.startThread(this,"MMServers");
    }

    /**
     * @javadoc
     * @language
     */
    public String getGUIIndicator(String field,MMObjectNode node) {
        if (field.equals("state")) {
            int val = node.getIntValue("state");
            switch(val) {
                case UNKNOWN: return "Unknown";
                case ACTIVE: return "Active";
                case INACTIVE: return "Inactive";
                case ERROR: return "Error";
                default: return "Unknown";
            }
        } else if (field.equals("atime")) {
            int now = (int)(System.currentTimeMillis()/1000);
            int then = node.getIntValue("atime");
            String tmp = ""+(now-then)+"sec";
            return tmp;
        }
        return null;
    }

    /**
     * @javadoc
     */
    public Object getValue(MMObjectNode node, String field) {
        if (field.equals("showstate")) {
            return getGUIIndicator("state", node);
        } else if (field.equals("showatime")) {
            return getGUIIndicator("atime", node);
        } else if (field.equals("uptime")) {
            // The 'node' object is not used, so this info makes only sense for _this_ server.

            int now= (int)(System.currentTimeMillis()/1000);
            int uptime = now - (int) mmb.startTime;
            return getUptimeString(uptime);
        }
        return super.getValue(node,field);
    }

    /**
     * @javadoc
     */
    private String getUptimeString(int uptime) {
        StringBuffer result = new StringBuffer();
        if (uptime >= (24 * 3600)) {
            int d = uptime/(24 * 3600);
            result.append(d).append(" d ");
            uptime -= d * 24 * 3600;
        }
        if (uptime >= 3600) {
            int h = uptime/3600;
            result.append(h).append(" h ");
            uptime -= h*3600;
        }
        if (uptime>=60) {
            int m = uptime/(60);
            result.append(m).append(" m ");
            uptime -= m * 60;
        }
        result.append(uptime).append(" s");
        return result.toString();
    }


    /**
     * run, checkup probe runs every intervaltime to
     * set the state of the server (used in clusters)
     */
    public void run() {
        while (!mmb.isShutdown()) {
            long thisTime = intervalTime;
            if (mmb != null && mmb.getState()) {
                doCheckUp();
            } else {
                // shorter wait, the server is starting
                thisTime = 2 * 1000; // wait 2 second
            }

            // wait the defined time
            try {
                Thread.sleep(thisTime);
            } catch (InterruptedException e) {
                log.debug(e.toString());
                break;
            }
        }
    }

    /**
     * @javadoc
     */
    private void doCheckUp() {
    try {
            boolean imoke=false;
        String machineName = mmb.getMachineName();
            host = mmb.getHost();
            log.debug("doCheckUp(): machine="+machineName);
            Enumeration e=search("");
            while (e.hasMoreElements()) {
                MMObjectNode node=(MMObjectNode)e.nextElement();
                String tmpname=node.getStringValue("name");
                if (tmpname.equals(machineName)) {
                    imoke = checkMySelf(node);
                } else {
                    checkOther(node);
                }
            }
            if (imoke==false) {
                createMySelf(machineName);
            }
    } catch(Exception e) {
            log.error("Something went wrong in MMServers Checkup Thread" + Logging.stackTrace(e));
    }
    }

    /**
     * @javadoc
     */
    private boolean checkMySelf(MMObjectNode node) {
        boolean state = true;
        String tmphost = node.getStringValue("host");
        /* Why ?
        if (!tmphost.equals(host)) {
            log.warning("MMServers-> Running on a new HOST possible problem");
        }
        */
        log.debug("checkMySelf() updating timestamp");
        node.setValue("state", ACTIVE);
        node.setValue("atime", (int)(System.currentTimeMillis()/1000));
        if (! checkedSystem) {
            node.setValue("os", osstr);
            node.setValue("host", host);
            node.setValue("jdk", javastr);
            checkedSystem = true;
        }
        node.commit();
        log.debug("checkMySelf() updating timestamp done");
        return state;
    }

    /**
     * @javadoc
     */
    private void checkOther(MMObjectNode node) {
        int now=(int)(System.currentTimeMillis()/1000);
        int then=node.getIntValue("atime");
        if ((now-then)>(serviceTimeout)) {
            if (node.getIntValue("state") != INACTIVE) {
                log.debug("checkOther() updating state for "+node.getStringValue("host"));
                node.setValue("state", INACTIVE);
                node.commit();

                // now also signal all its services are down !
                setServicesDown(node);
            }
        }
    }

    /**
     * @javadoc
     */
    private void createMySelf(String machineName) {
        MMObjectNode node = getNewNode("system");
        node.setValue("name",machineName);
        node.setValue("state", ACTIVE);
        node.setValue("atime", (int)(System.currentTimeMillis()/1000));
        node.setValue("os",osstr);
        node.setValue("host",host);
        node.setValue("jdk",javastr);
        insert("system",node);
    }

    /**
     * @javadoc
     */
    private void setServicesDown(MMObjectNode node) {
        Enumeration f=possibleServices.elements();
        log.debug("setServicesDown() for "+node);
        while (f.hasMoreElements()) {
            String type=(String)f.nextElement();
            Enumeration e=mmb.getInsRel().getRelated(node.getIntValue("number"),type);
            while (e.hasMoreElements()) {
                MMObjectNode node2=(MMObjectNode)e.nextElement();
                log.info("setServicesDown(): downnode("+node2+") REMOVING node");
                node2.parent.removeRelations(node2);
                node2.parent.removeNode(node2);

                //node2.setValue("state","down");
                //node2.commit();
            }
        }
        log.debug("setServicesDown() for "+node+" done");
    }

    /**
     * @javadoc
     */
    public void setCheckService(String name) {
        if (!possibleServices.contains(name)) {
            possibleServices.addElement(name);
        }
    }

    /**
     * @deprecated-now does not add anything
     */
    public boolean nodeRemoteChanged(String machine,String number,String builder,String ctype) {
        super.nodeRemoteChanged(machine,number,builder,ctype);
        return nodeChanged(machine,number,builder,ctype);
    }

    /**
     * @deprecated-now does not add anything
     */
    public boolean nodeLocalChanged(String machine,String number,String builder,String ctype) {
        super.nodeLocalChanged(machine,number,builder,ctype);
        return nodeChanged(machine,number,builder,ctype);
    }

    /**
     * @deprecated-now does not add anything
     */
    public boolean nodeChanged(String machine,String number,String builder,String ctype) {
        return true;
    }

    /**
     * @javadoc
     */
    /*
    private void startProtocolDrivers() {
        name2driver=new Hashtable();
        url2driver=new Hashtable();
        Enumeration e=search("");
        while (e.hasMoreElements()) {
            MMObjectNode node=(MMObjectNode)e.nextElement();
            String name=node.getStringValue("name");
            String url=node.getStringValue("host");
            int pos=url.indexOf("://");
            if (pos!=-1) {
                // do i allready have this url driver running ?
                ProtocolDriver pd=(ProtocolDriver)url2driver.get(url);
                if (pd!=null) {
                    name2driver.put(name,pd);
                } else {
                    String tmp=url;
                    String protocol=tmp.substring(0,pos);
                    tmp=tmp.substring(pos+3);
                    pos=tmp.indexOf(':');
                    String host;
                    int port=80;
                    if (pos==-1) {
                        host=tmp;
                    } else {
                        host=tmp.substring(0,pos);
                        try {
                            port=Integer.parseInt(tmp.substring(pos+1));
                        } catch(NumberFormatException nfe) {
                            log.error("Can't parse portnr since value isnt integer but "+tmp.substring(pos+1));
                            log.error(nfe.getMessage());
                            log.error(Logging.stackTrace(nfe));
                        }
                    }

                    try {
                        Class newclass=Class.forName("org.mmbase.module.builders.protocoldrivers."+protocol);
                        pd = (ProtocolDriver)newclass.newInstance();
                        pd.init(host,port);
                        url2driver.put(url,pd);
                        name2driver.put(name,pd);
                        log.info("Started driver("+pd+")");
                    } catch (Exception f) {
                        log.error("Can't load protocolclass("+protocol+")");
                        log.error(f.getMessage());
                        //log.error(Logging.stackTrace(f));
                    }
                }
            }
        }
    }
    */

    /**
     * @javadoc
     */
    public String getMMServerProperty(String mmserver,String key) {
        String value=getInitParameter(mmserver+":"+key);
        return value;
    }

    /**
     * @javadoc
     */
    public MMObjectNode getMMServerNode(String name) {
        Enumeration e=search("name=='"+name+"'");
        if (e.hasMoreElements()) {
            return (MMObjectNode)e.nextElement();
        } else {
            log.info("Can't find any mmserver node with name="+name);
            return null;
        }
    }

    /**
     * @return Returns the intervalTime.
     */
    public long getIntervalTime() {
        return intervalTime;
    }

    /**
     * @return
     */
    public List getActiveServers() {
        List activeServers = new ArrayList();

        String machineName = mmb.getMachineName();
        log.debug("getActiveServers(): machine="+machineName);
        Enumeration e=search("");
        while (e.hasMoreElements()) {
            MMObjectNode node = (MMObjectNode)e.nextElement();
            String tmpname=node.getStringValue("name");
            if (!tmpname.equals(machineName)) {
                if (node.getIntValue("state") == ACTIVE) {
                    activeServers.add(node);
                }
            }
        }
        return activeServers;
    }
}
