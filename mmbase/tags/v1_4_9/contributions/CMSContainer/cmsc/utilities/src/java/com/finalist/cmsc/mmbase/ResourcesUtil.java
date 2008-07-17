/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package com.finalist.cmsc.mmbase;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import net.sf.mmapps.commons.util.StringUtil;

import org.mmbase.bridge.Node;
import org.mmbase.module.builders.Images;
import org.mmbase.module.core.MMBaseContext;
import org.mmbase.servlet.MMBaseServlet;
import org.mmbase.util.functions.*;

public class ResourcesUtil {

   public static final String URLS = "urls";
   public static final String ATTACHMENTS = "attachments";
   public static final String IMAGES = "images";


   public static String getServletPath(Node node, String servletArgument) {
      Function servletPathFunction = getServletFunction(node);
      Parameters args = getServletArguments(servletArgument, servletPathFunction);
      args.setIfDefined(Parameter.CLOUD, node.getCloud());
      args.setIfDefined(Parameter.USER, node.getCloud().getUser());
      return servletPathFunction.getFunctionValue(args).toString();
   }


   public static Function getServletFunction(Node node) {
      Function servletPathFunction = node.getFunction("servletpath");
      return servletPathFunction;
   }


   public static Parameters getServletArguments(String servletArgument, Function servletPathFunction) {
      Parameters args = servletPathFunction.createParameters();
      args.set("context", MMBaseContext.getHtmlRootUrlPath()).set("argument", servletArgument);
      return args;
   }


   public static Node getImageNode(Node imageNode, int height, int width) {
      String template = getTemplate(height, width);
      imageNode = ResourcesUtil.getImageNode(imageNode, template);
      return imageNode;
   }


   public static Node getImageNode(Node node, String template) {
      if (StringUtil.isEmpty(template)) {
         return node;
      }
      else {
         // the cached image
         return node.getFunctionValue("cachednode", new Parameters(Images.CACHE_PARAMETERS).set("template", template))
               .toNode();
      }
   }


   public static String getTemplate(int height, int width) {
      String template = null;
      if (width > 0) {
         if (height > 0) {
            template = "s(" + width + "x" + height + ")";
         }
         else {
            template = "s(" + width + ")";
         }
      }
      else {
         if (height > 0) {
            template = "s(x" + height + ")";
         }
      }
      return template;
   }


   public static String getServletPathWithAssociation(String association, String defaultPath, String id, String title) {
      String servletpath = getServletPathWithAssociation(association, defaultPath);
      if (!servletpath.endsWith("/")) {
         servletpath += "/";
      }
      String url = servletpath + id;
      if (!StringUtil.isEmpty(title)) {
         url += "/" + TreeUtil.convertToFragment(title);
         // Apache webserver fails to pass the request to tomcat when / or \ is
         // in the url (escaped or not)
         url = url.replaceAll("%2F|%5C|%25", "");
      }
      return url;

   }


   public static String getServletPathWithAssociation(String association, String defaultPath) {
      return getServletPathWithAssociation(association, MMBaseContext.getHtmlRootUrlPath(), defaultPath);
   }


   public static String getServletPathWithAssociation(String association, String root, String defaultPath) {
      if (MMBaseContext.isInitialized()) {
         javax.servlet.ServletContext sx = MMBaseContext.getServletContext();
         if (sx != null) {
            String res = sx.getInitParameter("mmbase.servlet." + association + ".url");
            if (res != null && !res.equals("")) {
               return res;
            }
         }
      }
      String result;
      List<String> ls = MMBaseServlet.getServletMappingsByAssociation(association);
      if (ls.size() > 0) {
         result = ls.get(0);
         // remove mask
         int pos = result.lastIndexOf("*");
         if (pos > 0) {
            result = result.substring(0, pos);
         }
         pos = result.indexOf("*");
         if (pos == 0) {
            result = result.substring(pos + 1);
         }
      }
      else {
         result = defaultPath;
      }

      if (result.startsWith("/")) {
         // if it not starts with / then no use adding context.
         if (root != null) {
            if (root.endsWith("/")) {
               result = root + result.substring(1);
            }
            else {
               result = root + result;
            }
         }
      }
      return result;
   }

}
