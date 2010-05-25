
/*

  This software is OSI Certified Open Source Software.
  OSI Certified is a certification mark of the Open Source Initiative.

  The license (Mozilla version 1.0) can be read at the MMBase site.
  See http://www.MMBase.org/license

*/
package org.mmbase.util.xml.applicationdata;

import java.io.*;
import java.util.*;

import org.mmbase.util.*;
import org.mmbase.bridge.Field;
import org.mmbase.module.core.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;
import org.mmbase.util.xml.DocumentReader;
import org.w3c.dom.*;
import org.xml.sax.InputSource;

/**
 * This class reads a node from an exported application.
 * @application Applications
 * @author Daniel Ockeloen
 * @author Michiel Meeuwissen
 * @version $Id$
 */
public class NodeReader extends DocumentReader {
    private static final Logger log = Logging.getLoggerInstance(NodeReader.class);

    private ResourceLoader path;

    public boolean loadBinaries = true;

    /**
     * @since MMBase-1.8
     */
    public NodeReader(InputSource is, ResourceLoader path) {
        super(is, false);
        if (path == null) throw new NullPointerException();
        this.path = path;
    }

    /**
     * get the name of this application
     */
    public String getExportSource() {
        Node n1 = document.getDocumentElement();
        if (n1 != null) {
            NamedNodeMap nm = n1.getAttributes();
            if (nm != null) {
                Node n2 = nm.getNamedItem("exportsource");
                return (n2.getNodeValue());
            }
        } else {
            log.warn("exportsource attribute missing");
        }
        return null;
    }

    /**
     * get the name of this application
     */
    public int getTimeStamp() {
        Node n1 = document.getFirstChild();
        if (n1.getNodeType() == Node.DOCUMENT_TYPE_NODE) {
            n1 = n1.getNextSibling();
        }
        if (n1 != null) {
            NamedNodeMap nm = n1.getAttributes();
            if (nm != null) {
                Node n2 = nm.getNamedItem("timestamp");
                try {
                    java.text.SimpleDateFormat formatter =
                        new java.text.SimpleDateFormat("yyyyMMddhhmmss", Locale.US);
                    int times =
                        (int) (formatter.parse(n2.getNodeValue()).getTime() / 1000);
                    //int times=DateSupport.parsedatetime(n2.getNodeValue());
                    return times;
                }
                catch (java.text.ParseException e) {
                    return -1;
                }
            }
        }
        else {
            log.warn("timestamp attribute missing");
        }
        return -1;
    }

    public List<MMObjectNode> getNodes(MMBase mmbase) {
        List<MMObjectNode> nodes = new ArrayList<MMObjectNode>();
        Node n1 = document.getDocumentElement();
        while (n1 != null) {
            MMObjectBuilder bul = mmbase.getMMObject(n1.getNodeName());
            if (bul == null) {
                log.error("Can't get builder with name: '" + n1.getNodeName() + "'");
            } else {
                Node n2 = n1.getFirstChild();
                while (n2 != null) {
                    if (n2.getNodeName().equals("node")) {
                        NamedNodeMap nm = n2.getAttributes();
                        if (nm != null) {
                            Node n4 = nm.getNamedItem("owner");
                            MMObjectNode newNode = null;
                            if (n4 != null) {
                                newNode = bul.getNewNode(n4.getNodeValue());
                            } else {
                                newNode = bul.getNewNode("import");
                            }
                            n4 = nm.getNamedItem("alias");
                            if (n4 != null) {
                                // tokenize here!
                                String n4value = n4.getNodeValue();
                                String[] aliases = n4value.split(",");
                                for (String alias : aliases) {
                                    log.info("Setting alias to " + alias);
                                    newNode.setAlias(alias);
                                }
                            }
                            n4 = nm.getNamedItem("number");
                            try {
                                int num = Integer.parseInt(n4.getNodeValue());
                                newNode.setValue("number", num);
                            } catch (Exception e) {}
                            Node n5 = n2.getFirstChild();
                            while (n5 != null) {
                                if (n5.getNodeType() == Node.ELEMENT_NODE) {
                                    String key = n5.getNodeName();
                                    NodeList nl = n5.getChildNodes();
                                    StringBuilder res = new StringBuilder("");
                                    for (int i = 0; i < nl.getLength(); i++) {
                                        Node n = nl.item(i);
                                        if ((n.getNodeType() == Node.TEXT_NODE)
                                            || (n.getNodeType() == Node.CDATA_SECTION_NODE)) {
                                            res.append(n.getNodeValue().trim());
                                        }
                                    }
                                    String value = res.toString();

                                    setValue(bul, newNode, n5, key, value);
                                }
                                n5 = n5.getNextSibling();
                            }
                            nodes.add(newNode);
                        }
                    }
                    n2 = n2.getNextSibling();
                }
            }
            n1 = n1.getNextSibling();
        }
        return nodes;
    }

