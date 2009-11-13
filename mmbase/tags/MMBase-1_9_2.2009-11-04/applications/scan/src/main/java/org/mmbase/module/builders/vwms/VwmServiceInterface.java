/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.builders.vwms;

/**
 * Interface for VWMs that handle file servicing.
 * This interface is used for VWMs that need to be invoked by the scancache module whenever a file is to
 * be cached through the CACHE PAGE directive.
 * VWMS implementing this interface keep track of filechanges and update the filecache when needed.
 * @see PageMaster
 *
 * @application SCAN
 * @author Daniel Ockeloen
 * @author Pierre van Rooden (javadocs)
 * @version $Id$
 */
public interface VwmServiceInterface extends VwmInterface {

    /**
     * Handles a service-request on a file, registered in the netfiles builder.
     * @param number Number of the node in the netfiles buidler than contain service request information.
     * @param ctype the type of change to that node
     * @return <code>true</code> if the request was succesfully handled
     */
    public boolean fileChange(String number,String ctype);

    /**
     * Handles a service-request on a file, registered in the netfiles builder.
     * @param service the service to be performed
     * @param subservice the subservice to be performed
     * @param filename the filename to service
     * @return <code>true</code> if the request was handled
     */
    public boolean fileChange(String service,String subservice,String filename);
}
