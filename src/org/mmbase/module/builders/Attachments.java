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

import org.mmbase.servlet.MMBaseServlet;
import org.mmbase.module.builders.*;
import org.mmbase.module.database.*;
import org.mmbase.module.core.*;
import org.mmbase.module.gui.html.EditState;
import org.mmbase.util.*;
import org.mmbase.util.logging.*;

/**
 * This builder can be used for 'attachments' builders. That is
 * builders which have a 'handle' field and are associated with the
 * 'attachments servlet.
 *
 * @author cjr@dds.nl
 * @author Michiel Meeuwissen
 * @version $Id: Attachments.java,v 1.13 2002-06-28 22:58:12 michiel Exp $ 
 */
public class Attachments extends AbstractServletBuilder {
    private static Logger log = Logging.getLoggerInstance(Attachments.class.getName());


    protected String getAssociation() {
        return "attachments";
    }
    protected String getDefaultPath() {
        return "/attachment.db";
    }

    /**
     * this method will be invoked while uploading the file.
     */
    public boolean process(scanpage sp, StringTokenizer command, Hashtable cmds, Hashtable vars) {
        if (log.isDebugEnabled()) {
            log.debug("CMDS="+cmds);
            log.debug("VARS="+vars);
        }

        EditState ed = (EditState)vars.get("EDITSTATE");
        log.debug("Attachments::process() called");

        String action = command.nextToken();
        if (action.equals("SETFIELD")) {
            String fieldname = command.nextToken();
            if (log.isDebugEnabled()) log.debug("fieldname = "+fieldname);
            setEditFileField(ed, fieldname, cmds, sp);
        }
        return false;
    }

    public String getGUIIndicator(MMObjectNode node) {
        return getGUIIndicator("handle", node);
    }

    public String getGUIIndicator(String field, MMObjectNode node) {
        if (field.equals("handle")) {
            int num  = node.getIntValue("number");
            int size = node.getIntValue("size");

            String mimeType = node.getStringValue("mimetype");
            String filename = node.getStringValue("filename");

            if (filename == null) {
                filename = "*";                
            }

            if (/*size == -1  || */ num == -1) { // check on size seems sensible, but size was often not filled
                return "[" + filename + "]";
            } else {
                return "<a href=\"" + getServletPath(filename) + num + "\" target=\"extern\">[" + filename + "]</a>";
            }            
        }
        return super.getGUIIndicator(field, node);
    }

    /**
     * @javadoc
     */

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

    /**
     *
     */

    public boolean setValue(MMObjectNode node, String field) {

        // does not seem to work...
        // Using the bridge (jsp), mimetype is never filled automaticly
        // TODO: fix MagicFile
        log.service("Setting field " + field + " of node " + node);
        if(field.equals("handle")) {            
            String mimetype = node.getStringValue("mimetype");
            if (mimetype != null && !mimetype.equals("")) {
                log.debug("mimetype was set already");
            } else {
                byte[] handle = node.getByteValue("handle");
                log.debug("Attachment size of file = " + handle.length);
                node.setValue("size", handle.length);
                MagicFile magic = new MagicFile();
                node.setValue("mimetype", magic.test(handle));
                log.debug("ATTACHMENT mimetype of file = " + magic.test(handle));
            }
            return true;
        }        		
        return super.setValue(node, field);	
    }	
}
