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
package nl.leocms.rubrieken;

import java.io.PrintWriter;
import java.util.Map;

import nl.leocms.authorization.UserRole;
import nl.leocms.authorization.Roles;

import org.mmbase.bridge.Node;

import nl.leocms.rubrieken.RubriekTreeModel;
import com.finalist.tree.TreeCellRenderer;
import com.finalist.tree.TreeCellRendererAdapter;

/**
 * Renderer voor de rubrieken editor. Kijkt naar de rechten van de gebruiker of
 * wijzigen/ordenen mogelijk is.
 * 
 * @author Edwin van der Elst Date :Sep 15, 2003
 *  
 */
public class RubriekRenderer extends TreeCellRendererAdapter implements TreeCellRenderer {

   private Map roles;
   private RubriekTreeModel model;
   /*
	 * Render een node van de tree.
	 * 
	 * @see com.finalist.tree.TreeCellRenderer#render(java.lang.Object,
	 *      java.io.PrintWriter)
	 */
   public void render(Object node, PrintWriter out) {
      Node n = (Node) node;
      UserRole role = (UserRole) roles.get(new Integer(n.getNumber()));
      
      int level = n.getIntValue("level");
      
      out.print("<span style='width:200px; white-space: nowrap'>");
      
      if(level<1) {
         
         out.print(n.getStringValue("naam"));
      
      } else {
         
         if (role.getRol() >= Roles.EINDREDACTEUR) {
            out.print("<a href='RubriekInitAction.eb?number=" + n.getNumber() + "'  target='bottompane'>" + n.getStringValue("naam") + "<a/>");
         } else if (role.getRol() >= Roles.SCHRIJVER) {
            out.print(n.getStringValue("naam"));
         } else {
            out.println("<font class='notactive'>" + n.getStringValue("naam") + "</font>");
         }
         
         if (role.getRol() >= Roles.SCHRIJVER) {
            String url = "/mmbase/edit/wizard/jsp/wizard.jsp?wizard=config/rubriek/rubriek&nodepath=rubriek&referrer=/editors/empty.html&objectnumber=" + n.getNumber();
            out.println("<a href='" + url + "' target='workpane'><img src='../img/edit_w.gif' border='0' align='top' title='Bewerk rubriek'/></a>");
            
         }
         if (role.getRol() >= Roles.EINDREDACTEUR) {
            
            out.print("<a href=\"RubriekInitAction.eb?parent=" + n.getNumber() + "\"  target='bottompane'>");
            out.print("<img src='../img/new_rubriek.gif' border='0' align='top' title='Nieuwe subrubriek'/>");
            out.print("</a>&nbsp;");
            
            /* hh: both rubrieken and pages are reordered in the page tree
            if (model.getChildCount(n) > 1) {
               out.print("<a href=\"reorder.jsp?parent=" + n.getNumber() + "\"  target='bottompane'>");
               out.println("<img src='../img/reorder.gif' border='0' align='top' title='Volgorde subrubrieken wijzigen'/>");
               out.print("</a>");
            }
            */
            
            if ((model.getChildCount(n) == 0) && (level > 0)) {
               if (role.getRol() >= Roles.EINDREDACTEUR) {
                  out.print("&nbsp;<a href=\"delete_rubriek.jsp?number=" + n.getNumber() + "\" onclick=\"openPopupWindow('delete_rubriek', 500, 300)\""+ " target=\"delete_rubriek\"" +"\">");
                  out.println("<img src='../img/remove.gif' border='0' align='top' title='Verwijder rubriek'/>");
                  out.print("</a>");
               }
            }
         }
      }
      out.print("</span>");
   }

   public String getIcon(Object node) {
      return "rubriek.gif";
   }

   /**
    * @param model - reference to the RubriekTreeModel for this tree 
    * @param roles - Map of UserRoles for the current user
	 */
   public RubriekRenderer(RubriekTreeModel model, Map roles) {
      super();
      this.model = model;
      this.roles = roles;
   }
}
