/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.builders;

import java.util.*;

import org.mmbase.module.core.*;
import org.mmbase.storage.search.*;
import org.mmbase.storage.search.implementation.*;

import org.mmbase.util.logging.*;

/**
 * admin module, keeps track of all the worker pools
 * and adds/kills workers if needed (depending on
 * there load and info from the config module).
 *
 * @sql
 * @version $Id$
 * @author Daniel Ockeloen
 */
public class PropertiesProbe implements Runnable {

    private static Logger log = Logging.getLoggerInstance(PropertiesProbe.class.getName());

    Thread kicker = null;
    Properties parent = null;

    public PropertiesProbe(Properties parent) {
        this.parent = parent;
        init();
    }

    public void init() {
        this.start();
    }


    /**
     * Starts the admin Thread.
     */
    public void start() {
        /* Start up the main thread */
        if (kicker == null) {
            kicker = new Thread(this,"PropertiesProbe");
            kicker.setDaemon(true);
            kicker.start();
        }
    }

    /**
     * Stops the admin Thread.
     */
    public void stop() {
        /* Stop thread */
        kicker.interrupt();
        kicker = null;
    }

    /**
     */
    public void run () {
        while (kicker != null) {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                return;
            }
            if (parent.getMachineName().equals("test1")) doExpire();
        }
    }

    private void doExpire() {
        try {
            NodeSearchQuery query = new NodeSearchQuery(parent);
            StepField keyField = query.getField(parent.getField("key"));
            BasicFieldValueConstraint constraint1 = new BasicFieldValueConstraint(keyField, "LASTVISIT");
            StepField valueField = query.getField(parent.getField("value"));
            BasicFieldValueConstraint constraint2 = new BasicFieldValueConstraint(valueField, new Integer(10536));
            constraint2.setOperator(FieldCompareConstraint.LESS);

            BasicCompositeConstraint constraint = new BasicCompositeConstraint(CompositeConstraint.LOGICAL_AND);
            constraint.addChild(constraint1);
            constraint.addChild(constraint2);

            query.setConstraint(constraint);

            List nodes = parent.getNodes(query);
            int max=0;
            for (Iterator i = nodes.iterator(); i.hasNext() && max<1000;) {
                MMObjectNode node = (MMObjectNode)i.next();
                int number = node.getIntValue("parent");
                log.info("Want delete on : " + number);
                deleteProperties(number);
                deleteUser(number);
                max++;
            }
        } catch (Exception e) {}
    }

    private void deleteProperties(int id) {
        /* quicker, but ugly, so don't use

            try {
                DataSource dataSource = (DataSource) parent.mmb.getStorageManagerFactory().getAttribute(Attributes.DATA_SOURCE);
                Connection con = dataSource.getConnection();
                Statement stmt = con.createStatement();
                stmt.executeUpdate("delete from " + parent.mmb.baseName+"_" + parent.tableName + " where parent=" + id);
                stmt.close();
                con.close();
            } catch (Exception e) {}
        */
        try {
            NodeSearchQuery query = new NodeSearchQuery(parent);
            List nodes = parent.getNodes(query);
            for (Iterator i = nodes.iterator(); i.hasNext();) {
                MMObjectNode node = (MMObjectNode)i.next();
                parent.removeNode(node);
            }
        } catch (Exception e) {}
    }

    private void deleteUser(int id) {
        MMObjectNode user = parent.getNode(id);
        if (user != null) {
            user.getBuilder().removeNode(user);
        }
    }
}
