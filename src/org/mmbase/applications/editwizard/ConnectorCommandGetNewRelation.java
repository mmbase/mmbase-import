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
 * @version $Id: ConnectorCommandGetNewRelation.java,v 1.3 2002-03-15 09:52:37 pierre Exp $
 */

public class ConnectorCommandGetNewRelation extends ConnectorCommand {

    /**
     * @javadoc
     */
     public ConnectorCommandGetNewRelation(String role, String sourceobjectnumber, String destinationobjectnumber) {
         super("getnewrelation");
         addCommandAttr("role", role);
         addCommandAttr("source", sourceobjectnumber);
         addCommandAttr("destination", destinationobjectnumber);
     }

}
