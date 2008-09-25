package com.finalist.cmsc.util;

import java.io.*;
import java.util.*;

import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;
import org.w3c.dom.*;

/**
 * Common utilities for handling XML.
 *
 * @author Nico Klasens
 */
public class XmlUtil {

    /** MMbase logging system */
    private static Logger logger = Logging.getLoggerInstance(XmlUtil.class.getName());

    private XmlUtil() {
        // utility class
    }

	/**
	 * Serialize <code>Document</code> instance to pretty printed
	 * <code>String</code>.
	 *
	 * @param doc  Source object.
	 * @return document as <code>String</code>.
	 */
	public static String serializeDocument(Document doc) {
        Properties format = getXmlOutput(false, false, false, false);
		return serializeDocument(doc, format);
	}

    /**
     * Serialize <code>Document</code> instance to pretty printed
     * <code>String</code>.
     * Be carefull, Textnodes will be fomormatted and indented too.
     *
     * @param doc  Source object.
     * @param indent - indent xml
     * @param omitComments - omit tcomments
     * @param omitDocumentType - omit document type
     * @param omitXMLDeclaration - omit xml declaration
     * @return document as <code>String</code>.
     */
    public static String serializeDocument(Document doc, boolean indent, boolean omitComments,
        boolean omitDocumentType, boolean omitXMLDeclaration) {
        Properties format = getXmlOutput(indent, omitComments,
                omitDocumentType, omitXMLDeclaration);

        return serializeDocument(doc, format);
    }


    /**
     * Serialize <code>Document</code> instance to pretty printed
     * <code>String</code>.
     *
     * @param doc  Source object.
     * @param format The OutputFormat to use for the serialization
     * @return document as <code>String</code>.
     */
    private static String serializeDocument(Document doc, Properties format) {
        CharArrayWriter caw = null;
		try {
			caw = new CharArrayWriter();

            TransformerFactory tfactory = TransformerFactory.newInstance();
            Transformer serializer = tfactory.newTransformer();
            serializer.setOutputProperties(format);
            serializer.transform(new DOMSource(doc), new StreamResult(caw));
            caw.flush();

            return caw.toString();
		} catch( Exception e ) {
			logger.error(e.getMessage());
			logger.debug(e);
		} finally {
			if( caw != null ) {
            caw.close();
         }
		}
		return "";
    }

	/**
	 * Serialize <code>DocumentFragment</code> instance to pretty printed
	 * <code>String</code>.
	 *
	 * @param docfrag  Source object.
	 * @return documentfragment as <code>String</code>.
	 */
	public static String serializeDocumentFragment(DocumentFragment docfrag) {
		CharArrayWriter caw =null;
		try {
		    caw = new CharArrayWriter();
            Properties format = getXmlOutput(false, false, true, true);

            TransformerFactory tfactory = TransformerFactory.newInstance();
            Transformer serializer = tfactory.newTransformer();
            serializer.setOutputProperties(format);
            serializer.transform(new DOMSource(docfrag), new StreamResult(caw));
            caw.flush();

			return caw.toString();
		} catch( Exception e ) {
			logger.error( e.getMessage() );
			logger.debug(e);
		} finally {
		   if( caw != null ) {
            caw.close();
         }
		}
		return "";
	}

    /**
	 * Serialize <code>Document</code> instance to pretty printed
	 * <code>String</code>.
	 *
	 * @param element  Source object.
	 * @return Element as <code>String</code>.
	 */
	public static String serializeElement(Element element) {
		return serializeElement(element,false);
	}

	/**
	 * Serialize <code>Document</code> instance to pretty printed
	 * <code>String</code>.
	 *
	 * @param element  Source object.
     * @param omitxml Omit the xml declaration from the returned xml
	 * @return Element as <code>String</code>.
	 */
	public static String serializeElement(Element element, boolean omitxml) {
	    CharArrayWriter caw = null;
		try {
		    caw = new CharArrayWriter();
            Properties format = getXmlOutput(false, false, omitxml, omitxml);

            TransformerFactory tfactory = TransformerFactory.newInstance();
            Transformer serializer = tfactory.newTransformer();
            serializer.setOutputProperties(format);
            serializer.transform(new DOMSource(element), new StreamResult(caw));
            caw.flush();

			return caw.toString();
		} catch( Exception e ) {
			logger.error(e.getMessage());
			logger.debug(e);
		} finally {
			if( caw != null ) {
            caw.close();
         }
		}
		return "";
	}

