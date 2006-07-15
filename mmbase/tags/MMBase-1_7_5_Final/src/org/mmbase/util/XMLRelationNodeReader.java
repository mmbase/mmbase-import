/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util;

import java.util.Locale;
import java.util.Vector;
import java.io.*;

import org.mmbase.module.core.MMBase;
import org.mmbase.module.core.MMObjectBuilder;
import org.mmbase.module.core.MMObjectNode;
import org.mmbase.module.corebuilders.FieldDefs;
import org.mmbase.module.corebuilders.InsRel;
import org.mmbase.module.corebuilders.RelDef;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class XMLRelationNodeReader extends XMLBasicReader {

   /**
   * Logger routine
   */
   private static Logger log =
      Logging.getLoggerInstance(XMLRelationNodeReader.class.getName());

    String applicationpath;

    /**
     * Constructor
     * @param filename from the file to read from
     * @param applicationpath the path where this application was exported to
     * @param mmbase
     */
    public XMLRelationNodeReader(String filename, String applicationpath, MMBase mmbase) {
        super(filename, false);
        this.applicationpath = applicationpath;
    }

   /**
   * get the name of this application
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
      return (null);
   }

   /**
   * get the name of this application
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
      return -1;
   }

   /**
   */
   public Vector getNodes(MMBase mmbase) {
      Vector nodes = new Vector();
      Node n1 = document.getFirstChild();
      if (n1.getNodeType() == Node.DOCUMENT_TYPE_NODE) {
         n1 = n1.getNextSibling();
      }
      while (n1 != null) {
         MMObjectBuilder bul = mmbase.getMMObject(n1.getNodeName());
         if (bul == null) {
            log.error(
               "Can't get builder with name: '" + n1.getNodeName() + "'");
         }
         else {
            Node n2 = n1.getFirstChild();
            while (n2 != null) {
               if (n2.getNodeName().equals("node")) {
                  NamedNodeMap nm = n2.getAttributes();
                  if (nm != null) {
                     Node n4 = nm.getNamedItem("owner");
                     MMObjectNode newnode = bul.getNewNode(n4.getNodeValue());
                     try {
                        n4 = nm.getNamedItem("number");
                        int num = Integer.parseInt(n4.getNodeValue());
                        newnode.setValue("number", num);

                        n4 = nm.getNamedItem("snumber");
                        int rnum = Integer.parseInt(n4.getNodeValue());
                        newnode.setValue("snumber", rnum);
                        n4 = nm.getNamedItem("dnumber");
                        int dnum = Integer.parseInt(n4.getNodeValue());
                        newnode.setValue("dnumber", dnum);
                        n4 = nm.getNamedItem("rtype");
                        String rname = n4.getNodeValue();
                        RelDef reldef = mmbase.getRelDef();
                        if (reldef == null) {
                           log.error(
                              "XMLRelationReader -> can't get reldef builder");
                           return null;
                        }
                        // figure out rnumber
                        int rnumber = reldef.getNumberByName(rname);
                        newnode.setValue("rnumber", rnumber);

                        // directionality

                        if (InsRel.usesdir) {
                           n4 = nm.getNamedItem("dir");
                           int dir = 0;
                           if (n4 != null) {
                              String dirs = n4.getNodeValue();
                              if ("unidirectional".equals(dirs)) {
                                 dir = 1;
                              }
                              else
                                 if ("bidirectional".equals(dirs)) {
                                    dir = 2;
                                 }
                                 else {
                                    log.error(
                                       "invalid 'dir' attribute encountered in "
                                          + bul.getTableName()
                                          + " value="
                                          + dirs);
                                 }
                           }
                           if (dir == 0) {
                              MMObjectNode relnode = reldef.getNode(rnumber);
                              if (relnode != null) {
                                 dir = relnode.getIntValue("dir");
                              }
                           }
                           if (dir != 1)
                              dir = 2;
                           newnode.setValue("dir", dir);
                        }

                     }
                     catch (Exception e) {
                        log.error(e);
                        log.error(Logging.stackTrace(e));
                     }
                     Node n5 = n2.getFirstChild();
                     while (n5 != null) {
                        String key = n5.getNodeName();
                        if (n5.getNodeType() == Node.ELEMENT_NODE) {
                           Node n6 = n5.getFirstChild();
                           String value = "";
                           if (n6 != null) {
                               value = n6.getNodeValue(); // needs to be a loop
                           }
                           int type = bul.getDBType(key);
                           if (type != -1) {
                                if (type == FieldDefs.TYPE_STRING || type == FieldDefs.TYPE_XML) {
                                    if (value == null)
                                        value = "";
                                    newnode.setValue(key, value);
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
