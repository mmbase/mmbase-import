package com.finalist.cmsc.portalImpl.headerresource;

public class ScriptHeaderResource extends HeaderResource {

   public static final String JAVASCRIPT = "text/javascript";

   private String type;
   private String src;
   private String script;


   public ScriptHeaderResource(String type, String src, String script) {
      super(false);
      this.type = type;
      this.src = src;
      this.script = script;
   }


   public void render(StringBuffer buffer) {
      buffer.append("<script type=\"");
      buffer.append(type);
      buffer.append("\"");
      if (src != null) {
         buffer.append(" src=\"");
         buffer.append(src);
         buffer.append("\"");
      }
      buffer.append(">");
      if (script != null) {
         buffer.append(script);
      }
      buffer.append("</script>");
   }


   public String toString() {
      return "script_" + type + "_" + ((script == null) ? src : script);
   }

}