    /**
     * create Output format for xml
     * Be carefull, Textnodes will be fomormatted and indented too.
     * @param indent - indent xml
     * @param omitComments - omit tcomments
     * @param omitDocumentType - omit document type
     * @param omitXMLDeclaration - omit xml declaration
     * @return output format
     */
    public static Properties getXmlOutput(boolean indent, boolean omitComments,
            boolean omitDocumentType, boolean omitXMLDeclaration) {

        Properties format = new Properties();
        format.put(OutputKeys.METHOD, "xml");
        if (indent) {
            format.put(OutputKeys.INDENT, "yes");
            format.put("{http://xml.apache.org/xslt}indent-amount", "2");
            format.put("{http://xml.apache.org/xslt}line-width", "0");
            format.put("{http://xml.apache.org/xslt}line-separator", "\n");
        }
        else {
            format.put(OutputKeys.INDENT, "no");
        }

        if (omitComments) {
            format.put("{http://xml.apache.org/xslt}omit-comments", "yes");
        }
        else {
            format.put("{http://xml.apache.org/xslt}omit-comments", "no");
        }

        if (omitDocumentType) {
            format.put("{http://xml.apache.org/xslt}omit-document-type", "yes");
        }
        else {
            format.put("{http://xml.apache.org/xslt}omit-document-type", "no");
        }

        if (omitXMLDeclaration) {
            format.put(OutputKeys.OMIT_XML_DECLARATION, "yes");
        }
        else {
            format.put(OutputKeys.OMIT_XML_DECLARATION, "no");
        }

        return format;
    }

    public static Document createDocument() {
        try {
            return getFactory().newDocumentBuilder().newDocument();
        } catch (ParserConfigurationException e) {
            logger.debug("" + e.getMessage(), e);
        } catch (FactoryConfigurationError e) {
            logger.debug("" + e.getMessage(), e);
        }
        return null;
    }

    public static DocumentBuilderFactory getFactory() {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setIgnoringComments(true);
        dbf.setIgnoringElementContentWhitespace(true);
        dbf.setNamespaceAware(false);
        return dbf;
    }

    public static Element createChild(Element root, String elementName) {
        return createChild(root, elementName, null);
    }

    public static Element createChild(Element root, String elementName, String namespaceURI) {
        Element child = createElement(root.getOwnerDocument(), elementName, namespaceURI);
        root.appendChild(child);
        return child;
    }

    public static Element createRoot(Document doc, String elementName) {
        return createRoot(doc, elementName, null);
    }

    public static Element createRoot(Document doc, String elementName, String namespaceURI) {
        Element child = createElement(doc, elementName, namespaceURI);
        doc.appendChild(child);
        return child;
    }

    public static Element createElement(Document doc, String elementName) {
        return createElement(doc, elementName, null);
    }

    public static Element createElement(Document doc, String elementName, String namespaceURI) {
        Element child;
        if (namespaceURI != null) {
            child = doc.createElementNS(namespaceURI, elementName);
        }
        else {
            child = doc.createElement(elementName);
        }
        return child;
    }

    public static void createAttribute(Element element, String name, String value) {
        element.setAttribute(name, value);
    }

    public static void createText(Element root, String text) {
        if (text != null) {
            Text child;
            if (text.indexOf("<")!=-1 || text.indexOf("&")!=-1 ) {
                child = root.getOwnerDocument().createCDATASection(text);
            } else {
                child = root.getOwnerDocument().createTextNode(text);
            }
            root.appendChild(child);
        }
    }

    public static Element createChildText(Element element, String name, String value) {
        Element child = createChild(element, name);
        createText(child, value);
        return child;
    }

    public static void createAttribute(Element root, String name, boolean value) {
        createAttribute(root, name, String.valueOf(value));
    }
    public static void createAttribute(Element root, String name, int value) {
        createAttribute(root, name, String.valueOf(value));
    }
    public static void createAttribute(Element root, String name, float value) {
        createAttribute(root, name, String.valueOf(value));
    }
    public static void createAttribute(Element root, String name, long value) {
        createAttribute(root, name, String.valueOf(value));
    }
    public static void createAttribute(Element root, String name, double value) {
        createAttribute(root, name, String.valueOf(value));
    }

    public static void createText(Element root, boolean text) {
        createText(root, String.valueOf(text));
    }
    public static void createText(Element root, int text) {
        createText(root, String.valueOf(text));
    }
    public static void createText(Element root, float text) {
        createText(root, String.valueOf(text));
    }
    public static void createText(Element root, long text) {
        createText(root, String.valueOf(text));
    }
    public static void createText(Element root, double text) {
        createText(root, String.valueOf(text));
    }

    public static Element createChildText(Element element, String name, boolean value) {
        return createChildText(element, name, String.valueOf(value));
    }
    public static Element createChildText(Element element, String name, int value) {
        return createChildText(element, name, String.valueOf(value));
    }
    public static Element createChildText(Element element, String name, float value) {
        return createChildText(element, name, String.valueOf(value));
    }
    public static Element createChildText(Element element, String name, long value) {
        return createChildText(element, name, String.valueOf(value));
    }
    public static Element createChildText(Element element, String name, double value) {
        return createChildText(element, name, String.valueOf(value));
    }

    public static String getText(Node node) {
        return getText(node, "");
    }

