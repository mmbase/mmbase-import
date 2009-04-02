package com.finalist.cmsc.module.glossary.taglib;

import com.finalist.cmsc.module.glossary.Glossary;
import com.finalist.cmsc.module.glossary.GlossaryFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTagSupport;
import java.io.IOException;

public class GlossaryTag extends BodyTagSupport {
   private Log log;


   public GlossaryTag() {
      log = LogFactory.getLog(com.finalist.cmsc.module.glossary.taglib.GlossaryTag.class);
   }


   public int doAfterBody() {
      BodyContent bc = getBodyContent();
      String content = bc.getString();
      Glossary glossary = GlossaryFactory.getGlossary();
      try {
         bc.clear();
         getPreviousOut().write(glossary.mark(content));
      }
      catch (IOException e) {
         log.error("IO Exception when transfer glossary ", e);
      }
      return SKIP_BODY;
   }

}
