/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.builders.vwms;

import java.util.*;
import java.util.Properties;
import java.io.*;

import org.mmbase.util.logging.*;
import org.mmbase.module.core.*;
import org.mmbase.module.builders.*;

/**
 * Virtual Web Master base object.
 * Most VWM's inherit fromt his base class.
 * The Vwm contains routines for performing tasks at certain intervals.
 * The interval is determined from the maintime value in the Vwms builder (Maintenance time in seconds),
 * Each interval, the vwm invokes the ProbeCall() method.
 * VWMs perform periodic maintenance by overrideing the probeCall method (the default doesn't do anything).
 * A VWM is also called a 'Bot'.
 *
 * @application VWMs
 * @author Daniel Ockeloen
 * @author Pierre van Rooden (javadocs)
 * @version $Id$
 */

public class Vwm implements VwmInterface, VwmProbeInterface, Runnable {

    // Logger class
    private static Logger log = Logging.getLoggerInstance(Vwm.class);

    // temporary debug method... has to change later on!
    protected void debug(String msg) {
        log.debug(msg);
    }

    /**
    * Scheduler of tasks depending on VWMtask nodes associated with this Vwm.
    */
    protected VwmProbe probe;

    /**
    * Sleep time in seconds.
    * This is the interval in which the VWM performs it's maintenance probes.
    */
    protected int sleeptime;

    /**
    * The creation node of this VWM.
    * Used to retrieve the maintenance time and to maintain VWM state information.
    */
    protected MMObjectNode wvmnode;

    /**
    * Name of the VWM.
    * Retrieved from the node from the VWMs builder, but can also be set manually by the VWM's overriding class.
    */
    protected String name = "Unknown";

    /**
    * The VWMs builder that holds the VWM's node.
    * The same as the <code>parent</code> attribute of the vwm node (but a bit easier in use).
    * @see #getVwmNode
    */
    protected Vwms Vwms;

    /**
    * Thread in which the VWM runs.
    * This field can be used to stop the VWM.
    * Setting kicker to null (either from outside the thread or within) will cause the VWM
    * to terminate it's {@link #run} method.
    */
    Thread kicker = null;

    /**
    * What clients are using this VWM.
    * Each client implements the {@link VwmCallBackInterface}, and can be invoked when important changes occur.
    */
    Vector<VwmCallBackInterface> clients = new Vector<VwmCallBackInterface>();

    /**
    * Initialize the Vwm.
    * Called by the Vwms builder that starts the VWM.
    * Sets a few fields, creates a new probe instance, and starts a new thread.
    * @param vwmnode
    * @param Vwms The VWMs builder. It is not really necessary as this is the same as the <code>parent</code> attribute of <code>vwmnode</code>.
    */
    public void init(MMObjectNode vwmnode, Vwms Vwms) {
        this.wvmnode = vwmnode;
        this.name = vwmnode.getStringValue("name");
        this.sleeptime = wvmnode.getIntValue("maintime");
        this.Vwms = Vwms;
        /* or :
            this.Vwms = (Vwms)vwmnode.parent;
        */
        probe = new VwmProbe(this);
        this.start();
    }

    /**
     * Starts the thread for the Vwm.
     */
    public void start() {
        /* Start up the main thread */
        if (kicker == null) {
            kicker = new Thread(this, "Vwm : " + name);
            kicker.setDaemon(true);
            kicker.start();
        }
    }

    /**
     * Stops the Vwm's thread.
     * Sets the kicker field to null, which causes the run method to terminate.
     */
    public void stop() {
        /* Stop thread */
        kicker.interrupt();
        kicker = null;
    }

    /**
     * VWM maintenance scheduler.
     * Calls the {@link #probeCall} method, after which the thread sleeps for a number of seconds as set in {@link #sleeptime}.
     */
    public void run() {
        while (kicker != null) {
            try {
                probeCall();
            } catch (Exception e) {
                log.error("Vwm : Got a Exception in my probeCall : " + e.getMessage());
                log.error(Logging.stackTrace(e));
            }
            try {
                Thread.sleep(sleeptime * 1000);
            } catch (InterruptedException e) {
                //interrupted so exit
                return;
            }
        }
    }

    /**
    * Add a client to the listen queue of the wvm.
    * @param client The client-object to add
    * @return <code>true</code> if the client was added, <code>false</code> if it already existed in the queue.
    */
    public boolean addClient(VwmCallBackInterface client) {
        if (clients.contains(client)) {
            log.warn("Vwm : " + name + " allready has the client : " + client + ".");
            return false;
        } else {
            clients.addElement(client);
            return true;
        }
    }

    /**
    * Release a client from the listen queue of the wvm.
    * @param client The client-object to release
    * @return <code>true</code> if the client was released, <code>false</code> if it did not exist in the queue.
    */
    public boolean releaseClient(VwmCallBackInterface client) {
        if (clients.contains(client)) {
            clients.removeElement(client);
            return true;
        } else {
            log.warn("Vwm : " + name + " got a release call from : " + client + " but have no idea who he is.");
            return false;
        }
    }

    /**
    * Performs periodic maintenance.
    * This method is called by the VWM's own {@link #run} method.
    * Since this does not actually do anything, perhaps this method should be abstract.
    * @return <code>true</code> if maintenance was performed, <code>false</code> otherwise
    */
    public boolean probeCall() {
        log.info("Vwm probe call : " + name);
        return false;
    }