    public static String getText(Node node, String defaultvalue) {
		if (node == null) {
			return defaultvalue;
		}
		try {
            // return the value of the node if that node is itself a text-holding node
            if ((node.getNodeType()==Node.TEXT_NODE)
                    || (node.getNodeType()==Node.CDATA_SECTION_NODE)
                    || (node.getNodeType()==Node.ATTRIBUTE_NODE)) {
                return node.getNodeValue();
            }
            // otherwise return the text contained by the node's children
            Node childnode=node.getFirstChild();
            StringBuffer value = new StringBuffer();
            while (childnode != null) {
                if ((childnode.getNodeType()==Node.TEXT_NODE)
                        || (childnode.getNodeType()==Node.CDATA_SECTION_NODE)) {
                    value.append(childnode.getNodeValue());
                }
                childnode = childnode.getNextSibling();
            }
            if (value.length() > 0) {
               return value.toString();
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return defaultvalue;
    }

    /**
     * Returns a W3C Document representation of the string.
     *
     * @param str Xml which should be converted
     * @return DOM structure
     */
    public static Document toDocument(String str) {
        return toDocument(str, true);
    }

    /**
     * Returns a W3C Document representation of the string.
     *
     * @param str Xml which should be converted
     * @param validate should the xml be validated
     * @return DOM structure
     */
    public static Document toDocument(String str, boolean validate) {
      Document doc = null;
      try {
         doc = toDocument(new ByteArrayInputStream(str.getBytes("UTF-8")), validate);
      } catch (UnsupportedEncodingException e) {
         logger.error("String could not be converted to a Document.", e);
      }
      if (doc == null) {
         logger.error("Erroneous String: " + str);
      }
      return doc;
   }

    /**
     * Returns a W3C Document representation of the stream.
     * @param stream The input stream with the xml to convert
     * @return DOM structure
     */
    public static Document toDocument(InputStream stream) {
        return toDocument(stream, true);
    }

   /**
    * Returns a W3C Document representation of the stream.
    * @param stream The input stream with the xml to convert
     * @param validate should the xml be validated
    * @return DOM structure
    */
   public static Document toDocument(InputStream stream, boolean validate) {
      try {
         DocumentBuilderFactory builderFactory = getFactory();
         builderFactory.setValidating(validate);
         DocumentBuilder docBuilder = builderFactory.newDocumentBuilder();
         Document doc = docBuilder.parse(stream);

         return doc;
      } catch (Exception e) {
         logger.error("InputStream could not be converted to a Document.", e);
      }
      return null;
   }

   public static List<Element> getElements(Element element) {
      List<Element> elements = new ArrayList<Element>();

      NodeList nlist = element.getChildNodes();
      for(int i = 0; i < nlist.getLength(); i++) {
          Node node = nlist.item(i);
          if (node.getNodeType() == Node.ELEMENT_NODE) {
              Element e = (Element) node;
              elements.add(e);
          }
      }
      return elements;
   }

   public static String xmlEscape(String fragment) {
       return fragment.replaceAll("&", "&amp;").replaceAll("<", "&lt;").replaceAll(">", "&gt;");
   }

   public static String xmlUnescape(String escapedFragment) {
       return escapedFragment.replaceAll("&lt;", "<").replaceAll("&gt;", ">").replaceAll("&amp;", "&");
   }

   /**
    * Escapes XML entities.
    *
    * @param xml the input xml
    * @return escaped xml
    */
   public static String escapeXMLEntities(String xml) {
       if (xml == null) {
           throw new IllegalArgumentException("xml string is null");
       }
       return xml.replaceAll("&(?!lt;|gt;)", "&amp;");
   }

   /**
    * Unscapes XML entities.
    *
    * @param xml the input xml
    * @return escaped xml
    */
   public static String unescapeXMLEntities(String xml) {
       if (xml == null) {
           throw new IllegalArgumentException("xml string is null");
       }
      return xml.replaceAll("&amp;(?!lt;|gt;)", "&");
   }


   public static void trimTextNodes(Node in) {
       if (in.getNodeType() == Node.TEXT_NODE) {
          in.setNodeValue(in.getNodeValue().trim());
       }
       else if (in.hasChildNodes()) {
          NodeList nl = in.getChildNodes();
          for (int i = 0; i < nl.getLength(); i++) {
             trimTextNodes(nl.item(i));
          }
       }
    }

   /**
    * Find first (depth first) element with given name, attribute and attribute value.
    *
    * @param node parent node
    * @param tagname element name
    * @param attr attribute name
    * @param value attribute value
    *
    * @return a DOM Element with given name, attribute and value or null if no such node exists
    */
   public static Element getChildWithAttribute(Node node, String tagname, String attr, String value) {
      NodeList nl = node.getChildNodes();
      int len = nl.getLength();

      if (len == 0) {
         return null;
      }

      for (int i = 0; i < len; i++) {
         Node n = nl.item(i);
         if (n instanceof Element && tagname.equals(n.getNodeName())
                && value.equals(((Element) n).getAttribute(attr))) {
            return (Element) n;
         }

         Element e = getChildWithAttribute(n, tagname, attr, value);
         if (e != null) {
            return e;
         }
      }

      return null;
   }

}
