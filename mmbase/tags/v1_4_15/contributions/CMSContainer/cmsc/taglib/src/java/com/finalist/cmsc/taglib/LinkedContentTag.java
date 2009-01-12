package com.finalist.cmsc.taglib;

import net.sf.mmapps.commons.bridge.NodeFieldComparator;
import net.sf.mmapps.commons.util.StringUtil;

import org.mmbase.bridge.*;
import org.mmbase.bridge.jsp.taglib.NodeReferrerTag;
import org.mmbase.bridge.jsp.taglib.util.Attribute;

import com.finalist.cmsc.mmbase.ResourcesUtil;

import java.io.IOException;

import java.util.*;
import java.text.DecimalFormat;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;

/**
 * Tag that creates a list of related content elements.
 * 
 * @author sandervl
 */
@SuppressWarnings("serial")
public class LinkedContentTag extends NodeReferrerTag {

   /**
    * stores the nodemanager name of the related nodes that should be shown in
    * the list
    */
   private Attribute manager;

   /** stores the title of the link list */
   private Attribute title;

   /** the relation role */
   private Attribute role;

   /** the name of the field to sort on */
   private Attribute sort;

   /**
    * Formatter used for layout of list. This attribute is not required. If it
    * is not supplied, the default is used which is
    * net.sf.mmapps.commons.basicmodel.taglib.LinkedContentTag$DefaultFormatter.
    */
   private Attribute formatter = Attribute.NULL;


   private String getFormatter() throws JspTagException {
      String result = formatter.getString(this);
      if (StringUtil.isEmpty(result)) {
         result = "net.sf.mmapps.commons.basicmodel.taglib.LinkedContentTag$DefaultFormatter";
      }
      return result;
   }


   /**
    * This tag returns links to content.
    * 
    * @throws javax.servlet.jsp.JspException
    *            When something goes wrong.
    * @return SKIP_BODY
    */
   @Override
   public int doStartTag() throws JspException {
      try {
         // retrieve the content node
         Node contentNode = getNode();

         String mgrRelatedNodesStr = manager.getString(this);
         String roleStr = role.getString(this);
         String sortStr = sort.getString(this);
         // get the related nodes
         NodeList nodes = contentNode.getRelatedNodes(mgrRelatedNodesStr, roleStr, "destination");

         if (!StringUtil.isEmpty(sortStr)) {
            Collections.sort(nodes, new NodeFieldComparator(sortStr));
         }
         else {
            if (!StringUtil.isEmpty(roleStr)) {
               RelationManager relationManager = contentNode.getCloud().getRelationManager(roleStr);
               if (relationManager != null && relationManager.hasField("pos")) {
                  Collections.sort(nodes, new NodeFieldComparator("pos"));
               }
            }
         }

         // create the formatter
         LinkedContentTag.Formatter format = (Formatter) Class.forName(getFormatter()).newInstance();
         pageContext.getOut().write(format.write(nodes, title.getString(this)));
      }
      catch (IOException e) {
         throw new JspException("An IOException occured while trying to write output.", e);
      }
      catch (InstantiationException e) {
         throw new JspException("Could not instantiate the formatter: " + formatter, e);
      }
      catch (IllegalAccessException e) {
         throw new JspException(this.getClass().getName() + " does not have access to " + formatter, e);
      }
      catch (ClassNotFoundException e) {
         throw new JspException("The class " + formatter + " could not be found", e);
      }
      catch (BridgeException e) {
         throw new JspException("An exception occurred when retrieving content", e);
      }

      return SKIP_BODY;
   }


   /**
    * Setter for property formatter.
    * 
    * @param formatter
    *           full class name of an implementation of
    *           <code>LinkedContentTag.Formatter</code>
    * @throws JspTagException
    */
   public void setFormatter(String formatter) throws JspTagException {
      this.formatter = getAttribute(formatter);
   }


   /**
    * Sets the name of the nodemanager of the related nodes that should be
    * retrieved.
    * 
    * @param mgrRelatedNodes
    *           the name of the nodemanager of the related nodes that should be
    *           retrieved
    * @throws JspTagException
    */
   public void setManager(String mgrRelatedNodes) throws JspTagException {
      this.manager = getAttribute(mgrRelatedNodes);
   }


   /**
    * Sets the title of the link list.
    * 
    * @param title
    *           the title of the link list
    * @throws JspTagException
    */
   public void setTitle(String title) throws JspTagException {
      this.title = getAttribute(title);
   }


   /**
    * The role of the relation.
    * 
    * @param role
    *           the role of the relation
    * @throws JspTagException
    */
   public void setRole(String role) throws JspTagException {
      this.role = getAttribute(role);
   }


   /**
    * The name of the node field to sort on
    * 
    * @param sort
    *           the name of the node field to sort on
    * @throws JspTagException
    */
   public void setSort(String sort) throws JspTagException {
      this.sort = getAttribute(sort);
   }

