package com.finalist.cmsc.portalImpl.headerresource;

import com.finalist.cmsc.util.XmlUtil;

public class MetaHeaderResource extends HeaderResource {

   /**
    * <meta name="name" content="content" lang="lang" http-equiv="httpEquiv"/>
    */

   private String name;
   private String content;
   private String lang;
   private String httpEquiv;


   public MetaHeaderResource(boolean dublin, String name, String content, String lang, String httpEquiv) {
      super(dublin);
      this.name = name;
      this.content = content;
      this.lang = lang;
      this.httpEquiv = httpEquiv;
   }


   public MetaHeaderResource(boolean dublin, String name, String content) {
      this(dublin, name, content, null, null);
   }


   @Override
   public void render(StringBuffer buffer) {
      if (content != null && content.length() > 0) {
         buffer.append("<meta name=\"");
         if (isDublin()) {
            buffer.append("DC.");
         }
         buffer.append(name);
         buffer.append("\" content=\"");
         String contentStr = "";
         contentStr = content.replaceAll("\"", "");
         contentStr = XmlUtil.xmlEscape(contentStr);
         buffer.append(contentStr);
         buffer.append("\"");
         if (lang != null) {
            buffer.append(" lang=\"");
            buffer.append(lang);
            buffer.append("\"");
         }
         if (httpEquiv != null) {
            buffer.append(" http-equiv=\"");
            buffer.append(httpEquiv);
            buffer.append("\"");
         }
         buffer.append("/>");
      }
   }


   @Override
   public String toString() {
      return "meta_" + (isDublin() ? "DC." : ".") + name;
   }
}
