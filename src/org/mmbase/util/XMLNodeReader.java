/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util;

import java.io.*;
import java.util.*;

import org.mmbase.module.core.*;
import org.mmbase.module.corebuilders.FieldDefs;
import org.mmbase.util.logging.*;
import org.w3c.dom.*;

/**
 * This class reads a node from an exported application
 * @version $Id: XMLNodeReader.java,v 1.26 2004-01-08 23:49:58 michiel Exp $
 * @author ?
 * @author Michiel Meeuwissen
 */
public class XMLNodeReader extends XMLBasicReader {
    private static Logger log = Logging.getLoggerInstance(XMLNodeReader.class.getName());
    String applicationpath;

    /**
     * Constructor
     * @param filename from the file to read from
     * @param applicationpath the path where this application was exported to
     * @param mmbase
     */
    public XMLNodeReader(String filename, String applicationpath, MMBase mmbase) {
        super(filename, false);
        this.applicationpath = applicationpath;
    }

    /**
     *
     */
    public String getExportSource() {
        Node n1 = document.getFirstChild();
        if (n1.getNodeType() == Node.DOCUMENT_TYPE_NODE) {
            n1 = n1.getNextSibling();
        }
        while (n1 != null) {
            NamedNodeMap nm = n1.getAttributes();
            if (nm != null) {
                Node n2 = nm.getNamedItem("exportsource");
                return (n2.getNodeValue());
            }
        }
        return null;
    }

    /**
     *
     */
    public int getTimeStamp() {
        Node n1 = document.getFirstChild();
        if (n1.getNodeType() == Node.DOCUMENT_TYPE_NODE) {
            n1 = n1.getNextSibling();
        }
        while (n1 != null) {
            NamedNodeMap nm = n1.getAttributes();
            if (nm != null) {
                Node n2 = nm.getNamedItem("timestamp");
                try {
                    java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("yyyyMMddhhmmss", Locale.US);
                    int times = (int) (formatter.parse(n2.getNodeValue()).getTime() / 1000);
                    //int times=DateSupport.parsedatetime(n2.getNodeValue());
                    return times;
                } catch (java.text.ParseException e) {
                    log.warn("error retrieving timestamp: " + Logging.stackTrace(e));
                    return -1;
                }

            }
        }
        return -1;
    }

    /**
     *
     */
    public Vector getNodes(MMBase mmbase) {
        Vector nodes = new Vector();
        Node n1 = document.getFirstChild();
        if (n1.getNodeType() == Node.DOCUMENT_TYPE_NODE) {
            n1 = n1.getNextSibling();
        }
        while (n1 != null) {
            MMObjectBuilder bul = mmbase.getMMObject(n1.getNodeName());
            if (bul != null) {
                Node n2 = n1.getFirstChild();
                while (n2 != null) {
                    if (n2.getNodeName().equals("node")) {
                        NamedNodeMap nm = n2.getAttributes();
                        if (nm != null) {
                            Node n4 = nm.getNamedItem("owner");
                            MMObjectNode newnode = null;
                            if (n4 != null) {
                                newnode = bul.getNewNode(n4.getNodeValue());
                            } else {
                                newnode = bul.getNewNode("import");
                            }
                            n4 = nm.getNamedItem("alias");
                            if (n4 != null)
                                newnode.setAlias(n4.getNodeValue());
                            n4 = nm.getNamedItem("number");
                            try {
                                int num = Integer.parseInt(n4.getNodeValue());

                                newnode.setValue("number", num);
                            } catch (Exception e) {}
                            Node n5 = n2.getFirstChild();
                            while (n5 != null) {
                                if (n5.getNodeType() == Node.ELEMENT_NODE) {
                                    String key = n5.getNodeName();
                                    NodeList nl = n5.getChildNodes();
                                    StringBuffer res = new StringBuffer("");
                                    for (int i = 0; i < nl.getLength(); i++) {
                                        Node n = nl.item(i);
                                        if ((n.getNodeType() == Node.TEXT_NODE)
                                            || (n.getNodeType() == Node.CDATA_SECTION_NODE)) {
                                            res.append(n.getNodeValue().trim());
                                        }
                                    }
                                    String value = res.toString();

                                    int type = bul.getDBType(key);
                                    if (type != -1) {
                                        if (type == FieldDefs.TYPE_STRING || type == FieldDefs.TYPE_XML) {
                                            if (value == null) {
                                                value = ""; 
                                            }
                                            newnode.setValue(key, value);
                                            if (log.isDebugEnabled()) {
                                                log.debug("After value " + Casting.toString(newnode.getValue(key)));
                                            }
                                        } else if (type == FieldDefs.TYPE_NODE) {                                            
                                            // do not really set it, because we need syncnodes later for this.
                                            newnode.values.put("__" + key, value); // yes, this is hackery, I'm sorry.
                                            newnode.setValue(key, MMObjectNode.VALUE_NULL);
                                        } else if (type == FieldDefs.TYPE_INTEGER) {
                                           try {
                                                newnode.setValue(key, Integer.parseInt(value));
                                            } catch (Exception e) {
                                                log.warn("error setting integer-field " + e);
                                                newnode.setValue(key, -1);
                                            }
                                        } else if (type == FieldDefs.TYPE_FLOAT) {
                                            try {
                                                newnode.setValue(key, Float.parseFloat(value));
                                            } catch (Exception e) {
                                                log.warn("error setting float-field " + e);
                                                newnode.setValue(key, -1);
                                            }
                                        } else if (type == FieldDefs.TYPE_DOUBLE) {
                                            try {
                                                newnode.setValue(key, Double.parseDouble(value));
                                            } catch (Exception e) {
                                                log.warn("error setting double-field " + e);
                                                newnode.setValue(key, -1);
                                            }
                                        } else if (type == FieldDefs.TYPE_LONG) {
                                            try {
                                                newnode.setValue(key, Long.parseLong(value));
                                            } catch (Exception e) {
                                                log.warn("error setting long-field " + e);
                                                newnode.setValue(key, -1);
                                            }
                                        } else if (type == FieldDefs.TYPE_BYTE) {
                                            NamedNodeMap nm2 = n5.getAttributes();
                                            Node n7 = nm2.getNamedItem("file");
                                            newnode.setValue(key, readBytesFile(applicationpath + n7.getNodeValue()));
                                        } else {
                                            log.error("FieldDefs not found for #" + type + " was not known for field with name: '"
                                                      + key + "' and with value: '" + value + "'");
                                        }
                                    }
                                }
                                n5 = n5.getNextSibling();
                            }
                            nodes.addElement(newnode);
                        }
                    }
                    n2 = n2.getNextSibling();
                }
            } else {
                log.error("Could not find builder with name: " + n1.getNodeName() + "'");
            }
            n1 = n1.getNextSibling();
        }
        return nodes;
    }

    byte[] readBytesFile(String filename) {
        File bfile = new File(filename);
        int filesize = (int)bfile.length();
        byte[] buffer = new byte[filesize];
        try {
            FileInputStream scan = new FileInputStream(bfile);
            int len = scan.read(buffer, 0, filesize);
            scan.close();
        } catch (FileNotFoundException e) {
            log.error("error getfile : " + filename + " " + Logging.stackTrace(e));
        } catch (IOException e) {
            log.error("error getfile : " + filename + " " + Logging.stackTrace(e));
        }
        return (buffer);
    }
}
