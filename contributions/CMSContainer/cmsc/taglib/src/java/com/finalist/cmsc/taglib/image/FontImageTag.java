package com.finalist.cmsc.taglib.image;

import java.util.ArrayList;

import javax.servlet.jsp.JspTagException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mmbase.bridge.Node;
import org.mmbase.util.images.Dimension;

public class FontImageTag extends ImageTag {

   private static final long serialVersionUID = -4281091969074050923L;

   private static Log log = LogFactory.getLog(FontImageTag.class);

   private int width;
   private int height;


   @Override
   public int doStartTag() {
      return EVAL_BODY;
   }


   @Override
   public int doEndTag() throws JspTagException {

      Node node = getNode();
      StringBuffer template = new StringBuffer();
      StringBuffer altText = new StringBuffer();

      if (!node.getNodeManager().hasField("handle")) {
         throw new JspTagException("Found parent node '" + node.getNumber() + "' of type "
               + node.getNodeManager().getName() + " does not have 'handle' field, therefore cannot be a image."
               + " Perhaps you have the wrong node, perhaps you'd have to use the 'node' attribute?");
      }

      if (width > 0 && height > 0) {
         template.append("+" + getResizeTemplate(node, width, height));
      }

      ArrayList<ImageTextTag> textTags = (ArrayList<ImageTextTag>) getPageContext()
            .getAttribute(ImageTextTag.TEXT_TAGS);
      getPageContext().setAttribute(ImageTextTag.TEXT_TAGS, null);

      if (textTags == null) {
         log.warn("FontImageTag without ImageTextTags");
      }
      else {
         boolean asis = "true".equals(pageContext.getServletContext().getInitParameter(
               "mmbase.taglib.image.format.asis"));
         for (ImageTextTag textTag : textTags) {
            textTag.addToTemplate(template, asis);
            if (textTag.getText() != null) {
               altText.append(", " + textTag.getText());
            }
         }
      }

      String templateString = (template.length() > 1) ? template.substring(1) : "";
      String altTextString = (altText.length() > 2) ? altText.substring(2) : "";

      helper.useEscaper(false);
      Dimension dim = getDimension(node, templateString);

      Node servletNode = getServletNode(node, templateString);

      String servletArgument;
      if (servletNode == null) {
         servletNode = node;
         log.warn("Found null from " + servletNode + " with '" + templateString + "'");
         servletArgument = servletNode.getStringValue("number");
      }
      else {
         servletArgument = getServletArgument(servletNode, templateString);
      }

      String servletPath = getServletPath(servletNode, servletArgument);
      String outputValue = getOutputValue(ImageTag.MODE_HTML_IMG, node, servletPath, dim);

      outputValue = outputValue.replaceAll("alt=\"[^\"]*\"", "alt=\"" + altTextString + "\"");

      if (outputValue != null) {
         helper.setValue(outputValue);
      }
      pageContext.setAttribute("dimension", dim);

      if (getId() != null) {
         getContextProvider().getContextContainer().register(getId(), helper.getValue());
      }

      return super.doEndTag();
   }


   public void setHeight(int height) {
      this.height = height;
   }


   public void setWidth(int width) {
      this.width = width;
   }
}