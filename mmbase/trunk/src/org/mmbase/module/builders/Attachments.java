/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.builders;

import java.util.*;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.mmbase.module.core.*;
import org.mmbase.module.gui.html.EditState;
import org.mmbase.util.*;
import org.mmbase.util.functions.Parameters;
import org.mmbase.util.logging.*;

/**
 * This builder can be used for 'attachments' builders. That is
 * builders which have a 'handle' field and are associated with the
 * 'attachments servlet.
 *
 * @author cjr@dds.nl
 * @author Michiel Meeuwissen
 * @version $Id: Attachments.java,v 1.28 2004-01-09 15:35:12 nico Exp $
 */
public class Attachments extends AbstractServletBuilder {
    private static final Logger log = Logging.getLoggerInstance(Attachments.class);


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

    protected String getSGUIIndicator(MMObjectNode node, Parameters a) {
        String field = a.getString("field");
        if (field.equals("handle") || field.equals("")) {
            int num  = node.getIntValue("number");
            //int size = node.getIntValue("size");

            String fileName = node.getStringValue("filename");
            String title;

            if (fileName == null || fileName.equals("")) {
                title = "[*]";
            } else {
                title = "[" + fileName + "]";
            }

            if (/*size == -1  || */ num == -1) { // check on size seems sensible, but size was often not filled
                return title;               
            } else {
                String ses = (String) a.get("session");
                if (log.isDebugEnabled()) {
                    log.debug("bridge: " + usesBridgeServlet + " ses: " + ses);
                }
                StringBuffer servlet = new StringBuffer();
                HttpServletRequest req = (HttpServletRequest) a.get("request");
                if (req != null) {            
                    servlet.append(getServletPath(UriParser.makeRelative(new java.io.File(req.getServletPath()).getParent(), "/")));
                } else {
                    servlet.append(getServletPath());
                }
                boolean addFileName =   ! (servlet.charAt(servlet.length() - 1) == '?' ||  "".equals(fileName));
                servlet.append(usesBridgeServlet && ses != null ? "session=" + ses + "+" : "").append(num);

                if (addFileName) {
                    servlet.append('/').append(fileName);
                }

                HttpServletResponse res = (HttpServletResponse) a.get("response");
                String url;
                if (res != null) {
                    url = res.encodeURL(servlet.toString());
                } else {
                    url = servlet.toString();
                }
                return "<a href=\"" + url + "\" target=\"extern\">" + title + "</a>";
            }
        }
        return super.getSuperGUIIndicator(field, node);
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
     * If mimetype is not filled on storage in the database, then we
     * can try to do smart things.
     */

    protected void checkHandle(MMObjectNode node) {
        String mimetype = node.getStringValue("mimetype");
        if (mimetype == null || mimetype.equals("")) {
            log.service("Mimetype of attachment '" + node.getStringValue("title") + "' was not set. Using magic to determin it automaticly.");
            byte[] handle = node.getByteValue("handle");
            node.setValue("size", handle.length); // also the size, why not.
            log.debug("Attachment size of file = " + handle.length);
            
            String filename = node.getStringValue("filename");
            String extension = null;
            int dotIndex = filename.lastIndexOf("."); 
            if (dotIndex > -1) {
                extension = filename.substring(dotIndex + 1);
            }

            MagicFile magic = MagicFile.getInstance();
            try {
                String mime = null;
                if (extension == null) {
                    mime = magic.getMimeType(handle);
                }
                else {
                    mime = magic.getMimeType(handle, extension);
                }
                log.service("Found mime-type: " + mime);
                node.setValue("mimetype", mime);
            } catch (Throwable e) {
                log.warn("Exception in MagicFile  for " + node);
                node.setValue("mimetype", "application/octet-stream");                    
            }            
        }
    }

    public int insert(String owner, MMObjectNode node) {
        checkHandle(node);
        return super.insert(owner, node);
    }
    public boolean commit(MMObjectNode node) {
        checkHandle(node);
        return super.commit(node);
    }

    /**
     * Implements 'mimetype' function (Very simply for attachments, because they have the field).
     *
     * @since MMBase-1.6.1
     */
    protected Object executeFunction(MMObjectNode node, String function, List args) {
        log.debug("executeFunction of attachments builder");
        if ("mimetype".equals(function)) {
            return node.getStringValue("mimetype");
        }
        return super.executeFunction(node, function, args);
    }

}
