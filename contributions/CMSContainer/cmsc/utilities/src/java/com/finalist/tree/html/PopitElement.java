package com.finalist.tree.html;

/**
 * @author Nico Klasens
 */
public class PopitElement extends HTMLTreeElement {

   public PopitElement(String icon, String id, String name, String fragment) {
      super(icon, id, name, fragment);
   }


   public PopitElement(String icon, String id, String name, String fragment, String style) {
      super(icon, id, name, fragment, style);
   }


   public PopitElement(String icon, String id, String name, String fragment, String style, String link) {
      super(icon, id, name, fragment, style, link);
   }


   public PopitElement(String icon, String id, String name, String fragment, String style, String link, String target) {
      super(icon, id, name, fragment, style, link, target);
   }


   @Override
   protected String renderOptions(String imageBase) {
      StringBuffer buffer = new StringBuffer();
      buffer.append("<div id=\"popmenu_" + id + "\" class=\"treeskin\">");
      for (int count = 0; count < options.size(); count++) {
         buffer.append("<div class=\"menuitems\">");
         String optionString = ((HTMLTreeOption) options.get(count)).render(imageBase);
         buffer.append(optionString);
         buffer.append("</div>");
      }
      buffer.append("</div>");
      return buffer.toString();
   }

}
