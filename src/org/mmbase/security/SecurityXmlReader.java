package org.mmbase.security;

import org.apache.xerces.parsers.DOMParser;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NamedNodeMap;

import java.util.StringTokenizer;

/**
 *@depricated Instead should use BasicXMLReader
 */
// still have to add functionality to handle list's of elems..
public class SecurityXmlReader {
  private Document document;
  private String xmlUrl;

  public SecurityXmlReader(String xmlUrl) throws java.io.IOException, NoSuchMethodException {
    this.xmlUrl = xmlUrl;
    try {
      DOMParser parser = new DOMParser();
      parser.setFeature("http://apache.org/xml/features/dom/defer-node-expansion",   true);
      parser.setFeature("http://apache.org/xml/features/continue-after-fatal-error", true);
      parser.parse(xmlUrl);
      document = parser.getDocument();
    }
    catch(org.xml.sax.SAXException se) {
      throw new NoSuchMethodException(se.toString());
    }
  }

  // returns null when not there...
  public String getValue(String path) throws java.util.NoSuchElementException {
    return getValue(resolve(path));
  }

  public String getValue(Node entry) throws java.util.NoSuchElementException {
    Node childNode = entry.getFirstChild();
    // skip comments?
    if(childNode == null) {
      throw new java.lang.Error("did not contain a body");
    }
    String value = childNode.getNodeValue();
    return value.trim();
  }

  // returns null when not there...
  public String getAttribute(String path, String key) throws java.util.NoSuchElementException {
    return getAttribute(resolve(path), key);
  }

  // returns null when not there...
  public String getAttribute(Node entry, String key) throws java.util.NoSuchElementException  {
    NamedNodeMap nm = entry.getAttributes();
    if(nm  == null ) {
      // no attributes.....
      return null;
    }
    Node nameNode =nm.getNamedItem(key);
    if(nameNode  == null ) {
      //attribute not found...
      return null;
    }
    return nameNode.getNodeValue();
  }

  public Node resolve(String path) throws java.util.NoSuchElementException {
    if( path.length() < 1 || path.charAt(0) != '/') {
      throw new java.util.NoSuchElementException("path must start with a '/'");
    }
    return resolve(path.substring(1), document);
  }

  //cannot handle url's that start with a '/'
  public Node resolve(String path, Node beginNode) throws java.util.NoSuchElementException {
    StringTokenizer tokenizer = new StringTokenizer(path,"/");
    Node currentNode = beginNode; //document.getFirstChild();
    while(tokenizer.hasMoreTokens()) {
      // lookup the current
      Node lookUpNode = currentNode.getFirstChild();
      String lookUpName = tokenizer.nextToken();
      // System.out.println("looking for:" + lookUpName);
      if(lookUpName.equals("")) throw new java.util.NoSuchElementException("Probebly 2 '/'-es or an absolute url");
      while (lookUpNode != null && !lookUpNode.getNodeName().equals(lookUpName)) {
        //System.out.println("\tfound:" + lookUpNode.getNodeName());
        lookUpNode = lookUpNode.getNextSibling();
      }
      if(lookUpNode == null) {
        throw new java.util.NoSuchElementException("Could not find the key '"+
          path + "' from location '" + beginNode.getNamespaceURI() +
          "'inside configfile '" + xmlUrl +"'.");
      }
      // k we found him.... this one will take us deeper, when more strings
      // from tokenizer... otherwise it will be the return value..
      currentNode = lookUpNode;
    }
    return currentNode;
  }
}
