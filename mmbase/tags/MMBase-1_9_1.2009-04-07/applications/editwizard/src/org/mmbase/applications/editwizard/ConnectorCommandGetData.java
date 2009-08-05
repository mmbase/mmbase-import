/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.applications.editwizard;

import org.mmbase.applications.dove.Dove;
import org.w3c.dom.*;

/**
 * EditWizard
 * @javadoc
 * @author Kars Veling
 * @since   MMBase-1.6
 * @version $Id: ConnectorCommandGetData.java,v 1.7 2007-06-13 20:54:26 nklasens Exp $
 */

public class ConnectorCommandGetData extends ConnectorCommand {

    /**
     * @javadoc
     */
    public ConnectorCommandGetData(String aobjectnumber, NodeList queryfields) throws WizardException {
        super(Dove.GETDATA);
        addObject(aobjectnumber, queryfields);
    }

    /**
     * @javadoc
     */
    private void addObject(String objectnumber, NodeList queryfields) throws WizardException {
        Document obj = Utils.parseXML("<object number=\""+objectnumber+"\"/>");
        if (queryfields!=null) {
            // place extra restrictions
            Utils.appendNodeList(queryfields, obj.getDocumentElement());
        }
        addCommandNode(obj.getDocumentElement());
    }
}
