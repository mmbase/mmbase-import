/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.module.database.support;

/**
 * Class dTypeInfo
 *
 * @deprecated This code is scheduled for removal once MMBase has been fully converted to the new
 *             StorageManager implementation.
 * @javadoc
 * @rename DTypeInfo
  */

public class dTypeInfo {
public int mmbaseType;
public String dbType;
public int minSize=-1;
public int maxSize=-1;

    public dTypeInfo() {
    }

}
