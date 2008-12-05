package com.finalist.cmsc.taglib.flash;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

/**
 * This outputs a &lt;script /&gt; with a reference to the swfobject.js library.
 * The source defaults to &lt;context-root&gt;/js/swfobject.js, but you can
 * override that by providing a <code>swfobjectUrl</code> attribute.
 *
 * @author Auke van Leeuwen
 */
public class HeaderJavaScriptTag extends SimpleTagSupport {
   private String swfobjectUrl;

   /** {@inheritDoc} */
   @Override
   public void doTag() throws JspException, IOException {
      getJspContext().getOut().print(String.format("<script type=\"text/javascript\" src=\"%s\"></script>", getSwfobjectUrl()));
   }

   /**
    * Returns the swfobjectUrl.
    *
    * @return the swfobjectUrl
    */
   public String getSwfobjectUrl() {
      if (swfobjectUrl == null) {
         PageContext pageContext = (PageContext) getJspContext();
         HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
         swfobjectUrl = request.getContextPath() + "/js/swfobject.js";
      }

      return swfobjectUrl;
   }

   /**
    * Sets the swfobjectUrl to the specified value.
    *
    * @param swfobjectUrl
    *           the swfobjectUrl to set
    */
   public void setSwfobjectUrl(String swfobjectUrl) {
      this.swfobjectUrl = swfobjectUrl;
   }
}
