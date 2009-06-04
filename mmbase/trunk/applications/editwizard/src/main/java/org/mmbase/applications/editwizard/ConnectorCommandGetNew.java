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

public class ConnectorCommandGetNew extends ConnectorCommand {

    /**
     * @javadoc
     */
     public ConnectorCommandGetNew(String objecttype) throws WizardException {
         super(Dove.GETNEW);
         addCommandAttr(Dove.ELM_TYPE, objecttype);
     }
}
