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

public class ConnectorCommandGetList extends ConnectorCommand {

  String objecttype, objectnumber;
  Document cmd;

  public ConnectorCommandGetList(String objecttype) {
    super("getlist");
    addQuery(objecttype);
  }

  public ConnectorCommandGetList(Node query) {
    super("getlist");
    addQuery(query);
  }

  public void addQuery(Node query) {
    addCommandNode(query.cloneNode(true));
  }

  public void addQuery(String xpath) {
    String nr = objectnumber;
    Document obj = Utils.parseXML("<query xpath=\"/*@" + xpath + "\"/>");
    addCommandNode(obj.getDocumentElement());
  }
}
