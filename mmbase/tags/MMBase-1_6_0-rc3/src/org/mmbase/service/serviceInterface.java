/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.service;

/**
 * @javadoc
 * @rename ServiceInterface
 * @author Daniel Ockeloen
 */
public interface serviceInterface {
    /**
     * @javadoc
     */
    public void startUp();
    /**
     * @javadoc
     */
    public void shutDown();
}
