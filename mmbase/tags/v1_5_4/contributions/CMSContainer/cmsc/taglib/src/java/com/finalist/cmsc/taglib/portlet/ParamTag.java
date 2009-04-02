package com.finalist.cmsc.taglib.portlet;

import javax.portlet.PortletURL;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

/**
 * * Supporting class for the <CODE>param</CODE> tag. * defines a parameter
 * that can be added to a <CODE>actionURL</CODE> or * a <CODE>renderURL</CODE> *
 * <BR>
 * The following attributes are mandatory *
 * <UL> *
 * <LI><CODE>name</CODE> *
 * <LI><CODE>value</CODE> *
 * </UL>
 */
public class ParamTag extends TagSupport {

   private String name;
   private String value;


   /**
    * Processes the <CODE>param</CODE> tag.
    * 
    * @return <CODE>SKIP_BODY</CODE>
    */
   @Override
   public int doStartTag() throws JspException {
      BasicURLTag urlTag = (BasicURLTag) findAncestorWithClass(this, BasicURLTag.class);
      if (urlTag == null) {
         throw new JspException("the 'param' Tag must have actionURL or renderURL as a parent");
      }
      PortletURL url = urlTag.getUrl();

      if (url != null && getName() != null) {
         url.setParameter(getName(), getValue());
      }

      return SKIP_BODY;
   }


   /**
    * Returns the name.
    * 
    * @return String
    */
   public String getName() {
      return name;
   }


   /**
    * Returns the value.
    * 
    * @return String
    */
   public String getValue() {
      if (value == null) {
         value = "";
      }
      return value;
   }


   /**
    * Sets the name.
    * 
    * @param name
    *           The name to set
    */
   public void setName(String name) {
      this.name = name;
   }


   /**
    * Sets the value.
    * 
    * @param value
    *           The value to set
    */
   public void setValue(String value) {
      this.value = value;
   }

}
