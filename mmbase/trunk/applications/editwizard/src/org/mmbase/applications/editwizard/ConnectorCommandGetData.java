package org.mmbase.applications.editwizard;

import java.util.Vector;
import org.w3c.dom.*;

/**
 * Title:        VPRO EditWizard
 * Description:
 * Copyright:    Copyright (c) 1999
 * Company:      Q42
 * @author Kars Veling
 * @version
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
