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
 * @version $Id: ConnectorCommandGetNew.java,v 1.2 2002-02-25 11:53:57 pierre Exp $
 */

public class ConnectorCommandGetNew extends ConnectorCommand {

  String objecttype;

  public ConnectorCommandGetNew(String aobjecttype) {
    super("getnew");
    objecttype = aobjecttype;
    addCommandAttr("type", objecttype);
  }

}
