/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.config;

/**
 * Report Interface
 *
 * @javadoc
 * @application Config
 * @version $Id: ReportInterface.java,v 1.2 2004-09-29 14:26:49 pierre Exp $
 */
public interface ReportInterface {
    //public ReportInterface(String mode);
    public void init(String mode, String encoding);
    public String label();
    public String report();
}
