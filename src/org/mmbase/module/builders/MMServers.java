/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.module.builders;

import java.util.*;

import org.mmbase.module.core.*;
import org.mmbase.util.functions.*;
import org.mmbase.util.logging.*;
import org.mmbase.storage.search.implementation.*;
import org.mmbase.storage.search.*;

/**
 * @javadoc
 * mmservers stands for MMBase servers. It is possible to run multiple mmbase servers on one database instance.
 * Every mmserver node represent a real MMBase server(think of it as a machine where one instance of MMBase is running).
 * On startup MMBase looks in the mmservers table and looks if he is listed in the list of mmservers,
 * if not MMBase create a new node containing imfornation about itselve(name/host/os/jdk). the mmservers builder has extra behaviour,
 * it can communicate with other servers(using multicast). The basic funtionality it provides however is sending information
 * about changes of node to other mmservers (Listen !! I just have changed node 123). This mechanisme makes it possible to keep
 * nodes caches in sync but also makes it possible to split tasks between machines. You could for example have a server that encodes video.
 *  when a change to a certain node is made one of the servers (if wel configured) can start encoding the videos.
 * @author  vpro
 * @version $Id$
 */
public class MMServers extends MMObjectBuilder implements MMBaseObserver, Runnable, org.mmbase.datatypes.resources.StateConstants {

    private static final Logger log = Logging.getLoggerInstance(MMServers.class);
    private int serviceTimeout = 60 * 15; // 15 minutes
    private long intervalTime = 60 * 1000; // 1 minute

    private boolean checkedSystem = false;
    private String javastr;
    private String osstr;
    private Vector possibleServices = new Vector();

    /**
     * Function uptime
     * @since MMBase-1.8
     */
    protected Function getUpTime = new AbstractFunction("uptime", Parameter.EMPTY, ReturnType.LONG) {
            {
                setDescription("The function 'uptime' returns the uptime of the current server.");
            }
            public Object getFunctionValue(Parameters parameters) {
                int now = (int) (System.currentTimeMillis() / 1000);
                return new Long(now - MMBase.startTime);
            }
        };
    {
        addFunction(getUpTime);
    }

    /**
     * @javadoc
     */
    public MMServers() {
        javastr = System.getProperty("java.version") + "/" + System.getProperty("java.vm.name");
        osstr = System.getProperty("os.name") + "/" + System.getProperty("os.version");
    }

    public boolean init() {
        if (oType != -1)
            return true; // inited already
        if (!super.init())
            return false;
        String tmp = getInitParameter("ProbeInterval");
        if (tmp != null) {
            intervalTime = (long)Integer.parseInt(tmp) * 1000;
            log.service("ProbeInterval was configured to be " + intervalTime / 1000 + " seconds");
        } else {
            log.service("ProbeInterval defaults to " + intervalTime / 1000 + " seconds");
        }
         tmp = getInitParameter("ServiceTimeout");
        if (tmp != null) {
            serviceTimeout = (int)Integer.parseInt(tmp);
            log.service("ServiceTimeout was configured to be " + serviceTimeout + " seconds");
        } else {
            log.service("ServiceTimeout defaults to " + serviceTimeout + " seconds");
        }
        start();
        return true;
    }

