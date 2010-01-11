/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.applications.editwizard;

import org.mmbase.applications.dove.Dove;

/**
 * EditWizard
 * @javadoc
 * @author Kars Veling
 * @since   MMBase-1.6
 * @version $Id$
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
                                           String destinationObjectNumber, String destinationType, String createDir) throws WizardException {
         super(Dove.GETNEWRELATION);
         addCommandAttr(Dove.ELM_ROLE,            role);
         addCommandAttr(Dove.ELM_SOURCE,          sourceObjectNumber);
         addCommandAttr(Dove.ELM_SOURCETYPE,      sourceType);
         addCommandAttr(Dove.ELM_DESTINATION,     destinationObjectNumber);
         addCommandAttr(Dove.ELM_DESTINATIONTYPE, destinationType);
         addCommandAttr(Dove.ELM_CREATEDIR,       createDir);
     }

}
