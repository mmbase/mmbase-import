/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.security.implementation.cloudcontext.builders;

import org.mmbase.security.implementation.cloudcontext.*;
import org.mmbase.module.core.*;
import org.mmbase.module.corebuilders.*;

/**
 * Simple extension of Contexts. It overrides the concept of an 'own' node wich is on default only the mmbaseusers object.
 * This adds also the people nodes where the account field is the current account.
 *
 *
 * @author Michiel Meeuwissen
 * @version $Id: PeopleContexts.java,v 1.3 2004-07-30 17:11:35 michiel Exp $
 */
public class PeopleContexts extends Contexts {



    protected String peopleBuilder = "people";
    public boolean init() {
        String s = (String) getInitParameters().get("peoplebuilder");
        if (s != null) {
            peopleBuilder = s;
        }
        return super.init();
    }

    // javadoc inherited
    protected boolean isOwnNode(User user, MMObjectNode node) {       
        if (super.isOwnNode(user, node)) return true;
        if (node.getBuilder().getTableName().equals(peopleBuilder)) {
            FieldDefs  field = node.getBuilder().getField("account");
            if (field != null) {
                switch (field.getDBType()) {
                case FieldDefs.TYPE_STRING:
                    if (node.getStringValue("account").equals(user.getIdentifier())) {
                        return true;
                    }
                    break;
                case FieldDefs.TYPE_NODE:
                    if (node.getIntValue("account") == user.getNode().getNumber()) {
                        return true;
                    }
                    break;
                }                
            }
        }
        return false;
    }


}
