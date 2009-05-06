/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.security.implementation.cloudcontext.builders;

import org.mmbase.security.implementation.cloudcontext.*;
import org.mmbase.module.core.*;
import org.mmbase.bridge.Field;
import org.mmbase.core.CoreField;

/**
 * Simple extension of Contexts. It overrides the concept of an 'own' node wich is on default only
 * the mmbaseusers object.  This adds also the people nodes where the 'account; field is the current
 * account (either as a String field containing the user's identifier), or as a NODE field refering
 * to the mmbaseusers node.
 *
 * @author Michiel Meeuwissen
 * @version $Id: PeopleContexts.java,v 1.9 2009-04-28 08:35:09 michiel Exp $
 */
public class PeopleContexts extends Contexts {



    protected String peopleBuilder = "people";
    @Override
    public boolean init() {
        String s = getInitParameters().get("peoplebuilder");
        if (s != null) {
            peopleBuilder = s;
        }
        return super.init();
    }

    // javadoc inherited
    protected boolean isOwnNode(User user, MMObjectNode node) {       
        if (super.isOwnNode(user, node)) return true;
        if (node.getBuilder().getTableName().equals(peopleBuilder)) {
            CoreField field = node.getBuilder().getField("account");
            if (field != null) {
                switch (field.getType()) {
                case Field.TYPE_STRING:
                    if (node.getStringValue("account").equals(user.getIdentifier())) {
                        return true;
                    }
                    break;
                case Field.TYPE_NODE:
                    if (node.getIntValue("account") == user.getNode().getNumber()) {
                        return true;
                    }
                    break;
                default:
                    break;
                }
            }
        }
        return false;
    }


}
