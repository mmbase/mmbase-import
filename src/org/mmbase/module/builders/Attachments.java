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
import org.mmbase.util.*;
import org.mmbase.util.functions.*;
import org.mmbase.util.logging.*;

/**
 * This builder can be used for 'attachments' builders. That is
 * builders which have a 'handle' field and are associated with the
 * 'attachments servlet.
 *
 * @author cjr@dds.nl
 * @author Michiel Meeuwissen
 * @version $Id: Attachments.java,v 1.37 2005-06-27 15:24:56 michiel Exp $
 */
public class Attachments extends AbstractServletBuilder {
    private static final Logger log = Logging.getLoggerInstance(Attachments.class);

    public static final String FIELD_SIZE       = "size";

    protected String getAssociation() {
        return "attachments";
    }
    protected String getDefaultPath() {
        return "/attachment.db";
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
        } else if (field.equals(FIELD_SIZE)) {
            return getFileSizeGUI(node.getIntValue(FIELD_SIZE));
            
        }
        return super.getSuperGUIIndicator(field, node);
    }

    protected final Set ATTACHMENTS_HANDLE_FIELDS = Collections.unmodifiableSet(new HashSet(Arrays.asList(new String[] {FIELD_MIMETYPE, FIELD_SIZE})));
    // javadoc inherited
    protected Set getHandleFields() {
        return ATTACHMENTS_HANDLE_FIELDS;
    }

    /**
     * If mimetype is not filled on storage in the database, then we
     * can try to do smart things.
     */
    protected void checkHandle(MMObjectNode node) {
        super.checkHandle(node);
        if (getField(FIELD_SIZE) != null) {
            if (node.getIntValue(FIELD_SIZE) == -1) {
                node.setValue(FIELD_SIZE, node.getByteValue(FIELD_HANDLE).length);
            }
        }
    }

    {
        /**
         * @since MMBase-1.8
         */
        addFunction(new NodeFunction("iconurl",
                                     new Parameter[] {
                                         Parameter.CLOUD, // makes it possible to implement by bridge.
                                         Parameter.REQUEST,
                                         new Parameter("iconroot", String.class, "/mmbase/style/icons/")
                                     },
                                     ReturnType.STRING) {
                {
                    setDescription("Returns an URL for an icon for this attachment");
                }
                public Object getFunctionValue(MMObjectNode node, Parameters parameters) {
                    String mimeType = node.getStringValue(FIELD_MIMETYPE);
                    ResourceLoader webRoot = ResourceLoader.getWebRoot();
                    HttpServletRequest request = (HttpServletRequest) parameters.get(Parameter.REQUEST);
                    String root;
                    if (request != null) {
                        root = request.getContextPath();
                    } else {
                        root = MMBaseContext.getHtmlRootUrlPath();
                    }

                    String iconRoot = (String) parameters.get("iconroot");
                    if (root.endsWith("/") && iconRoot.startsWith("/")) iconRoot = iconRoot.substring(1);

                    if (! iconRoot.endsWith("/")) iconRoot = iconRoot + '/';
                    
                    String resource = iconRoot + mimeType + ".gif";
                    try {
                        if (! webRoot.getResource(resource).openConnection().getDoInput()) {
                            resource = iconRoot + "application/octet-stream.gif";
                        }
                    } catch (java.io.IOException ioe) {
                        log.warn(ioe.getMessage(), ioe);
                        resource = iconRoot + "application/octet-stream.gif";
                    }
                    return root + resource;
                }

            });
    }


    /**
     * Implements 'mimetype' function (Very simply for attachments, because they have the field).
     *
     * @since MMBase-1.6.1
     */
    protected Object executeFunction(MMObjectNode node, String function, List args) {
        log.debug("executeFunction of attachments builder");
        if ("mimetype".equals(function)) {
            return node.getStringValue(FIELD_MIMETYPE);
        } else if (function.equals("format")) {
            String mimeType = node.getStringValue(FIELD_MIMETYPE);
            if (mimeType.length() > 0) {
                MagicFile mf = MagicFile.getInstance();
                String ext = mf.mimeTypeToExtension(mimeType);
                if (! "".equals(ext)) {
                    return ext;
                }
            }
        }
        return super.executeFunction(node, function, args);
    }

}
