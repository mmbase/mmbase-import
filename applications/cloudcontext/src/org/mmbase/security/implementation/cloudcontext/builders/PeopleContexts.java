/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.security.implementation.cloudcontext.builders;

import org.mmbase.security.implementation.cloudcontext.*;
import org.mmbase.module.core.*;

/**
 * Simple extension of Contexts. It overrides the concept of an 'own' node wich is on default only the mmbaseusers object.
 * This adds also the people nodes where the account field is the current account.
 *
 *
 * @author Michiel Meeuwissen
 * @version $Id: PeopleContexts.java,v 1.1 2003-11-03 13:22:31 michiel Exp $
 */
public class PeopleContexts extends Contexts {


    // javadoc inherited
    protected boolean isOwnNode(User user, MMObjectNode node) {       
        if (super.isOwnNode(user, node)) return true;
        if (node.getBuilder().getTableName().equals("people")) {
            if (node.getStringValue("account").equals(user.getIdentifier())) {
                return true;
            }
        }
        return false;
    }


}
