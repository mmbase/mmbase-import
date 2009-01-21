package com.finalist.cmsc.taglib.editors;

import java.io.IOException;
import java.util.ResourceBundle;

import javax.portlet.RenderRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.jstl.core.Config;
import javax.servlet.jsp.jstl.fmt.LocalizationContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@SuppressWarnings("serial")
public class EditorMessageTag extends org.apache.taglibs.standard.tag.rt.fmt.MessageTag {

   static Log log = LogFactory.getLog(EditorMessageTag.class);


   @Override
   public int doEndTag() throws JspException {

      RenderRequest renderRequest = (RenderRequest) pageContext.getRequest().getAttribute("javax.portlet.request");
      LocalizationContext ctx = (LocalizationContext) renderRequest.getAttribute(Config.FMT_LOCALIZATION_CONTEXT
            + ".request.editors");
      if (ctx == null) {
         ctx = (LocalizationContext) renderRequest.getAttribute(Config.FMT_LOCALIZATION_CONTEXT + ".request");
      }

      ResourceBundle bundle = ctx.getResourceBundle();

      try {
         pageContext.getOut().print(bundle.getString(keyAttrValue));
      }
      catch (IOException ioe) {
         throw new JspTagException(ioe.toString(), ioe);
      }

      return EVAL_PAGE;
   }
}
