package com.finalist.cmsc.basicmodel.taglib;

import net.sf.mmapps.commons.util.StringUtil;

import org.mmbase.bridge.Node;
import org.mmbase.bridge.jsp.taglib.util.Attribute;

import org.mmbase.util.images.Dimension;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;


import javax.servlet.jsp.JspTagException;

/**
 * This tag renders an html image tag to display an image from MMBase.
 *
 * @author Hillebrand Gelderblom
 * @version $Revision: 1.1 $, $Date: 2006-06-12 13:08:48 $
 */
public class ImageTag extends org.mmbase.bridge.jsp.taglib.ImageTag {
   /** URL to the jsp used to display the image. */
   private final static String IMAGE_POPUP_URL = "imagePopup.jsp";
   
   /** Default title of the image which is used when the title of the image cannot be retrieved. */
   private final static String IMAGE_POPUP_TITLE = "Afbeelding";
   
   /** Features of the popup window.  */
   private final static String IMAGE_POPUP_FEATURES = "toolbar=no,location=no,directories=no,status=no,menubar=no,resizable=yes,scrollbars=yes";
   
   /** Constant that indicates that the legend shoud be displayed under the image. */
   public final static int LEGEND_UNDER = 0;

   /** Constant that indicates that the legend shoud be displayed above the image. */
   public final static int LEGEND_ABOVE = 1;

   /** Constans that indicates that the legend should be displayed as an alternative text */
   public final static int LEGEND_ALT = 2;

   
   /** The logger. */
   private static Logger log = Logging.getLoggerInstance(ImageTag.class);
   
   /** Holds value of property popup. */
   private Attribute popup = Attribute.NULL;
   
   /** Holds value of property legendtype. */
   private Attribute legendType = Attribute.NULL;

   private String externalAttributes;
   
   
    /**
     * @see org.mmbase.bridge.jsp.taglib.ImageTag#getOutputValue(int, org.mmbase.bridge.Node, java.lang.String, org.mmbase.util.images.Dimension)
     */
    public String getOutputValue(int mode, Node node, String servletPath, Dimension dim) throws JspTagException {
        return getOutputValue(node, servletPath, dim, getLegendtype(), isPopup());
    }

    /**
     * Method that renders the html image tag to display an image from MMBase. This tag 
     * displays a legend.
     * @param node image node
     * @param servletPath path to servlet
     * @param dim preferred dimensions of image
     * @param legendtype position of legend
     * @param popup generate popuplink
     * @return html image tag
     * @throws JspTagException
     */
    public String getOutputValue(Node node, String servletPath, Dimension dim, int legendtype, boolean popup) throws JspTagException {
        StringBuffer sb = new StringBuffer();
        if (legendtype == LEGEND_ABOVE) {
           sb.append(getLegend(node, "img-txt-up"));
        }
        sb.append(super.getOutputValue(MODE_HTML_IMG, node, servletPath, dim));
    
        if (legendtype == LEGEND_UNDER) {
           sb.append(getLegend(node, "img-txt-down"));
        }
          
        String imgHtml = sb.toString();
        if (popup) {
            imgHtml = addPopup(node, imgHtml, getDimension(node, null));
        }
        return imgHtml;
    }
   
    protected String getOtherAttributes() throws JspTagException {
        if (externalAttributes == null) {
            super.getOtherAttributes();
        }
        return externalAttributes;
    }
    
   /**
    * Retrieves html code to display a legend of an image.
    * @param image - image for which the legend should be displayed
    * @param style - legend style
    * @return html - code for displaying legend
    */
   private static String getLegend(Node image, String style) {
      StringBuffer legend = new StringBuffer();

      try {
         String descr = image.getStringValue("description");
         if ((descr != null) && (descr.trim() != "")) {
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

   public String getAltAttribute(Node arg0) throws JspTagException {
        if (getLegendtype() == LEGEND_ALT) {
            return super.getAltAttribute(arg0); 
        }
        return "";
    }
   
   /**
     * Adds html code to popup this image
     * 
     * @param image - image to display in the popup
     * @param imgHtml - html to display the image which will be surrounded by the popup code
     * @param dimension - dimension og original image 
     * @return if this image should popup, imgHtml surrounded by popup code else unmodified imgHtml
     */
   private String addPopup(Node image, String imgHtml, Dimension dimension) {
      log.debug("This image should popup is " + popup);
      StringBuffer sb = new StringBuffer();
      sb.append("<a href=\"#\" onclick=\"javascript:handle = window.open('");
      sb.append(IMAGE_POPUP_URL);
      sb.append("?nodenumber=" + image.getNumber() + "' , '");
      String title = image.getStringValue("title");
      sb.append(!StringUtil.isEmpty(title) ? title: IMAGE_POPUP_TITLE);
      sb.append("', '");
      sb.append(IMAGE_POPUP_FEATURES);
      sb.append(",width=" + (dimension.getWidth() * 2));
      sb.append(",height=" + (dimension.getHeight() * 2));
      sb.append("');handle.focus();return false;\">\n");
      sb.append(imgHtml);
      sb.append("\n</a>\n");
      
      return sb.toString();
   }
   
   /**
    * Getter for property popup.
    * @return Value of property popup.
    * @throws JspTagException 
    */
   public boolean isPopup() throws JspTagException {
      return this.popup.getBoolean(this, false);
   }
   
   /**
    * Setter for property popup.
    * @param popup New value of property popup.
    * @throws JspTagException 
    */
   public void setPopup(String popup) throws JspTagException {
      this.popup = getAttribute(popup);
   }
   
   /**
    * Getter for property legendtype.
    * @return Value of property legendtype.
    * @throws JspTagException 
    */
   public int getLegendtype() throws JspTagException {
      return legendType.getInt(this, -1);
   }
   
   /**
    * Setter for property legendtype.
    * @param legendType New value of property legendtype.
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
   
}
