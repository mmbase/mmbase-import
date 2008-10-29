package com.finalist.cmsc.taglib.image;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspTagException;

import org.apache.commons.lang.StringUtils;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.jsp.taglib.util.Attribute;
import org.mmbase.util.images.Dimension;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * This tag renders an html image tag to display an image from MMBase.
 *
 * @author Hillebrand Gelderblom
 */
@SuppressWarnings("serial")
public class ImageTag extends org.mmbase.bridge.jsp.taglib.ImageTag {

   /** URL to the jsp used to display the image. */
   private final static String IMAGE_POPUP_URL = "imagePopup.jsp";

   /**
    * Default title of the image which is used when the title of the image
    * cannot be retrieved.
    */
   private final static String IMAGE_POPUP_WINDOW = "imagePopupWindow";

   /** Features of the popup window. */
   private final static String IMAGE_POPUP_FEATURES = "toolbar=no,location=no,directories=no,status=no,menubar=no,resizable=yes,scrollbars=yes";

   /** Constant that indicates that the legend shoud not be displayed. */
   public static final String LEGEND_NONE = "none";

   /**
    * Constant that indicates that the legend shoud be displayed under the
    * image.
    */
   public final static String LEGEND_BOTTOM = "bottom";

   /**
    * Constant that indicates that the legend shoud be displayed above the
    * image.
    */
   public final static String LEGEND_TOP = "top";

   /**
    * Constans that indicates that the legend should be displayed as an
    * alternative text
    */
   public final static String LEGEND_ALT = "alt";

   /** The logger. */
   private static final Logger log = Logging.getLoggerInstance(ImageTag.class);

   /** Holds value of property popup. */
   private Attribute popup = Attribute.NULL;

   /** Holds value of property legendtype. */
   private Attribute legendType = Attribute.NULL;

   private String externalAttributes;


   /**
    * @see org.mmbase.bridge.jsp.taglib.ImageTag#getOutputValue(int,
    *      org.mmbase.bridge.Node, java.lang.String,
    *      org.mmbase.util.images.Dimension)
    */
   @Override
   public String getOutputValue(int mode, Node node, String servletPath, Dimension dim) throws JspTagException {
      return getOutputValue(node, servletPath, dim, getLegendtype(), isPopup());
   }


   /**
    * Method that renders the html image tag to display an image from MMBase.
    * This tag displays a legend.
    *
    * @param node
    *           image node
    * @param servletPath
    *           path to servlet
    * @param dim
    *           preferred dimensions of image
    * @param legendtype
    *           position of legend
    * @param popup
    *           generate popuplink
    * @return html image tag
    * @throws JspTagException
    */
   public String getOutputValue(Node node, String servletPath, Dimension dim, String legendtype, boolean popup)
         throws JspTagException {
      StringBuffer sb = new StringBuffer();
      if (LEGEND_TOP.equals(legendtype)) {
         sb.append(getLegend(node, "img-txt-up"));
      }
      sb.append(super.getOutputValue(MODE_HTML_IMG, node, servletPath, dim));

      if (LEGEND_BOTTOM.equals(legendtype)) {
         sb.append(getLegend(node, "img-txt-down"));
      }

      String imgHtml = sb.toString();
      if (popup) {
         imgHtml = addPopup(node, imgHtml, getDimension(node, null));
      }
      return imgHtml;
   }


   @Override
   protected String getOtherAttributes() throws JspTagException {
      if (externalAttributes == null) {
         return super.getOtherAttributes();
      }
      return externalAttributes;
   }


   /**
    * Retrieves html code to display a legend of an image.
    *
    * @param image -
    *           image for which the legend should be displayed
    * @param style -
    *           legend style
    * @return html - code for displaying legend
    */
   private static String getLegend(Node image, String style) {
      StringBuffer legend = new StringBuffer();

      try {
         String descr = image.getStringValue("description");
         if (StringUtils.isNotBlank(descr)) {
            legend.append("<span class=\"" + style + "\">");
            legend.append(descr.trim());
            legend.append("</span>\n");
         }
      }
      catch (Exception e) {
         log.debug("An exception has occured while retrieving the legend. No legend will be rendered.");
      }
      return legend.toString();
   }


