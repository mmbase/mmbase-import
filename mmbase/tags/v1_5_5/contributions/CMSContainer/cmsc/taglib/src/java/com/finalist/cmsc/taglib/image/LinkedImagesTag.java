/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package com.finalist.cmsc.taglib.image;

import java.util.Iterator;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;

import org.apache.commons.lang.StringUtils;

import org.mmbase.bridge.*;
import org.mmbase.bridge.jsp.taglib.NodeReferrerTag;
import org.mmbase.bridge.jsp.taglib.util.Attribute;
import org.mmbase.storage.search.*;
import org.mmbase.util.functions.Function;
import org.mmbase.util.functions.Parameters;
import org.mmbase.util.images.Dimension;

/**
 * Tag that creates a list of related image elements.
 */

@SuppressWarnings("serial")
public class LinkedImagesTag extends NodeReferrerTag {

   private static final String IMAGEREL = "imagerel";

   private Attribute position = Attribute.NULL;

   /** Holds value of property style. */
   private Attribute style = Attribute.NULL;

   /** Holds value of property clazz. */
   private Attribute styleClass = Attribute.NULL;

   /** Holds value of property align. */
   private Attribute align = Attribute.NULL;

   /** Holds value of property border. */
   private Attribute border = Attribute.NULL;

   /** Holds value of property hspace. */
   private Attribute hspace = Attribute.NULL;

   /** Holds value of property vspace. */
   private Attribute vspace = Attribute.NULL;

   /** Holds value of property width. */
   private Attribute width = Attribute.NULL;

   /** Holds value of property height. */
   private Attribute height = Attribute.NULL;

   /** Holds value of property popup. */
   private Attribute popup = Attribute.NULL;

   /** Holds value of property max. */
   private Attribute max = Attribute.NULL;

   /** Holds value of property template. */
   private Attribute template = Attribute.NULL;

   public void setPosition(String position) throws JspTagException {
      this.position = getAttribute(position);
   }


   public void setStyle(String style) throws JspTagException {
      this.style = getAttribute(style);
   }


   public void setStyleClass(String styleClass) throws JspTagException {
      this.styleClass = getAttribute(styleClass);
   }


   public void setAlign(String align) throws JspTagException {
      this.align = getAttribute(align);
   }


   public void setBorder(String border) throws JspTagException {
      this.border = getAttribute(border);
   }


   public void setHspace(String hspace) throws JspTagException {
      this.hspace = getAttribute(hspace);
   }


   public void setVspace(String vspace) throws JspTagException {
      this.vspace = getAttribute(vspace);
   }


   public void setWidth(String width) throws JspTagException {
      this.width = getAttribute(width);
   }


   public void setHeight(String height) throws JspTagException {
      this.height = getAttribute(height);
   }


   public void setMax(String max) throws JspTagException {
      this.max = getAttribute(max);
   }


   public void setPopup(String popup) throws JspTagException {
      this.popup = getAttribute(popup);
   }