   /**
    * Generic interface for formatting lists of links.
    */
   interface Formatter {
      /**
       * Should return html that contains the html with the links.
       * 
       * @param list
       *           the node list with the content nodes
       * @param title
       *           the title of the list or <code>null</code>
       * @return some html
       * @throws javax.servlet.jsp.JspException
       *            When something goes wrong.
       */
      String write(NodeList list, String title) throws JspException;
   }

   /**
    * Implementation of the <code>Formatter</code> interface.
    */
   public static class DefaultFormatter implements Formatter {

      /**
       * @see net.sf.mmapps.commons.basicmodel.taglib.LinkedContentTag.Formatter#write(org.mmbase.bridge.NodeList,
       *      java.lang.String)
       */
      public String write(NodeList list, String title) throws JspException {
         if ((list == null) || (list.size() == 0)) {
            return "";
         }

         StringBuffer buffer = new StringBuffer();
         Iterator<Node> iterator = list.iterator();
         boolean first = true;

         while (iterator.hasNext()) {
            Node content = iterator.next();

            if (first) {
               buffer.append("<div class=\"");
               buffer.append(getStyleClass(content));
               buffer.append("\">");

               if (title != null) {
                  buffer.append("<h4>");
                  buffer.append(title);
                  buffer.append("</h4>");
               }

               buffer.append("<ul>");
               first = false;
            }

            buffer.append(writeLink(content));

            if (!iterator.hasNext()) {
               buffer.append("</ul>");
               buffer.append("</div>");
            }
         }

         return buffer.toString();
      }


      /**
       * Returns the style class based on the nodemanager name of the node.
       * 
       * @param content
       *           the content node
       * @return the style class
       */
      public String getStyleClass(Node content) {
         String getStyleClass = content.getNodeManager().getName();

         if (getStyleClass.equals("urls") || getStyleClass.equals("attachments")) {
            getStyleClass = "link";
         }
         else if (getStyleClass.equals("forums") || getStyleClass.equals("threads") || getStyleClass.equals("messages")
               || getStyleClass.equals("forumcategories")) {
            getStyleClass = getStyleClass.substring(0, getStyleClass.length() - 1);
         }

         return getStyleClass + "s";
      }


      /**
       * Writes the list element part of the link html.
       * 
       * @param content
       *           the content object
       * @return some html
       * @throws javax.servlet.jsp.JspException
       *            When something goes wrong.
       */
      public String writeLink(Node content) throws JspException {

         StringBuffer buffer = new StringBuffer();
         try {
            buffer.append("<li><a href=\"");
            buffer.append(getHref(content));
            buffer.append("\"><span class=\"");
            buffer.append(getIconStyleClass(content));
            buffer.append("\">[");
            buffer.append(getIcon(content));
            buffer.append("]&nbsp;</span>");
            buffer.append(content.toString());
            if (content.getNodeManager().getName().equals("attachments")) {
               buffer.append(getSize(content));
            }
            buffer.append("</a></li>");
         }
         catch (BridgeException e) {
            throw new JspException("error when retrieving content", e);
         }

         return buffer.toString();
      }


      /**
       * Determines the icon to return. The icon indicates what type of link it
       * is.
       * 
       * @param content
       *           the content object
       * @return the icon string
       */
      private String getIcon(Node content) {
         String getIcon = "+";
         String mgr = content.getNodeManager().getName();

         if (mgr.equals("attachments")) {
            getIcon = "~";
         }
         else if (mgr.equals("urls")) {
            getIcon = "&gt;";
         }

         return getIcon;
      }


      /**
       * Determines the corresponding stylesheet class for the icon.
       * 
       * @param content
       *           the content object
       * @return the style sheet class
       */
      private String getIconStyleClass(Node content) {
         String getIconStyleClass = "ico-plus";
         String mgr = content.getNodeManager().getName();

         if (mgr.equals("attachments")) {
            getIconStyleClass = "ico-wave";
         }
         else if (mgr.equals("urls")) {
            getIconStyleClass = "ico-ext";
         }

         return getIconStyleClass;
      }


      /**
       * Returns the url that should be referenced by the link.
       * 
       * @param content
       *           the content object
       * @return the url that should be referenced by the link
       */
      private String getHref(Node content) {
         String title = content.getStringValue("title");
         String id = content.getStringValue("number");
         return ResourcesUtil.getServletPathWithAssociation("content", "/content/*", id, title);
      }


      /**
       * Returns a string with the size like ' (123 Kb)' or ' ( 1,5 Mb)'.
       * 
       * @param content
       *           the content object
       * @return the size
       */
      private String getSize(Node content) {
         StringBuffer buffer = new StringBuffer();
         int size = content.getIntValue("size");
         size = size / 1024;
         buffer.append(" (");
         if (size < 1000) {
            buffer.append(size);
            buffer.append(" Kb");
         }
         else {
            DecimalFormat format = new DecimalFormat("#.#");
            buffer.append(format.format(size));
            buffer.append(" Mb");
         }
         buffer.append(")");
         return buffer.toString();
      }
   }
}