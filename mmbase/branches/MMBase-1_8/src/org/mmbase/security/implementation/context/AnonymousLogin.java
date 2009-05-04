/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.security.implementation.context;

import org.mmbase.security.Rank;
import java.util.Map;

/**
 * The anonymous login module of the context security implementation only creates the 'anonymous'
 * user with the rank {@link Rank#ANONYMOUS}.
 *
 * @author Eduard Witteveen
 * @version $Id$
 */
public class AnonymousLogin extends ContextLoginModule {
    public ContextUserContext login(Map userLoginInfo, Object[] userParameters) throws org.mmbase.security.SecurityException {
        return getValidUserContext("anonymous", Rank.ANONYMOUS);
    }
}