   public void setTemplate(String template) throws JspTagException {
      this.template = getAttribute(template);
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
         Node parentNode = getNode();
         helper.useEscaper(false);

         RelationManager imagerelManager = getCloudVar().getRelationManager(IMAGEREL);
         NodeQuery query = imagerelManager.createQuery();
         StepField sf = query.getStepField(imagerelManager.getField("order"));
         query.addSortOrder(sf, SortOrder.ORDER_ASCENDING);

         Field sourceField = imagerelManager.getField("snumber");
         FieldValueConstraint soourceConstraint = query.createConstraint((query.getStepField(sourceField)),
               FieldCompareConstraint.EQUAL, parentNode.getValue("number"));
         query.setConstraint(soourceConstraint);

         NodeList list = imagerelManager.getList(query);

         String pos = position.getString(this);
         if (StringUtils.isNotEmpty(pos)) {
            // filter list on position
            // Other option to filter on position is to add a constraint to the
            // query
            // This is preferred, because the query will be the same for all
            // positions.
            // This results in an optimal usage of the MMBase Query Cache
            for (Iterator<Node> iter = list.iterator(); iter.hasNext();) {
               Node imagerelNode = iter.next();
               if (!pos.equals(imagerelNode.getStringValue("pos"))) {
                  iter.remove();
               }
            }
         }

         int maxSize = max.getInt(this, Integer.MAX_VALUE);
         while (list.size() > maxSize) {
            list.remove(list.size() - 1);
         }

         String outputValue = "";

         for (Iterator<Node> iter = list.iterator(); iter.hasNext();) {
            Node imagerelNode = iter.next();
            if (imagerelNode.isRelation()) {
               Relation imagerel = imagerelNode.toRelation();
               Node image = imagerel.getDestination();

               int width = imagerel.getIntValue("width");
               int height = imagerel.getIntValue("height");
               if (width == -1 && height == -1) {
                  width = image.getIntValue("width");
                  height = image.getIntValue("height");
               }
               String crop = imagerel.getStringValue("crop");
               String legendType = imagerel.getStringValue("legend");
               boolean popup = imagerel.getBooleanValue("popup");
               String template = null;

               if (this.width != Attribute.NULL && this.height != Attribute.NULL) {
                  width = this.width.getInt(this, 0);
                  height = this.height.getInt(this, 0);
               }
               else {
                  if (this.width != Attribute.NULL) {
                     int forcedWidth = this.width.getInt(this, 0);
                     if (width != forcedWidth) {
                        height = height * forcedWidth / width;
                        width = forcedWidth;
                     }
                  }
                  else {
                     if (this.height != Attribute.NULL) {
                        int forcedHeight = this.height.getInt(this, 0);
                        if (height != forcedHeight) {
                           width = width * forcedHeight / height;
                           height = forcedHeight;
                        }
                     }
                  }
               }

               if (this.popup != Attribute.NULL) {
                  popup = this.popup.getBoolean(this, false);
               }

               ImageTag imgTag = new ImageTag();
               imgTag.setPageContext(pageContext);
               // Issue NIJ-149: legendType was not set
               imgTag.setLegendtype(legendType);

               if (this.template != Attribute.NULL) {
                  template = this.template.getString(this);
               }

               imgTag.setExternalAttributes(getOtherAttributes());
               String templateStr = imgTag.getTemplate(image, template, width, height, crop);
               Dimension dim = imgTag.getDimension(image, templateStr);

               Node cachedNode = imgTag.getServletNode(image, templateStr);

               String servletArgument;
               if (cachedNode == null) {
                  cachedNode = image;
                  servletArgument = cachedNode.getStringValue("number");
               }
               else {
                  servletArgument = imgTag.getServletArgument(cachedNode, templateStr);
               }

               String servletPath = getServletPath(imgTag, cachedNode, servletArgument);
               outputValue += imgTag.getOutputValue(image, servletPath, dim, legendType, popup);
            }
         }

         if (outputValue != null) {
            helper.setValue(outputValue);
         }
      }
      catch (BridgeException e) {
         throw new JspException("An exception occurred when retrieving content", e);
      }
      return SKIP_BODY;
   }


   public String getServletPath(ImageTag imgTag, Node node, String servletArgument) throws JspTagException {
      Function servletPathFunction = imgTag.getServletFunction(node);
      Parameters args = imgTag.getServletArguments(servletArgument, servletPathFunction);
      fillStandardParameters(args);
      return servletPathFunction.getFunctionValue(args).toString();
   }


   private String getOtherAttributes() throws JspTagException {
      StringBuffer attributes = new StringBuffer();
      attributes.append((styleClass != Attribute.NULL) ? (" class=\"" + styleClass.getString(this) + "\"") : "");
      attributes.append((style != Attribute.NULL) ? (" style=\"" + style.getString(this) + "\"") : "");
      attributes.append((align != Attribute.NULL) ? (" align=\"" + align.getString(this) + "\"") : "");
      attributes.append((border != Attribute.NULL) ? (" border=\"" + border.getString(this) + "\"") : "");
      attributes.append((hspace != Attribute.NULL) ? (" hspace=\"" + hspace.getString(this) + "\"") : "");
      attributes.append((vspace != Attribute.NULL) ? (" vspace=\"" + vspace.getString(this) + "\"") : "");
      return attributes.toString();
   }


   @Override
   public int doEndTag() throws JspTagException {
      helper.doEndTag();
      return super.doEndTag();
   }

}
