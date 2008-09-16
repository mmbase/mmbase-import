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
package nl.leocms.authorization.forms;

import java.io.PrintWriter;
import java.util.Map;

import nl.leocms.authorization.UserRole;

import org.mmbase.bridge.Node;

import com.finalist.tree.TreeCellRenderer;
import com.finalist.tree.TreeCellRendererAdapter;

/**
 * @author Edwin van der Elst
 * Date :Sep 15, 2003
 * 
 */
public class FormRenderer extends TreeCellRendererAdapter implements TreeCellRenderer {

   private Map roles;

   /* (non-Javadoc)
    * @see com.finalist.tree.TreeCellRenderer#render(java.lang.Object, java.io.PrintWriter)
    */
   public void render(Object node, PrintWriter out) {
      out.println("<span style='width: 90px; white-space: nowrap; overflow: clip;'>" + ((Node) node).getFieldValue("naam") + "</span>");
      Node rubriek = (Node) node;
      Integer rubriekNumber = new Integer(rubriek.getNumber());
      UserRole userRole = (UserRole) roles.get(rubriekNumber);
      int val;
      if (userRole==null || userRole.isInherited()) {
         val = -1;
      } else {
         val = userRole.getRol();
      }

      out.println("<select name=\"rol_"+rubriekNumber+"\" class='input.select'>");
      renderOption(out,"-1","-", val==-1);
      renderOption(out,"0","Lezer", val==0);
      renderOption(out,"1","Schrijver", val==1);
      renderOption(out,"2","Redacteur", val==2);
      renderOption(out,"3","Eindredacteur", val==3);
      renderOption(out,"100","Webmaster", val==100);
      out.println("</select>");
   }
   
   private void renderOption(PrintWriter out,String value, String tekst, boolean selected) {
      out.print("<option value='"+value+"'");
      if (selected) {
         out.print(" selected");
      }
      out.println(">"+tekst+"</option>");      
   }
   
   /**
    * @param roles
    */
   public FormRenderer(Map roles) {
      super();
      this.roles = roles;
   }
}
