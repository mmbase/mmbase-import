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
 * @version $Id$
 */

public class ConnectorCommandGetRelations extends ConnectorCommand {

    public ConnectorCommandGetRelations(String aobjectnumber, NodeList queryrelations) throws WizardException {
        super(Dove.GETRELATIONS);
        addObject(aobjectnumber, queryrelations);
    }

    public void addObject(String objectnumber, NodeList queryrelations) throws WizardException {
        Document obj = Utils.parseXML("<object number=\""+objectnumber+"\"/>");

        if (queryrelations!=null) {
            Utils.appendNodeList(queryrelations, obj.getDocumentElement());
        }
        addCommandNode(obj.getDocumentElement());
    }
}
