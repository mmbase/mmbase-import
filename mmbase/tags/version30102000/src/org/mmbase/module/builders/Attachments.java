/*
 
This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.
 
The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license
 
*/
/*
 
  $Id: Attachments.java,v 1.4 2000-08-18 21:37:17 case Exp $
 
  $Log: not supported by cvs2svn $
  Revision 1.3  2000/08/16 21:50:02  case
  cjr: Now sets filename (new attribute) and mimetype according to values
       made available through HttpPost: parameters file_name and file_type
 
  Revision 1.2  2000/08/06 00:31:05  case
  cjr: Now calls org.mmbase.util.MagicFile to automatically set mimetype on upload
 
 
*/
package org.mmbase.module.builders;

import java.util.*;
import java.io.*;
import java.sql.*;

import org.mmbase.module.builders.*;
import org.mmbase.module.database.*;
import org.mmbase.module.core.*;
import org.mmbase.module.gui.html.EditState;
import org.mmbase.util.*;


/**
 * @author cjr@dds.nl
 *
 * @version $Id: Attachments.java,v 1.4 2000-08-18 21:37:17 case Exp $
 *
 */
public class Attachments extends MMObjectBuilder {
    private String classname = getClass().getName();
    private boolean debug = false;

    protected String defaultMimeType = "application/x-binary";

    public boolean process(scanpage sp, StringTokenizer command, Hashtable cmds, Hashtable vars) {
        if (debug) {
            debug("CMDS="+cmds);
            debug("VARS="+vars);
        }
        EditState ed = (EditState)vars.get("EDITSTATE");
        if (debug) {
            debug("Attachments::process() called");
        }
        String action = command.nextToken();
        if (action.equals("SETFIELD")) {
            String fieldname = command.nextToken();
            if (debug) {
                debug("fieldname = "+fieldname);
            }
            setEditFileField(ed, fieldname, cmds, sp);
        }
        return false;
    }

    public String getGUIIndicator(String field,MMObjectNode node) {
        if (field.equals("handle")) {
            int num=node.getIntValue("number");
            int size = node.getIntValue("size");
            String mimeType = node.getStringValue("mimetype");
            String filename = node.getStringValue("filename");
            if (filename != null && !filename.equals("")) {
                if (size == -1 || num == -1) {
                    return "["+filename+"]";
                } else {
                    return "<a href=\"attachment.db/"+filename+"?"+num+"\" target=\"extern\">["+filename+"]</a>";
                }
            } else {
                return "";
            }
        }
        return (null);
    }

    protected boolean setEditFileField(EditState ed, String fieldname,Hashtable cmds,scanpage sp) {
        MMObjectBuilder obj=ed.getBuilder();
        //FieldDefs def=obj.getField(fieldname);
        try {
            MMObjectNode node=ed.getEditNode();
            if (node!=null) {
                byte[] bytes=sp.poster.getPostParameterBytes("file");

                // [begin] Let's see if we can get to the filename, -cjr
                String file_name = sp.poster.getPostParameter("file_name");
                String file_type = sp.poster.getPostParameter("file_type");
                String file_size = sp.poster.getPostParameter("file_size");
                if (debug) {
                    if (file_name == null) {
                        debug("file_name is NULL");
                    } else {
                        debug("file_name = "+file_name);
                    }
                    if (file_type == null) {
                        debug("file_type is NULL");
                    } else {
                        debug("file_type = "+file_type);
                    }
                    if (file_size == null) {
                        debug("file_size is NULL");
                    } else {
                        debug("file_size = "+file_size);
                    }
                }

                // [end]
                node.setValue(fieldname,bytes);

                if (bytes != null && bytes.length > 0) {
                    //MagicFile magic = new MagicFile();
                    //String mimetype = magic.test(bytes);
                    node.setValue("mimetype",file_type);
                    node.setValue("filename",file_name);
                    node.setValue("size",bytes.length);  // Simpler than converting "file_size"
                }
                else {
                    if (debug) {
                        debug("Grr. Got zero bytes");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return(true);
    }

    /**
     * cjr: copied from FieldEditor
     * getFile: This method creates a byte array using the specified filepath argument.
     */
    public byte[] getFile(String filepath) {
        try {
            File file = new File(filepath);
            FileInputStream fis = new FileInputStream(filepath);
            byte[] ba = new byte[(int)file.length()];	//Create a bytearray with a length the size of the filelength.
            fis.read(ba);	//Read up to ba.length bytes of data from this inputstream into the bytearray ba.
            fis.close();
            return(ba);
        } catch (IOException ioe) {
            return(null);
        }
    }
}




