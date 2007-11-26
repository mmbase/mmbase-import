/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package com.finalist.cmsc.taglib;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;

import org.mmbase.bridge.Node;
import org.mmbase.bridge.jsp.taglib.NodeReferrerTag;
import org.mmbase.bridge.jsp.taglib.util.Attribute;

import com.finalist.cmsc.mmbase.ResourcesUtil;

@SuppressWarnings("serial")
public class ContentUrlTag extends NodeReferrerTag {

   /** Holds value of property number. */
   private Attribute number = Attribute.NULL;
   private boolean absolute = false;


   public void setNumber(String t) throws JspTagException {
      number = getAttribute(t);
   }


   public void setAbsolute(String absolute) {
      this.absolute = Boolean.valueOf(absolute);
   }


   @Override
   public int doStartTag() throws JspException {
      Node node = null;
      int nr = number.getInt(this, -1);
      if (nr == -1) {
         node = getNode();
      }
      else {
         node = getCloudVar().getNode(nr);
      }
      if (node == null) {
         throw new JspTagException("Node not found for content url tag");
      }

      String url = null;
      String builderName = node.getNodeManager().getName();
      if ("attachments".equals(builderName)) {
         url = ResourcesUtil.getServletPath(node, node.getStringValue("number"));
         if (absolute) {
            url = makeAbsolute(url);
         }
      }
      else {
         if ("urls".equals(builderName)) {
            url = node.getStringValue("url");
         }
         else {
            url = getContentUrl(node);
            if (absolute) {
               url = makeAbsolute(url);
            }
         }
      }

      if (url != null) {
         helper.setValue(url);
      }

      if (getId() != null) {
         getContextProvider().getContextContainer().register(getId(), helper.getValue());
      }

      return EVAL_BODY_BUFFERED;
   }


   private String makeAbsolute(String url) {
      String webapp = getServerDocRoot((HttpServletRequest) pageContext.getRequest());
      if (url.startsWith("/")) {
         url = webapp + url.substring(1);
      }
      else {
         url = webapp + url;
      }
      return url;
   }


   public static String getServerDocRoot(HttpServletRequest request) {
      StringBuffer s = new StringBuffer();
      s.append(request.getScheme()).append("://").append(request.getServerName());

      int serverPort = request.getServerPort();
      if (serverPort != 80 && serverPort != 443) {
         s.append(':').append(Integer.toString(serverPort));
      }
      s.append('/');
      return s.toString();
   }


   private String getContentUrl(Node node) {
      return ResourcesUtil.getServletPathWithAssociation("content", "/content/*", node.getStringValue("number"), node
            .getStringValue("title"));
   }


   @Override
   public int doEndTag() throws JspTagException {
      helper.doEndTag();
      return super.doEndTag();
   }

}