    /**
     * Starts the thread for the task scheduler
     * @since MMBase-1.7
     */
    protected void start() {
        MMBaseContext.startThread(this,"MMServers");
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
            int now = (int) (System.currentTimeMillis() / 1000);
            int uptime = now - MMBase.startTime;
            return getUptimeString(uptime);
        }
        return super.getValue(node, field);
    }

    /**
     * @javadoc
     */
    private String getUptimeString(int uptime) {
        StringBuffer result = new StringBuffer();
        if (uptime >= (24 * 3600)) {
            int d = uptime / (24 * 3600);
            result.append(d).append(" d ");
            uptime -= d * 24 * 3600;
        }
        if (uptime >= 3600) {
            int h = uptime / 3600;
            result.append(h).append(" h ");
            uptime -= h * 3600;
        }
        if (uptime >= 60) {
            int m = uptime / (60);
            result.append(m).append(" m ");
            uptime -= m * 60;
        }
        result.append(uptime).append(" s");
        return result.toString();
    }

    /**
     * run, checkup probe runs every intervaltime to
     * set the state of the server (used in clusters)
     * @since MMBase-1.7
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
                log.debug(Thread.currentThread().getName() +" was interrupted.");
                continue;
            }
        }
    }

    /**
     * @javadoc
     */
    private void doCheckUp() {
        try {
            boolean imoke = false;
            String machineName = mmb.getMachineName();
            String host = mmb.getHost();
            log.debug("doCheckUp(): machine=" + machineName);
            for (Iterator iter = getMMServerNodes().iterator(); iter.hasNext();) {
                MMObjectNode node = (MMObjectNode) iter.next();
                String name = node.getStringValue("name");
                String h    = node.getStringValue("host");
                log.debug("Checking " + name + "@" + h);
                if (name.equals(machineName) && h.equals(host)) {
                    imoke = checkMySelf(node);
                } else {
                    checkOther(node);
                }
            }
            if (!imoke) {
                createMySelf(machineName);
            }
        } catch (Exception e) {
            log.error("Something went wrong in MMServers Checkup Thread " + e.getMessage(), e);
        }
    }

    /**
     * Returns all the nodes from the builder without loading it in the nodes cache of MMBase.
     * @return The nodes.
     * @throws SearchQueryException when something fails on database level.
     */
    public List getMMServerNodes() throws SearchQueryException {
        List nodes = storageConnector.getNodes(new NodeSearchQuery(this), false);
        if (nodes != null) {
            return nodes;
        }
        return new ArrayList();
    }
    
    /**
     * @javadoc
     */
    private boolean checkMySelf(MMObjectNode node) {
        boolean state = true;
        log.debug("checkMySelf() updating timestamp");
        node.setValue("state", ACTIVE);
        node.setValue("atime", (int) (System.currentTimeMillis() / 1000));
        if (!checkedSystem) {
            node.setValue("os", osstr);
            node.setValue("host", mmb.getHost());
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
        int now = (int) (System.currentTimeMillis() / 1000);
        int then = node.getIntValue("atime");
        if (log.isDebugEnabled()) {
            log.debug("" + now + ": Checking " + node.getValue("name")  + " (updated at  " + then + ", " + (now - then) + " s ago, interval: " + serviceTimeout + " s )" );
        }
        if ((now - then) > (serviceTimeout)) {
            if (node.getIntValue("state") != INACTIVE) {
                log.debug("checkOther() updating state for " + node.getStringValue("host"));
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
        node.setValue("name", machineName);
        node.setValue("state", ACTIVE);
        node.setValue("atime", (int) (System.currentTimeMillis() / 1000));
        node.setValue("os", osstr);
        node.setValue("host", mmb.getHost());
        node.setValue("jdk", javastr);
        insert("system", node);
    }

    /**
     * @javadoc
     */
    private void setServicesDown(MMObjectNode node) {
        Enumeration f = possibleServices.elements();
        log.debug("setServicesDown() for " + node);
        while (f.hasMoreElements()) {
            String type = (String)f.nextElement();
            Enumeration e = mmb.getInsRel().getRelated(node.getIntValue("number"), type);
            while (e.hasMoreElements()) {
                MMObjectNode node2 = (MMObjectNode)e.nextElement();
                MMObjectBuilder parent = node2.getBuilder();
                log.info("setServicesDown(): downnode(" + node2 + ") REMOVING node");
                parent.removeRelations(node2);
                parent.removeNode(node2);

                //node2.setValue("state","down");
                //node2.commit();
            }
        }
        if (log.isDebugEnabled()) {
            log.debug("setServicesDown() for " + node + " done");
        }
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
     * @javadoc
     */
    public String getMMServerProperty(String mmserver, String key) {
        String value = getInitParameter(mmserver + ":" + key);
        return value;
    }

    /**
     * @javadoc
     */
    public MMObjectNode getMMServerNode(String name) {
        return getMMServerNode(name, null);
    }

    /**
     * @since MMBase-1.8.3
     */
    public MMObjectNode getMMServerNode(String name, String host) {
        NodeSearchQuery query = new NodeSearchQuery(this);
        Constraint constraint = new BasicFieldValueConstraint(query.getField(getField("name")), name);
        if (host != null) {
            BasicCompositeConstraint comp = new BasicCompositeConstraint(CompositeConstraint.LOGICAL_AND);
            comp.addChild(constraint);
            BasicFieldValueConstraint constraint2 = new BasicFieldValueConstraint(query.getField(getField("host")), host);
            comp.addChild(constraint2);
            constraint = comp;
        }
        query.setConstraint(constraint);
        try {
            List nodeList = getNodes(query);
            if (nodeList.size() > 0) {
                return (MMObjectNode) nodeList.get(0);
            } else {
                log.info("Can't find any mmserver node with name=" + name);
                return null;
            }
        } catch (SearchQueryException sqe) {
            log.warn(sqe);
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
     * MMServer object are field by field equals.
     */

    public boolean equals(MMObjectNode o1, MMObjectNode o2) {
        return o1 == null ? o2 == null : o2 != null && (o1.getNumber() == o2.getNumber() && o1.getValue("name").equals(o2.getValue("name")) && o1.getValue("host").equals(o2.getValue("host")));
    }

    public String toString(MMObjectNode n) {
        return "" + n.getValue("name") + "@" + n.getValue("host");
    }



    protected NodeSearchQuery query = null;
    /**
     * @return List of MMObjectNodes representing  active servers, which are not this server.
     */
    public List getActiveServers() {
        String machineName = mmb.getMachineName();
        String host        = mmb.getHost();
        if (log.isDebugEnabled()) {
            log.debug("machine=" + machineName + " host=" + host);
        }
        if (query == null) {
            query = new NodeSearchQuery(this);
            BasicFieldValueConstraint constraint1a = new BasicFieldValueConstraint(query.getField(getField("name")), machineName);
            BasicFieldValueConstraint constraint1b = new BasicFieldValueConstraint(query.getField(getField("host")), host);
            BasicCompositeConstraint constraint1 = new BasicCompositeConstraint(CompositeConstraint.LOGICAL_AND);
            constraint1.addChild(constraint1a);
            constraint1.addChild(constraint1b);
            constraint1.setInverse(true);
            BasicFieldValueConstraint constraint2 = new BasicFieldValueConstraint(query.getField(getField("state")), new Integer(ACTIVE));
            BasicCompositeConstraint constraint = new BasicCompositeConstraint(CompositeConstraint.LOGICAL_AND);
            constraint.addChild(constraint1);
            constraint.addChild(constraint2);
            query.setConstraint(constraint);
            StepField field = query.getField(getField(FIELD_NUMBER));
            BasicSortOrder so = query.addSortOrder(field);
            so.setDirection(SortOrder.ORDER_DESCENDING);
        }

        try {
            return storageConnector.getNodes(query, false);
        } catch (org.mmbase.storage.search.SearchQueryException sqe) {
            log.error(sqe);
            return new ArrayList();
        }
    }
}
