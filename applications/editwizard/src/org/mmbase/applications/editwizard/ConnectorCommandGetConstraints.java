/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.applications.editwizard;

/**
 * EditWizard
 * @javadoc
 * @author Kars Veling
 * @since   MMBase-1.6
 * @version $Id: ConnectorCommandGetConstraints.java,v 1.5 2003-03-04 13:27:08 nico Exp $
 */

public class ConnectorCommandGetConstraints extends ConnectorCommand {

    /**
     * @javadoc
     */
    public ConnectorCommandGetConstraints(String objecttype) throws WizardException {
        super("getconstraints");
        addCommandAttr("type",objecttype);
    }


}
