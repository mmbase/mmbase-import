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
 * @version $Id: ConnectorCommandGetData.java,v 1.2 2002-02-25 11:53:57 pierre Exp $
 */

public class ConnectorCommandGetData extends ConnectorCommand {

  String objecttype, objectnumber;
  Document cmd;

  public ConnectorCommandGetData(String aobjectnumber) {
    super("getconstraints");
    addObject(aobjectnumber, null);
  }

  public ConnectorCommandGetData(String aobjectnumber, NodeList queryfields) {
    super("getdata");
    addObject(aobjectnumber, queryfields);
  }

  public void addObject(String objectnumber, NodeList queryfields) {
    String nr = objectnumber;
    Document obj = Utils.parseXML("<object number=\""+objectnumber+"\"/>");
    if (queryfields!=null) {
        // place extra restrictions
    Utils.appendNodeList(queryfields, obj.getDocumentElement());
    }
    addCommandNode(obj.getDocumentElement());
  }
}
