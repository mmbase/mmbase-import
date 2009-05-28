/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.servlet;

import java.io.*;
import java.util.Map;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.*;

import org.mmbase.bridge.*;

import org.mmbase.util.*;
import org.mmbase.util.logging.*;


/**
 * Base servlet for nodes with a 'handle' field. It serves as a basic implementation for more
 * specialized servlets. The mime-type is always application/x-binary, forcing the browser to
 * download.
 *
 * @version $Id$
 * @author Michiel Meeuwissen
 * @since  MMBase-1.6
 * @see ImageServlet
 * @see AttachmentServlet
 */
public class HandleServlet extends BridgeServlet {
    private static Logger log;

    private long expires; // expires so many milliseconds after serving

    private boolean isIECompatibleJpeg = true;

    protected Map getAssociations() {
        Map a = super.getAssociations();
        // Can do the following:
        a.put("attachments", new Integer(0));
        a.put("downloads",   new Integer(20)); // good at this (because it does not determine the mime-type)
        a.put("images",      new Integer(-10)); // bad in images (no mime-type, no awareness of icaches)
        return a;
    }

    /**
     * Takes care of the 'expire' init-parameter.
     * {@inheritDoc}
     */
    public void init() throws ServletException {
        super.init();
        log = Logging.getLoggerInstance(HandleServlet.class);

        String expiresParameter = getInitParameter("expire");
        if (expiresParameter == null) {
            // default: one hour
            expires = 60 * 60 * 1000;
        } else {
            expires = Integer.valueOf(expiresParameter).intValue() * 1000;
        }

        String ieCompat = getInitParameter("IECompatibleJpeg");
        if (ieCompat != null) {
            isIECompatibleJpeg = Boolean.valueOf(ieCompat).booleanValue();
        }
    }

    // just to get HandleServlet in the stacktrace.
    protected Cloud getClassCloud() {
        return super.getClassCloud();
    }

    /**
     * Forces download in browsers.
     * This is overriden in several extensions.
     */
    protected String getMimeType(Node node) {
        return "application/x-binary";
    }


    protected static final Pattern legalizeFileName = Pattern.compile("[\\/\\:\\;\\\\\"]+");

    /**
     * @since MMBase-1.8
     */
    protected String getFileName(final Node node, Node titleNode, final String def) {
        if (titleNode == null) titleNode = node;
        NodeManager nm = titleNode.getNodeManager();
        // Try to find a sensible filename to use in the content-disposition header.
        String fileName;
        if (node == titleNode) {
            fileName = nm.hasField("filename") ? titleNode.getStringValue("filename") : null;
        } else {
            if (nm.hasField("filename")) {
                fileName = titleNode.getStringValue("filename");
                String ext = node.getFunctionValue("format", null).toString();
                if (! ext.equals(titleNode.getFunctionValue("format", null).toString())) {
                    fileName += '.' + ext;
                }
            } else {
                fileName = null;
            }
        }
        if (fileName != null) {
            int backSlash = fileName.lastIndexOf("\\");
            // if uploaded in MSIE, then the path may be in the fileName
            // this is also fixed in the set-processor, but if that is or was missing, be gracefull here.
            if (backSlash > -1)  {
                fileName = fileName.substring(backSlash + 1);
            }
        }

        if (fileName == null || fileName.equals("")) {
            fileName = nm.hasField("title") ? titleNode.getStringValue("title") + '.' + node.getFunctionValue("format", null).toString() : null;
        }
        if (fileName == null || fileName.equals("")) {
            fileName = nm.hasField("name") ? titleNode.getStringValue("name") + '.' + node.getFunctionValue("format", null).toString() : null;
        }
        if (fileName == null || fileName.equals("")) { // give it up
            fileName = def + "." + node.getFunctionValue("format", null).toString();
        }

        return legalizeFileName.matcher(fileName).replaceAll("_");
    }

    /**
     *@since MMBase-1.8.7
     */
    protected String getContentDisposition(QueryParts query, Node node, String def) {
        String fileNamePart = query.getFileName();
        if(fileNamePart != null) {
            if (fileNamePart.startsWith("/inline/")) {
                return "inline";
            }
            if (fileNamePart.startsWith("/attachment/")) {
                return "attachment";
            }
        }
        String cd = node.getNodeManager().getProperty("Content-Disposition");
        return cd == null ? def : cd;
    }

    /**
     * Sets the content disposition header.
     * @return true on success
     */
    protected boolean setContent(QueryParts query, Node node, String mimeType) throws IOException {
        String disposition = getContentDisposition(query, node, "attachment");
        query.getResponse().setHeader("Content-Disposition", disposition + "; filename=\""  + getFileName(node, null, "mmbase-attachment")+ "\"");
        //res.setHeader("X-MMBase-1", "Not sending Content-Disposition because this might confuse Microsoft Internet Explorer");
        return true;
    }

