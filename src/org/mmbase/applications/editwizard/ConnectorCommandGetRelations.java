/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.applications.editwizard;

import java.util.Vector;
import org.w3c.dom.*;

/**
 * EditWizard
 * @javadoc
 * @author Kars Veling
 * @since   MMBase-1.6
 * @version $Id: ConnectorCommandGetRelations.java,v 1.3 2002-03-15 09:52:37 pierre Exp $
 */

public class ConnectorCommandGetRelations extends ConnectorCommand {

    public ConnectorCommandGetRelations(String aobjectnumber, NodeList queryrelations) {
        super("getrelations");
        addObject(aobjectnumber, queryrelations);
    }

    public void addObject(String objectnumber, NodeList queryrelations) {
        String nr = objectnumber;
        Document obj = Utils.parseXML("<object number=\""+objectnumber+"\"/>");

        if (queryrelations!=null) {
            Utils.appendNodeList(queryrelations, obj.getDocumentElement());
        }
        addCommandNode(obj.getDocumentElement());
    }
}
