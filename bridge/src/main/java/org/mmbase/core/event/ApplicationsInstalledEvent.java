/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.core.event;

/**
 * Application installations functionality of MMAdmin.
 *
 * @author Michiel Meeuwissen
 * @since MMBase-1.9.6
 * @version $Id$
 */
public class ApplicationsInstalledEvent extends SystemEvent {

    private static int instanceCount = 0;

    private int count = instanceCount++;

    /**
     * @since MMBase-2.0
     */
    public int getCount() {
        return count;
    }




}
