/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module;

/**
 * @javadoc
 * @application SCAN
 * @rename IdInterface
 * @deprecated no implementation of this interface exists, and the only class that references the interface
 *             (scanparser) never instantiates it.
 * @author vpro
 * @version $Id: idInterface.java,v 1.1 2005-07-14 20:22:06 nklasens Exp $
 */
public interface idInterface {
    public String getState(String userName, String name);
    public String getValue(String userName, String name);
    public String setValue(String userName, String name, String value);
    public String setState(String userName, String name, String value);
}