    /**
    * Adds a new task to the list of taks to perform.
    * Passes the task to the VWM's probe, which handles the of tasks.
    * @param node the node describing the task (from the Vwmtasks builder)
    * @return <code>true</code> is the task was succesfully added.
    */
    public boolean putTask(MMObjectNode node) {
        return probe.putTask(node);
    }

    /**
    * Returns the name of the VWM.
    */
    public String getName() {
        return name;
    }

    /**
    * Performs maintenance based on a Vwmtasknode.
    * This method is called by the {@link #probe} object assoviated with the VWM.
    * the default method sets a status field to indicate an error, and sends an error email.
    * Perhaps this method should be abstract.
    * @param node The Vwmtask node that describes the task to be performed.
    * @return <code>true</code> if maintenance was performed, <code>false</code> if it failed
    */
    public boolean performTask(MMObjectNode node) {
        log.error("Vwm : performTask not implemented in : " + name);
        node.setValue("status", Vwmtasks.STATUS_ERROR);
        node.commit();

        Vwms.sendMail(name, "performTask not implemented", "");
        return false;
    }

    /**
    * Signals that the task node is claimed.
    * Sets the status of the task to 'claimed', as well as the machinename that is claiming the task.
    * Setting a task to 'claimed' prevents it from being scheduled for performance by the Vwmtask builder.
    * @param node The VwmTask node that describes the task
    * @return <code>true</code> if teh task's state was cahnged, <code>false</code> if it fails.
    */
    protected boolean claim(MMObjectNode node) {
        node.setValue("status", Vwmtasks.STATUS_CLAIMED);
        node.setValue("claimedcpu", Vwms.getMachineName());
        return node.commit();
    }

    /**
    * Signals that the task should be performed again (possibly after an initial failure).
    * Sets the status of the task to 'request'.
    * Setting a task to 'request' allows it to be scheduled for performance by the Vwmtask builder.
    * @param node The VwmTask node that describes the task
    * @return <code>true</code> if teh task's state was cahnged, <code>false</code> if it fails.
    */
    protected boolean rollback(MMObjectNode node) {
        node.setValue("status", Vwmtasks.STATUS_REQUEST);
        return node.commit();
    }

    /**
    * Signals that the task to be performed failed.
    * Sets the status of the task to 'error'.
    * Setting a task to 'error' prevents it from being scheduled for performance by the Vwmtask builder.
    * @param node The VwmTask node that describes the task
    * @return <code>true</code> if the task's state was cahnged, <code>false</code> if it fails.
    */
    protected boolean failed(MMObjectNode node) {
        node.setValue("status", Vwmtasks.STATUS_ERROR);
        return node.commit();
    }

    /**
    * Signals that the task to be performed was successful and has finished.
    * Sets the status of the task to 'done'.
    * Setting a task to 'done' prevents it from being scheduled for performance by the Vwmtask builder.
    * @param node The VwmTask node that describes the task
    * @return <code>true</code> if teh task's state was cahnged, <code>false</code> if it fails.
    */
    protected boolean performed(MMObjectNode node) {
        node.setValue("status", Vwmtasks.STATUS_DONE);
        return node.commit();
    }

    /**
    * Converts a string of properties to a Hashtable.
    * Used to parse the 'data' field of a VwmTask node.
    * The property format is a string with a property on each line in the format: name = value
    * Very generic, should probably be moved to the MMObjectNode class (as in getPropertiesValue(fieldname) ).
    * @param props the properties string
    * @return a <code>hashtable</code> with the property name=value pairs.
    */
    protected Hashtable parseProperties(String props) {
        //assume properties are in default encoding
        //it might also be possible to props.getBytes("utf-8");
        byte[] bytes = props.getBytes();
        Properties p = new Properties();
        try {
            p.load(new ByteArrayInputStream(bytes));
        } catch (IOException e) {
            log.error("failed to load the String ["+props +"] into the properties object due to IOException (this might be an encoding problem) stacktrace" + Logging.stackTrace(e));
        }
        return p;
    }

    /**
    * Retrieves the creation node of this VWM.
    */
    public MMObjectNode getVwmNode() {
        return wvmnode;
    }

    /**
    * Called when a local node is changed.
    * @param machine Name of the machine that changed the node.
    * @param number Number of the changed node as a <code>String</code>
    * @param builder type of the changed node
    * @param ctype command type, 'c'=changed, 'd'=deleted', 'r'=relations changed, 'n'=new
    * @return <code>true</code> if maintenance was performed, <code>false</code> (the default) otherwise
    */
    public boolean nodeRemoteChanged(String machine, String number, String builder, String ctype) {
        return false;
    }

    /**
    * Called when a remote node is changed.
    * @param machine Name of the machine that changed the node.
    * @param number Number of the changed node as a <code>String</code>
    * @param builder type of the changed node
    * @param ctype command type, 'c'=changed, 'd'=deleted', 'r'=relations changed, 'n'=new
    * @return <code>true</code> if maintenance was performed, <code>false</code> (the default) otherwise
    */
    public boolean nodeLocalChanged(String machine, String number, String builder, String ctype) {
        return false;
    }
}
