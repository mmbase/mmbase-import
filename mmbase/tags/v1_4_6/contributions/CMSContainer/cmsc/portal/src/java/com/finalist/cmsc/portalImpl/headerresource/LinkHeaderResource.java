package com.finalist.cmsc.portalImpl.headerresource;

/**
 * <link rel="rel" href="href">
 * 
 * @author freek
 */
public class LinkHeaderResource extends HeaderResource {

   private String rel;
   private String href;
   private String type;


   public LinkHeaderResource(boolean dublin, String rel, String href, String type) {
      super(dublin);
      this.rel = rel;
      this.href = href;
      this.type = type;
   }


   public void render(StringBuffer buffer) {
      buffer.append("<link rel=\"");
      buffer.append(rel);
      buffer.append("\" href=\"");
      buffer.append(href);
      buffer.append("\"");
      if (type != null) {
         buffer.append(" type=\"");
         buffer.append(type);
         buffer.append("\"");
      }
      buffer.append("/>");
   }


   public boolean equals(Object o) {
      if (o == null) {
         return false;
      }
      if (o instanceof LinkHeaderResource) {
         return ((LinkHeaderResource) o).rel.equals(rel);
      }
      return false;
   }


   public int hashCode() {
      return rel.hashCode();
   }


   public String toString() {
      return "link_" + href;
   }
}
