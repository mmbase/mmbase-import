package com.finalist.cmsc.taglib.flash;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.jsp.JspException;

/**
 * Handles the &lt;flashvar /&gt; tag. You can use this tag with either a name and
 * a value attribute or you can only specify a name attribute and specify the
 * value in the body of this tag. If you use both a {@link JspException} will be
 * thrown.
 *
 * @author Auke van Leeuwen
 */
public class FlashVarTag extends NameValueTag {
   private String urlEncodingScheme = System.getProperty("file.encoding", "utf-8");
   private boolean urlEncode = true;

   /** {@inheritDoc} */
   @Override
   protected Map<String, List<String>> initAllowedValues() {
      return null;
   }

   /** {@inheritDoc} */
   @Override
   public void doTag() throws JspException, IOException {
      super.doTag();

      FlashTag flashTag = (FlashTag) findAncestorWithClass(this, FlashTag.class);
      if (flashTag == null) {
         throw new JspException("A flashvar tag should be nested inside a flash tag!");
      }

      if (!isAllowedValue(getName(), getValue())) {
         throw new JspException(String.format("Invalid value '%s' for '%s'!", getValue(), getName()));
      }

      flashTag.addFlashvar(getName(), getValue(), getUrlEncode(), getUrlEncodingScheme());
   }

   /**
    * Returns the urlEncodingScheme.
    *
    * @return the urlEncodingScheme
    */
   public String getUrlEncodingScheme() {
      return urlEncodingScheme;
   }

   /**
    * Sets the urlEncodingScheme to the specified value.
    *
    * @param urlEncodingScheme
    *           the urlEncodingScheme to set
    */
   public void setUrlEncodingScheme(String urlEncodingScheme) {
      this.urlEncodingScheme = urlEncodingScheme;
   }

   /**
    * Returns the urlEncode.
    *
    * @return the urlEncode
    */
   public boolean getUrlEncode() {
      return urlEncode;
   }

   /**
    * Sets the urlEncode to the specified value.
    *
    * @param urlEncode
    *           the urlEncode to set
    */
   public void setUrlEncode(boolean urlEncode) {
      this.urlEncode = urlEncode;
   }
}