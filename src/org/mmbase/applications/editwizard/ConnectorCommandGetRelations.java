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

public class ConnectorCommandGetRelations extends ConnectorCommand {

  String objecttype, objectnumber;
  Document cmd;

  public ConnectorCommandGetRelations(String aobjectnumber, NodeList queryrelations) {
    super("getrelations");
    addObject(aobjectnumber, queryrelations);
  }

  public void addObject(String objectnumber) {
        addObject(objectnumber, null);
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