    /**
     * Sets the exires header.
     * @return true on sucess
     */
    protected boolean setExpires(HttpServletResponse res, Node node) {
        if (node.getNodeManager().getName().equals("icaches")) {
            // cached images never expire, they cannot change without receiving a new number, thus changing the URL.
            long never = System.currentTimeMillis() + (long) (365.25 * 24 * 60 * 60 * 1000);
            // one year in future, this is considered to be sufficiently 'never'.
            res.setDateHeader("Expires", never);
        } else {
            long later = System.currentTimeMillis() + expires;
            res.setDateHeader("Expires", later);
        }
        return true;
    }

    /**
     * Sets cache-controlling headers. Only nodes which are to be served to 'anonymous' might be
     * (front proxy) cached. To other nodes there might be read restrictions, so they should not be
     * stored in front-proxy caches.
     *
     * @return true if cacheing is disabled.
     * @since MMBase-1.7
     */
    protected boolean setCacheControl(HttpServletResponse res, Node node) {
        if (!node.getCloud().getUser().getRank().equals(org.mmbase.security.Rank.ANONYMOUS)) {
            res.setHeader("Cache-Control", "private");
            // res.setHeader("Pragma", "no-cache"); // for http 1.0 : is frustrating IE when https
            // res.setHeader("Pragma", "no-store"); // no-cache not working in apache!
            // we really don't want this to remain in proxy caches, but the http 1.0 way is making IE not work.
            return true;
        } else {
            res.setHeader("Cache-Control", "public");
            return false;
        }
    }


    /**
     * Serves a node with a byte[] handle field as an attachment.
     */

    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        QueryParts query = readQuery(req, res);
        Node queryNode = getNode(query);
        if (queryNode == null) {
            return;
        }

        //res.setHeader("X-MMBase-Version", org.mmbase.Version.get());
        Node node = getServedNode(query, getNode(query));

        if (node == null) {
            return;
        }

        NodeManager manager = node.getNodeManager();
        if (! manager.hasField("handle")) {
            res.sendError(HttpServletResponse.SC_NOT_FOUND, "No handle found in node " + node.getNumber());
            req.setAttribute(MESSAGE_ATTRIBUTE, "No handle found in node " + node.getNumber());
            return;
        }

        // fill the headers
        res.setDateHeader("Date", System.currentTimeMillis());

        String mimeType = getMimeType(node);
        res.setContentType(mimeType);

        if (node.isNull("handle")) {
            return;
        }
        InputStream bytes = node.getInputStreamValue("handle");


        //remove additional information left by PhotoShop 7 in jpegs
        //this information may crash Internet Exploder. that's why you need to remove it.
        //With PS 7, Adobe decided by default to embed XML-encoded "preview" data into JPEG files,
        //using a feature of the JPEG format that permits embedding of arbitrarily-named "profiles".
        //In theory, these files are valid according to the JPEG specifications.
        //However they break many applications, including Quark and, significantly,
        //various versions of Internet Explorer on various platforms.

        boolean canSendLength = true;

        if (isIECompatibleJpeg) {
            if (mimeType.equals("image/jpeg") || mimeType.equals("image/jpg")) {
                bytes = new IECompatibleJpegInputStream(bytes);
                canSendLength = false;
                //res.setHeader("X-MMBase-IECompatibleJpeg", "This image was filtered, because Microsoft Internet Explorer might crash otherwise");
            }
        }

        if (!setContent(query, node, mimeType)) {
            return;
        }
        setExpires(res, node);
        setCacheControl(res, node);

        if (canSendLength) {
            int size = -1;
            if (manager.hasField("size")) {
                size = node.getIntValue("size");
            } else if (manager.hasField("filesize")) {
                size = node.getIntValue("filesize");
            }
            if (size >= 0) {
                res.setContentLength(size);
            }
            log.debug("Serving node " + node.getNumber() + " with bytes " + size);
        } else {
            log.debug("Serving node " + node.getNumber() + " with unknown size, because IE sucks");
        }
        sendBytes(res, bytes);
    }


    /**
     * Utility function to send bytes at the end of doGet implementation.
     * @deprecated
     */
    final protected void sendBytes(HttpServletResponse res, byte[] bytes) throws IOException {
        int fileSize = bytes.length;
        res.setContentLength(fileSize);

        BufferedOutputStream out = new BufferedOutputStream(res.getOutputStream());
        out.write(bytes, 0, fileSize);
        out.flush();
    }
    final protected void sendBytes(HttpServletResponse res, InputStream bytes) throws IOException {
        if (log.isDebugEnabled()) {
            log.debug("Sending by " + bytes.getClass());
        }
        IOUtil.copy(bytes, res.getOutputStream());
        res.getOutputStream().flush();
        bytes.close();
    }

    public static void main(String argv[]) {
        System.out.println(legalizeFileName.matcher(argv[0]).replaceAll("_"));
    }

}
