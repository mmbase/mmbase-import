/*
 
This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.
 
The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license
 
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
import org.mmbase.util.logging.*;

/**
 * This builder can be used to maintain files
 *
 * @author cjr@dds.nl
 */
public class Attachments extends MMObjectBuilder {
    private static Logger log = Logging.getLoggerInstance(Attachments.class.getName());

    protected String defaultMimeType = "application/x-binary";

	/**
	 * this method will be invoked while uploading the file.
	 */
    public boolean process(scanpage sp, StringTokenizer command, Hashtable cmds, Hashtable vars) {
     	log.debug("CMDS="+cmds);
       	log.debug("VARS="+vars);

        EditState ed = (EditState)vars.get("EDITSTATE");
        log.debug("Attachments::process() called");

        String action = command.nextToken();
        if (action.equals("SETFIELD")) {
            String fieldname = command.nextToken();
            log.debug("fieldname = "+fieldname);
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
                    return "<a href=\"/attachment.db/"+filename+"?"+num+"\" target=\"extern\">["+filename+"]</a>";
                }
            } else {
                return "";
            }
        }
        return (null);
    }

    protected boolean setEditFileField(EditState ed, String fieldname,Hashtable cmds,scanpage sp) {
        MMObjectBuilder obj=ed.getBuilder();
        try {
            MMObjectNode node=ed.getEditNode();
            if (node!=null) {
                byte[] bytes=sp.poster.getPostParameterBytes("file");

                // [begin] Let's see if we can get to the filename, -cjr
                String file_name = sp.poster.getPostParameter("file_name");
                String file_type = sp.poster.getPostParameter("file_type");
                String file_size = sp.poster.getPostParameter("file_size");
              	if (file_name == null) {
                	log.debug("file_name is NULL");
          		} else {
              		log.debug("file_name = "+file_name);
          		}
          		if (file_type == null) {
              		log.debug("file_type is NULL");
          		} else {
              		log.debug("file_type = "+file_type);
          		}
          		if (file_size == null) {
              		log.debug("file_size is NULL");
          		} else {
             	 	log.debug("file_size = "+file_size);
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
                	log.debug("Attachment builder -> Grr. Got zero bytes");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return(true);
    }

	public boolean setValue(MMObjectNode node, String field) {

		try {
			if(field.equals("handle") && node.getValue("mimetype")==null) {
				byte[] handle = (byte[])node.getValue("handle");
				node.setValue("size",handle.length);
				log.debug("Attachment size of file = "+handle.length);
				MagicFile magic = new MagicFile();
       			node.setValue("mimetype",magic.test(handle));
				log.debug("ATTACHMENT mimetype of file = "+magic.test(handle));
			}
		} catch (Exception e) {
			log.error("Attachments, wasn't able to determine mime/type or size");
		}
		
		return true;	
	}	
}
