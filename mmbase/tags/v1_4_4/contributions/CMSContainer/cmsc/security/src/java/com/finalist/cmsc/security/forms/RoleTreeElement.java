package com.finalist.cmsc.security.forms;

import javax.servlet.http.HttpServletRequest;

import com.finalist.cmsc.util.bundles.JstlUtil;
import com.finalist.tree.html.HTMLTreeElement;

/**
 * @author Nico Klasens
 */
public class RoleTreeElement extends HTMLTreeElement {

   private HttpServletRequest request;
   private int number;
   private int roleId = -1;


   public RoleTreeElement(HttpServletRequest request, String icon, String id, String name, String fragment,
         String style, int number, int roleId) {
      super(icon, id, name, fragment, style);
      this.request = request;
      this.number = number;
      this.roleId = roleId;
   }


   /**
    * @see com.finalist.tree.TreeElement#render(String)
    */
   @Override
   public String render(String imageBase) {
      String str = super.render(imageBase);

      StringBuffer buffer = new StringBuffer();
      buffer.append("<select name=\"role_" + number + "\" class='input.select'>");
      renderOption(buffer, "-1", JstlUtil.getMessage(request, "role.inherit"), roleId == -1);
      renderOption(buffer, "0", JstlUtil.getMessage(request, "role.none"), roleId == 0);
      renderOption(buffer, "1", JstlUtil.getMessage(request, "role.writer"), roleId == 1);
      renderOption(buffer, "2", JstlUtil.getMessage(request, "role.editor"), roleId == 2);
      renderOption(buffer, "3", JstlUtil.getMessage(request, "role.chiefeditor"), roleId == 3);
      renderOption(buffer, "100", JstlUtil.getMessage(request, "role.webmaster"), roleId == 100);
      buffer.append("</select>");

      return str + buffer.toString();
   }


   private void renderOption(StringBuffer buffer, String value, String text, boolean selected) {
      buffer.append("<option value='" + value + "'");
      if (selected) {
         buffer.append(" selected='selected'");
      }
      buffer.append(">" + text + "</option>");
   }
}
