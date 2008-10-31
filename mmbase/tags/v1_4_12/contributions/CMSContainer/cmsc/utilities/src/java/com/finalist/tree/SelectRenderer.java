/*

 This software is OSI Certified Open Source Software.
 OSI Certified is a certification mark of the Open Source Initiative.

 The license (Mozilla version 1.0) can be read at the MMBase site.
 See http://www.MMBase.org/license

 */
package com.finalist.tree;

import java.text.MessageFormat;

import javax.servlet.http.HttpServletResponse;

import net.sf.mmapps.commons.util.StringUtil;

import org.mmbase.bridge.Node;

import com.finalist.cmsc.mmbase.TreeUtil;

public abstract class SelectRenderer implements TreeCellRenderer {

   private HttpServletResponse response;
   private String linkPattern;
   private String target;


   protected SelectRenderer(HttpServletResponse response, String linkPattern, String target) {
      this.response = response;
      this.linkPattern = linkPattern;
      this.target = target;
   }


   /**
    * @see com.finalist.tree.TreeCellRenderer#getElement(TreeModel, Object,
    *      String)
    */
   public TreeElement getElement(TreeModel model, Object node, String id) {
      Node parentNode = (Node) node;
      if (id == null) {
         id = String.valueOf(parentNode.getNumber());
      }
      String fragment = getFragment(parentNode);

      String name = getName(parentNode);
      String icon = getIcon(node);
      TreeElement element = createElement(icon, id, name, fragment);
      Object[] arguments = { String.valueOf(parentNode.getNumber()), parentNode.getStringValue(TreeUtil.PATH_FIELD) };
      String link = MessageFormat.format(linkPattern, arguments);
      if (!link.startsWith("javascript:")) {
         link = getUrl(link);
      }
      element.setLink(link);
      if (!StringUtil.isEmpty(target)) {
         element.setTarget(target);
      }
      return element;
   }


   private String getUrl(String url) {
      return response.encodeURL(url);
   }


   protected abstract TreeElement createElement(String icon, String id, String name, String fragment);


   protected abstract String getName(Node parentNode);


   protected abstract String getFragment(Node parentNode);


   public abstract String getIcon(Object node);

   public boolean showChildren(Object node){
      return true;//By default show its children
   }
}
