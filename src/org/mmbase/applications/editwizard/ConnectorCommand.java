/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.applications.editwizard;

import org.w3c.dom.*;
import java.util.Date;

/**
 * EditWizard
 * @javadoc
 * @author Kars Veling
 * @since   MMBase-1.6
 * @version $Id: ConnectorCommand.java,v 1.2 2002-02-25 11:53:57 pierre Exp $
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
