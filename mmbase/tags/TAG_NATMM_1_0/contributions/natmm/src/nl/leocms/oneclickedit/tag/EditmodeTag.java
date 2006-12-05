/*
 * Version: MPL 1.1/GPL 2.0/LGPL 2.1
 *
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Original Code is LeoCMS.
 *
 * The Initial Developer of the Original Code is
 * 'De Gemeente Leeuwarden' (The dutch municipality Leeuwarden).
 *
 * See license.txt in the root of the LeoCMS directory for the full license.
 */
package nl.leocms.oneclickedit.tag;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import nl.leocms.oneclickedit.OneClickEditUtils;

import org.mmbase.util.logging.Logging;
import org.mmbase.util.logging.Logger;

/**
 * @author Gerard van de Weerd
 *
 * This class implements the tag support to provide custom behaviour for
 * the leocms website:
 * <ul>
 * <li>check if the edit mode is set
 * <li>
 */
public class EditmodeTag extends TagSupport {

   private static final Logger log = Logging.getLoggerInstance(EditmodeTag.class);

   private boolean inverse;


   /* (non-Javadoc)
       * @see javax.servlet.jsp.tagext.Tag#doStartTag()
       */
   public int doStartTag() throws JspException {
      //pageContext.getServletContext()

      int returnValue = SKIP_BODY;

      ServletRequest request = pageContext.getRequest();
      String contextPath = "";
      if (request instanceof HttpServletRequest) {
         if (OneClickEditUtils.isEditMode((HttpServletRequest) request) ^ inverse) {
            returnValue = EVAL_BODY_INCLUDE;
         }
      }

      return returnValue;
   }


   /* (non-Javadoc)
    * @see javax.servlet.jsp.tagext.Tag#doEndTag()
    */
   public int doEndTag() throws JspException {
      // TODO Auto-generated method stub
      return super.doEndTag();
   }


   /**
    * @return
    */
   public boolean isInverse() {
      return inverse;
   }


   /**
    * @param b
    */
   public void setInverse(boolean b) {
      inverse = b;
   }

}
