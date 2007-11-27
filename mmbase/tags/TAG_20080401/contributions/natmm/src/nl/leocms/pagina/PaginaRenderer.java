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
package nl.leocms.pagina;

import java.io.PrintWriter;
import java.util.Map;

import nl.leocms.authorization.UserRole;
import nl.leocms.authorization.Roles;
import nl.leocms.authorization.AuthorizationHelper;

import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;

import com.finalist.tree.TreeCellRenderer;
import com.finalist.tree.TreeCellRendererAdapter;

/**
 * Renderer voor de pagina editor. Kijkt naar de rechten van de gebruiker of
 * wijzigen/ordenen mogelijk is.
 * 
 * @author Gerard van de Weerd Date :Oct 20, 2003
 *  
 */
public class PaginaRenderer extends TreeCellRendererAdapter implements TreeCellRenderer {

   private static final String RUBRIEK_NODE_MANAGER = "rubriek";
   private Map roles;
   private PaginaTreeModel model;
   private String username;
   private Cloud cloud;
   
   /*
	 * Render een node van de tree.
	 * 
	 * @see com.finalist.tree.TreeCellRenderer#render(java.lang.Object,
	 *      java.io.PrintWriter)
	 */
  public void render(Object node, PrintWriter out) {
      Node n = (Node) node;
      String typedef = n.getNodeManager().getName();
      // write the rubrieken
      if (PaginaRenderer.RUBRIEK_NODE_MANAGER.equals(typedef)) {                  
         out.println("<span style='width:100px; white-space: nowrap'>" + n.getStringValue("naam") + "</span>");
         UserRole role = (UserRole) roles.get(new Integer(n.getNumber()));         
         if (role.getRol() >= Roles.SCHRIJVER) {            
            out.print("<a href=\"PaginaInitAction.eb?parent=" + n.getNumber() + "\">");
            out.print("<img src='../img/new_pagina.gif' border='0' align='top' title='Nieuwe pagina'/>");
            out.print("</a>&nbsp;");                              
            if (model.getPageCount(n) > 1) {
               out.print("<a href=\"reorder_pagina.jsp?parent=" + n.getNumber() + "\">");
               out.println("<img src='../img/reorder.gif' border='0' align='top' title='Volgorde pagina's wijzigen'/>");
               out.print("</a>");
            }
         }         
      }
      // write the pages
      else {
         out.println("<span style='width:200px'><a href='PaginaInitAction.eb?number=" + n.getNumber() + "'>" + n.getStringValue("titel") + "</a>");
         if (n.getBooleanValue("verwijderbaar")) {
            // can be deleted add delete link
            AuthorizationHelper authHelper = new AuthorizationHelper(cloud);
            UserRole userRole = authHelper.getRoleForUserWithPagina(authHelper.getUserNode(this.username), "" + n.getNumber());    
            if (userRole.getRol() >= Roles.WEBMASTER) {
               out.print("&nbsp;<a href=\"delete_pagina.jsp?number=" + n.getNumber() + "\" onclick=\"openPopupWindow('delete_pagina', 500, 300)\""+ " target=\"delete_pagina\"" +"\">");
               out.println("<img src='../img/remove.gif' border='0' align='top' title='Verwijder pagina'/>");
               out.print("</a>");
            }
         }
         out.print("</span>");                  
      }
   }

   public String getIcon(Object node) {
      Node n = (Node) node;
      if (PaginaRenderer.RUBRIEK_NODE_MANAGER.equals(n.getNodeManager().getName())) {
         return "rubriek.gif";
      }
      else {
         return "page.gif";
      }      
   }

   /**
    * @param model - reference to the RubriekTreeModel for this tree 
    * @param roles - Map of UserRoles for the current user
	 */
   public PaginaRenderer(PaginaTreeModel model, Map roles, String username, Cloud cloud) {
      super();
      this.model = model;
      this.roles = roles;
      this.username = username;
      this.cloud = cloud;
   }
}