   @Override
   public String getAltAttribute(Node node) {
      // Issue NIJ-149: description cannot use multiple lines when used for alt
      // tag
      String alt = findAltAttribute(node);
      if (alt == null || "".equals(alt)) {
         return " alt=\"\"";
      }
      alt = org.mmbase.util.transformers.Xml.XMLAttributeEscape(alt, '\"');
      alt = org.mmbase.util.transformers.Xml.XMLEscape(alt);
      return " alt=\"" + alt + "\" title=\"" + alt + "\"";
   }


   /** Finds the alt attribute to use */
   private String findAltAttribute(Node node) {
      // only use description if this option is selected
      // this an ugly quick fix for nijmegen because of the upcoming deathline
      // make a better to pick which field to use in the alt
      // if (LEGEND_ALT.equals(getLegendtype())) {
      if (node.getNodeManager().hasField("description")) {
         return replaceLineFeeds(node.getStringValue("description"), " ");
      }
      // }
      // try another one, best match first
      if (node.getNodeManager().hasField("alt")) {
         return node.getStringValue("alt");
      }
      if (node.getNodeManager().hasField("title")) {
         return node.getStringValue("title");
      }
      if (node.getNodeManager().hasField("name")) {
         return node.getStringValue("name");
      }
      return null;
   }


   /**
    * Adds html code to popup this image
    *
    * @param image -
    *           image to display in the popup
    * @param imgHtml -
    *           html to display the image which will be surrounded by the popup
    *           code
    * @param dimension -
    *           dimension og original image
    * @return if this image should popup, imgHtml surrounded by popup code else
    *         unmodified imgHtml
    */
   private String addPopup(Node image, String imgHtml, Dimension dimension) {
      log.debug("This image should popup is " + popup);

      HttpServletRequest req = (HttpServletRequest) pageContext.getRequest();

      // add some pixels for the scrollbar
      int popupWidth = dimension.getWidth() + 20;
      int popupHeight = (dimension.getHeight() + 20);

      StringBuffer sb = new StringBuffer();
      sb.append("<a href=\"#\" onclick=\"javascript:handle = window.open('");
      sb.append(req.getContextPath() + "/" + IMAGE_POPUP_URL);
      sb.append("?nodenumber=" + image.getNumber() + "' , '");
      sb.append(IMAGE_POPUP_WINDOW);
      sb.append("', '");
      sb.append(IMAGE_POPUP_FEATURES);
      sb.append(",width=" + Math.min(popupWidth, 1011));
      sb.append(",height=" + Math.min(popupHeight, 672));
      sb.append("');handle.focus();return false;\">\n");
      sb.append(imgHtml);
      sb.append("\n</a>\n");

      return sb.toString();
   }


   /**
    * Getter for property popup.
    *
    * @return Value of property popup.
    * @throws JspTagException
    */
   private boolean isPopup() throws JspTagException {
      return this.popup.getBoolean(this, false);
   }


   /**
    * Setter for property popup.
    *
    * @param popup
    *           New value of property popup.
    * @throws JspTagException
    */
   public void setPopup(String popup) throws JspTagException {
      this.popup = getAttribute(popup);
   }


   /**
    * Getter for property legendtype.
    *
    * @return Value of property legendtype.
    * @throws JspTagException
    */
   public String getLegendtype() throws JspTagException {
      String temp = legendType.getString(this);
      return StringUtils.isEmpty(temp) ? LEGEND_NONE : temp;
   }


   /**
    * Setter for property legendtype.
    *
    * @param legendType
    *           New value of property legendtype.
    * @throws JspTagException
    */
   public void setLegendtype(String legendType) throws JspTagException {
      this.legendType = getAttribute(legendType);
   }


   public String getExternalAttributes() {
      return externalAttributes;
   }


   public void setExternalAttributes(String externalAttributes) {
      this.externalAttributes = externalAttributes;
   }


   /**
    * Replaces all carriage returns and/or linefeeds in a <code>str</code>
    * with <code>replacement</code>. Multiple consecutive CR's and/or LF's
    * are replaced only once. TODO put this in a utility class
    */
   private String replaceLineFeeds(String str, String replacement) {
      return str.replaceAll("[\\r\\n]+", replacement);
   }


   @Override
   public Node getServletNode(Node node, String template) {
      return super.getServletNode(node, template);
   }
}
