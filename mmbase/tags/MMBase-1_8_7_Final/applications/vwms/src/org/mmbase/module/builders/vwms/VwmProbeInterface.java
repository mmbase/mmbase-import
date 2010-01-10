/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.builders.vwms;

import org.mmbase.module.core.*;

/**
 * Virtual Web Master Probe interface.
 * A VWM that runs a scheduler should implement this interface.
 * The routines defined here are entry routines for the probe, needed to perform scheduled tasks.
 *
 * @application VWMs
 * @deprecated with only one implementation, better to not use an interface?
 * @author Daniel Ockeloen
 * @author Pierre van Rooden (javadocs)
 * @version $Id: VwmProbeInterface.java,v 1.7 2004-10-08 10:57:57 pierre Exp $
 */
public interface VwmProbeInterface {

    /**
    * Performs general periodic maintenance.
    * @return <code>true</code> if maintenance was performed, <code>false</code> otherwise
    */
    public boolean probeCall();

    /**
    * Returns the name of the VWM.
    */
    public String getName();

    /**
    * Performs maintenance based on a Vwmtasknode.
    * @param node The Vwmtask node that describes the task to be performed.
    * @return <code>true</code> if maintenance was performed, <code>false</code> if it failed
    */
    public boolean performTask(MMObjectNode node);
}
