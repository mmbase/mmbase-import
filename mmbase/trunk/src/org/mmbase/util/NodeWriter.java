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

import org.mmbase.module.corebuilders.*;

/**
 *
 * @author Daniel Ockeleon
 * @author Jaco de Groot
 */
public class NodeWriter{
    private static final boolean DEBUG = true;
    private MMBase mmb;
    private Vector resultsmsgs;
    private String directory;
    private String builderName;
    private boolean isRelationNode;
    private File file;
    private FileWriter fw;
    private int nrOfNodes;

    /**
     * @param directory  the directory to write the files to (including the
     *                   trailing slash).
     */ 
    NodeWriter(MMBase mmb, Vector resultsmsgs, String directory,
               String builderName, boolean isRelationNode) {
        this.mmb = mmb;
        this.resultsmsgs = resultsmsgs;
        this.directory = directory;
        this.builderName = builderName;
        this.isRelationNode = isRelationNode;
        file = new File(directory + builderName + ".xml");
        try {
            if (DEBUG) System.out.println("Opening " + file + " for writing.");
            fw = new FileWriter(file);
        } catch (Exception e) {
            resultsmsgs.addElement("Failed opening file " + file);
        }
        // Write the header
        write("<" + builderName + " "
              + "exportsource=\"mmbase://127.0.0.1/install/b1\" "
              + "timestamp=\"20000602143030\">\n");
        nrOfNodes = 0;
    }

    public void write(MMObjectNode node) {
        int number=node.getIntValue("number");
        String owner=node.getStringValue("owner");
        // start the node
        if (isRelationNode) {
            String rtype = builderName;
            if ("insrel".equals(builderName)) {
                rtype = "related";
            }
            write("\t<node number=\""+number+"\" owner=\""+owner+"\" snumber=\""+ node.getIntValue("snumber") +"\" dnumber=\""+ node.getIntValue("dnumber") +"\" rtype=\""+ rtype +"\">\n");
        } else {
            String tm=mmb.OAlias.getAlias(number);
            if (tm==null) {
                write("\t<node number=\""+number+"\" owner=\""+owner+"\">\n");
            } else {
                write("\t<node number=\""+number+"\" owner=\""+owner+"\" alias=\""+tm+"\">\n");
            }
        }
        // write the values of the node
        Hashtable values=node.getValues();	
        Enumeration nd=values.keys();
        while (nd.hasMoreElements()) {
            String key=(String)nd.nextElement();
            if (isRelationNode) {
                if (!key.equals("number") && !key.equals("owner")
                        && !key.equals("otype") && !key.equals("CacheCount")
                        && !key.equals("snumber") && !key.equals("dnumber")
                        && !key.equals("rnumber")) {
                    write("\t\t<"+key+">"+node.getValue(key)+"</"+key+">\n");
                }
            } else {
                write(writeXMLField(key, node, directory, mmb));
            }
        }
        // end the node
        write("\t</node>\n\n");
        nrOfNodes++;
    }
    
    public void done() {
        // write the footer	
        write("</"+ builderName + ">\n");
        resultsmsgs.addElement("Saving " + nrOfNodes + " " + builderName
                               + " to : " + file);
        try {
            if (DEBUG) System.out.println("Closing file " + file);
            fw.close();
        } catch (Exception e) {
            resultsmsgs.addElement("Failed closing file " + file);
        }
    }

    private void write(String s) {
        try {
            fw.write(s);
        } catch (Exception e) {
            resultsmsgs.addElement("Failed writing to file " + file);
        }
    }

    private static String writeXMLField(String key,MMObjectNode node, String targetpath,MMBase mmb) {
	if (!key.equals("number") && !key.equals("owner") && !key.equals("otype") && !key.equals("CacheCount")) {
		// this is a bad way of doing it imho
		int type=node.getDBType(key);
		String stype=mmb.getTypeDef().getValue(node.getIntValue("otype"));	
		if (type==FieldDefs.TYPE_BYTE) {
			String body="\t\t<"+key+" file=\""+stype+"/"+node.getIntValue("number")+"."+key+"\" />\n";
			File file = new File(targetpath+stype);
			try {
				file.mkdirs();
			} catch(Exception e) {
				System.out.println("Can't create dir : "+targetpath+stype);
			}
			byte[] value=node.getByteValue(key);
			saveFile(targetpath+stype+"/"+node.getIntValue("number")+"."+key,value);
			return(body);
		} else {
			String body="\t\t<"+key+">"+node.getValue(key)+"</"+key+">\n";
			return(body);
		}

	}
	return("");
    }

	static boolean saveFile(String filename,byte[] value) {
		File sfile = new File(filename);
		try {
			DataOutputStream scan = new DataOutputStream(new FileOutputStream(sfile));
			scan.write(value);
			scan.flush();
			scan.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
		return(true);
	}

}
