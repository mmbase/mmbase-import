/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.builders;

import java.io.*;
import java.net.*;
import java.util.*;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.mmbase.bridge.*;
import org.mmbase.module.core.*;
import org.mmbase.util.*;
import org.mmbase.util.logging.*;
import org.mmbase.util.functions.*;

/**
 * An attachement builder where, asside form storing the binary data in the database, you can point out a 
 * binary resource on another server using an url. 
 *
 * @author Pierre van Rooden
 * @version $Id$
 * @since   MMBase-1.8
 */
public class ReferredAttachments extends Attachments {

    public static final String FIELD_URL = "url";
    private static final Logger log = Logging.getLoggerInstance(ReferredAttachments.class);

    private NodeFunction formerFunction;

    public NodeFunction servletPathFunction =
	new NodeFunction("servletpath", new Parameter[] {
                                             new Parameter("session",  String.class), // For read-protection
                                             new Parameter("field",    String.class), // The field to use as argument, defaults to number unless 'argument' is specified.
                                             new Parameter("context",  String.class), // Path to the context root, defaults to "/" (but can specify something relative).
                                             new Parameter("argument", String.class), // Parameter to use for the argument, overrides 'field'
                                             Parameter.REQUEST,
                                             Parameter.CLOUD
                                         },
                                         ReturnType.STRING) {
                {
                    setDescription("Returns the path associated with this builder or node.");
                }

                protected StringBuffer getServletPath(Parameters a) {
                    StringBuffer servlet = new StringBuffer();
                    // third argument, the servlet context, can use a relative path here, as an argument
                    String context             = (String) a.get("context");

                    if (context == null) {
                        // no path to context-root specified explitiely, try to determin:
                        HttpServletRequest request = (HttpServletRequest) a.get(Parameter.REQUEST);
                        if (request == null) {
                            // no request object given as well, hopefully it worked on servlet's initalizations (it would, in most servlet containers, like tomcat)
                            servlet.append(ReferredAttachments.this.getServletPath()); // use 'absolute' path (starting with /)
                        } else {
                            servlet.append(ReferredAttachments.this.getServletPath(request.getContextPath()));
                        }
                    } else {
                        // explicitely specified the path!
                        servlet.append(ReferredAttachments.this.getServletPath(context));
                    }
                    return servlet;
                }

                public Object getFunctionValue(Node node, Parameters a) {
                  String url = node.getStringValue("url");
                  if (url != null && !url.equals("")) {
                    return url;
                  } else {

                  
                    StringBuffer servlet = getServletPath(a);

                    String session = getSession(a, node.getNumber());
                    String argument = (String) a.get("argument");
                    // argument representint the node-number

                    if (argument == null) {
                        String fieldName   = (String) a.get("field");
                        if (fieldName == null || "".equals(fieldName)) {
                            argument = node.getStringValue("number");
                        } else {
                            if (log.isDebugEnabled()) {
                                log.debug("Getting 'field' '" + fieldName + "'");
                            }
                            argument = node.getStringValue(fieldName);
                        }
                    }
                    MMObjectNode mmnode = node.getNumber() > 0 ?
                        ReferredAttachments.this.getNode(node.getNumber()) :
                        new MMObjectNode(ReferredAttachments.this, new org.mmbase.bridge.util.NodeMap(node));
                    boolean addFileName = addFileName(mmnode, servlet.toString());

                    log.debug("Using session " + session);

                    if (usesBridgeServlet &&  session != null) {
                        servlet.append("session=" + session + "+");
                    }

                    if (! addFileName) {
                        return servlet.append(argument).toString();
                    } else {
                        servlet.append(argument).append('/');
                        getFileName(mmnode, servlet);
                        return servlet.toString();
                    }
                  }
                }

                public Object getFunctionValue(Parameters a) {
                    return getServletPath(a).toString();
                }
            };

    public boolean init() {
      super.init();
      formerFunction = (NodeFunction) getFunction("servletpath"); 
      addFunction(servletPathFunction);
      return true;
    }
    
    
    protected void checkHandle(MMObjectNode node) {
      String url = node.getStringValue(FIELD_URL);
      if (url != null && !url.equals("")) {
        try {
            URL reference = new URL(url);
            URLConnection connection  = reference.openConnection();
            
            if (getField(FIELD_SIZE) != null) {
                if (node.getIntValue(FIELD_SIZE) == -1) {
                  node.setValue(FIELD_SIZE, connection.getContentLength());
                }
            }
            if (getField(FIELD_MIMETYPE) != null) {
              node.setValue(FIELD_MIMETYPE, connection.getContentType());
            }
            if (getField(FIELD_FILENAME) != null) {
              String filename = url;
              int pos = filename.lastIndexOf('/');
              if (pos > 0 && pos < filename.length()-1) {
                filename = filename.substring(pos+1);
              }
              node.setValue(FIELD_FILENAME, filename);
            }
        } catch (MalformedURLException mue) {
          log.warn("wrong url format:" + url);
        } catch (IOException ie) {
          log.warn("cannot connect to:" + url);
        }
      } else {
        super.checkHandle(node);
      }
    }
    
    public boolean commit(MMObjectNode node) {
        Collection changed = node.getChanged();
        if (changed.contains(FIELD_URL)) {
            // set those fields to null, which are not changed too:
            Collection cp = new ArrayList();
            cp.addAll(getHandleFields());
            cp.removeAll(changed);
            Iterator i = cp.iterator();
            while (i.hasNext()) {
                String f = (String) i.next();
                if (node.getBuilder().hasField(f)) {
                    node.setValue(f, null);
                }
            }
        }
        return super.commit(node);
    }
    
    protected String getSGUIIndicator(MMObjectNode node, Parameters a) {
        String field = a.getString("field");
        if (field.equals("handle") || field.equals("")) {
          String url = node.getStringValue(FIELD_URL);
          if (url != null && !url.equals("")) {
            String fileName = getFileName(node, new StringBuffer()).toString();
            String title;
            if (fileName == null || fileName.equals("")) {
                title = "[*]";
            } else {
                title = "[" + fileName + "]";
            }            
	          HttpServletResponse res = (HttpServletResponse) a.get("response");
            if (res != null) {
                url = res.encodeURL(url);
            } 
            return "<a href=\"" + url + "\" target=\"extern\">" + title + "</a>";
          }
        }
        return super.getSGUIIndicator(node, a);
    }

}
