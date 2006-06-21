package com.finalist.cmsc.security.forms;

import com.finalist.tree.html.HTMLTreeElement;

/**
 * @author Nico Klasens
 *
 */
public class RoleTreeElement extends HTMLTreeElement {

   private int number;
   private int roleId = -1;
   
   public RoleTreeElement(String icon, String id, String name, String fragment, String style, int number, int roleId) {
      super(icon, id, name, fragment, style);
      this.number = number;
      this.roleId = roleId;
   }
   
   /**
    * @see com.finalist.tree.TreeElement#render(String)
    */
   public String render(String imageBase) {
      String str = super.render(imageBase);
      
      StringBuffer buffer = new StringBuffer();
      buffer.append("<select name=\"role_"+number+"\" class='input.select'>");
      renderOption(buffer,"-1","-", roleId==-1);
      renderOption(buffer,"0","GEEN", roleId==0);
      renderOption(buffer,"1","Schrijver", roleId==1);
      renderOption(buffer,"2","Redacteur", roleId==2);
      renderOption(buffer,"3","Eindredacteur", roleId==3);
      renderOption(buffer,"100","Webmaster", roleId==100);
      buffer.append("</select>");
      
      return str + buffer.toString();
   }
   
   private void renderOption(StringBuffer buffer, String value, String text, boolean selected) {
      buffer.append("<option value='"+value+"'");
      if (selected) {
         buffer.append(" selected='selected'");
      }
      buffer.append(">"+text+"</option>");      
   }
}
