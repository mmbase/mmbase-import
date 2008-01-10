/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.security.implementation.basic;

import java.util.Map;

/**
 * @javadoc
 * @author Eduard Witteveen
 * @version $Id: LoginModule.java,v 1.5 2008-01-10 14:12:24 michiel Exp $
 */
public interface LoginModule {
    public void load(Map<String, Object> properties);
    public boolean login(NameContext user, Map<String, Object> loginInfo,  Object[] parameters);
}
