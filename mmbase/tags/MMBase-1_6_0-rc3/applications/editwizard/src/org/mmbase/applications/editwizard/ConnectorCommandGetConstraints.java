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
 * @version $Id: ConnectorCommandGetConstraints.java,v 1.4 2002-03-18 17:13:06 eduard Exp $
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