    protected void setValue(MMObjectBuilder bul, MMObjectNode newNode, Node n5, String key, String value) {
        int type = bul.getDBType(key);
        if (type != -1) {
            if ("".equals(value) && ! bul.getField(key).isRequired()) {
                value = null;
            }
            if (value == null &&
                type != Field.TYPE_BINARY // binaries are handled specially
                ) {
                newNode.setValue(key, null);
                return;
            }
            if (type == Field.TYPE_STRING || type == Field.TYPE_XML) {
                newNode.setValue(key, value);
            } else if (type == Field.TYPE_NODE) {
                // do not really set it, because we need syncnodes later for this.
                newNode.storeValue("__" + key, value); // yes, this is hackery, I'm sorry.
                newNode.setValue(key, null);
            } else if (type == Field.TYPE_INTEGER) {
                try {
                    newNode.setValue(key, Integer.parseInt(value));
                } catch (Exception e) {
                    log.warn("error setting integer-field '" + key + "' to '" + value + "' because " + e);
                    newNode.setValue(key, -1);
                }
            } else if (type == Field.TYPE_FLOAT) {
                try {
                    newNode.setValue(key, Float.parseFloat(value));
                } catch (Exception e) {
                    log.warn("error setting float-field '" + key + "' to '" + value + "' because " + e);
                    newNode.setValue(key, -1);
                }
            } else if (type == Field.TYPE_DOUBLE) {
                try {
                    newNode.setValue(key, Double.parseDouble(value));
                } catch (Exception e) {
                    log.warn("error setting double-field '" + key + "' to '" + value + "' because " + e);
                    newNode.setValue(key, -1);
                }
            } else if (type == Field.TYPE_LONG) {
                try {
                    newNode.setValue(key, Long.parseLong(value));
                } catch (Exception e) {
                    log.warn("error setting long-field '" + key + "' to '" + value + "' because " + e);
                    newNode.setValue(key, -1);
                }
            } else if (type == Field.TYPE_DATETIME) {
                newNode.setValue(key, Casting.toDate(value));
            } else if (type == Field.TYPE_BOOLEAN) {
                newNode.setValue(key, Casting.toBoolean(value));
            } else if (type == Field.TYPE_BINARY) {
                NamedNodeMap nm2 = n5.getAttributes();
                Node n7 = nm2.getNamedItem("file");
                try {
                    if(loadBinaries) {
                        newNode.setValue(key, readBytesStream(n7.getNodeValue()));
                    }
                    else{
                        newNode.setValue(key, n7.getNodeValue());
                    }
                } catch (IOException ioe) {
                    log.warn("Could not set field " + key + " " + ioe);
                }
            } else {
                log.error("CoreField not found for #" + type + " was not known for field with name: '"
                          + key + "' and with value: '" + value + "'");
            }
        }
    }

    private byte[] readBytesStream(String resourceName) throws IOException {
        InputStream stream = path.getResourceAsStream(resourceName);
        if (stream == null) {
            log.error("The resource '" + resourceName + "' could not be found");
            return null;
        } else {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            IOUtil.copy(stream, buffer);
            return buffer.toByteArray();
        }
    }

    public void loadBinaryFields(MMObjectNode newNode) {
        Set<String> fieldNames = newNode.getBuilder().getFieldNames();
        if(fieldNames!=null && fieldNames.size()>0){
            for (String fieldName : fieldNames) {
                int fieldDBType = newNode.getBuilder().getDBType(fieldName);
                if(fieldDBType == Field.TYPE_BINARY){
                    try {
                        String resource = newNode.getStringValue(fieldName);
                        newNode.setValue(fieldName, readBytesStream(resource));
                    } catch (Exception setValueEx){
                        log.error(setValueEx);
                    }
                }
            }
        }
    }

    public void setLoadBinaries(boolean loadBinaries) {
        this.loadBinaries = loadBinaries;
    }
}
