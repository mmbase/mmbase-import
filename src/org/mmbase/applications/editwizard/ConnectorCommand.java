package org.mmbase.applications.editwizard;

import org.w3c.dom.*;
import java.util.Date;

/**
 * Title:        VPRO EditWizard
 * Description:
 * Copyright:    Copyright (c) 1999
 * Company:      Q42
 * @author Kars Veling
 * @version
 */

public class ConnectorCommand {
  String id;
  String name;
  Document xml;
  Document responsexml;

  public ConnectorCommand(String aname) {
    name = aname;
    id = new Date().getTime()+"";
    xml = Utils.parseXML("<"+name+" id=\"" + id + "\"/>");
    responsexml = Utils.parseXML("<response/>");
  }

  public void addCommandNode(Node node) {
    Node newnode = xml.getDocumentElement().appendChild(xml.importNode(node.cloneNode(true), true));
  }

  public void addCommandAttr(String name, String value) {
    Utils.setAttribute(xml.getDocumentElement(), name, value);
  }

  public Document getCommandXML() {
    return xml;
  }

  public void setResponseXML(Node responsenode) {
    responsexml = Utils.EmptyDocument();
    responsexml.appendChild(responsexml.importNode(responsenode.cloneNode(true), true));
  }

  public boolean hasError() {
    return false;
  }

}
