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
 * @version $Id: ConnectorCommandGetNewRelation.java,v 1.5 2002-10-31 08:23:19 pierre Exp $
 */

public class ConnectorCommandGetNewRelation extends ConnectorCommand {

    /**
     * Constructs a command to craete a new temporarily relation.
     *
     * @param role                    The name of the role the new relation should have.
     * @param sourceObjectNumber      the number of the sourceobject
     * @param sourceType              the type of the sourceobject
     * @param destinationObjectNumber the number of the destination object
     * @param destinationType         the type of the destination object
     */
     public ConnectorCommandGetNewRelation(String role, String sourceObjectNumber, String sourceType,
                                           String destinationObjectNumber, String destinationType) throws WizardException {
         super("getnewrelation");
         addCommandAttr("role", role);
         addCommandAttr("source", sourceObjectNumber);
         addCommandAttr("sourcetype", sourceType);
         addCommandAttr("destination", destinationObjectNumber);
         addCommandAttr("destinationtype", destinationType);
     }

}
