package com.finalist.cmsc.taglib.navigation;

import java.io.IOException;
import java.io.StringWriter;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.JspFragment;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A tag to make a param for the surrounding link tag.
 * 
 * @author Wouter Heijke
 * @version $Revision: 1.3 $
 */
public class LinkParamTag extends SimpleTagSupport {
   private static Log log = LogFactory.getLog(LinkParamTag.class);

   /**
    * param name
    */
   private String name;

   /**
    * param value
    */
   private String value;


   /**
    * @param name
    *           The name to set.
    */
   public void setName(String name) {
      this.name = name;
   }


   /**
    * @param value
    *           The value to set.
    */
   public void setValue(String value) {
      this.value = value;
   }


   /**
    * Create and write a param for the parent link tag.
    */
   @Override
   public void doTag() throws JspException, IOException {
      log.debug("LinkParamTag");
      LinkTag container = (LinkTag) findAncestorWithClass(this, LinkTag.class);
      if (container != null) {
         // handle body, call any nested tags
         JspFragment body = getJspBody();
         if (body != null) {
            StringWriter buffer = new StringWriter();
            body.invoke(buffer);
            value = buffer.toString();
         }
         container.addParam(name, value);
      }
      else {
         throw new JspException("Couldn't find link tag");
      }
   }
}