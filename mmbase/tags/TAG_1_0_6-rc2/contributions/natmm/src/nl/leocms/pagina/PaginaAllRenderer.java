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
import java.util.TreeMap;

import nl.leocms.authorization.UserRole;
import nl.leocms.authorization.Roles;
import nl.leocms.authorization.AuthorizationHelper;
import nl.leocms.util.PaginaHelper;
import nl.leocms.util.ApplicationHelper;

import org.mmbase.bridge.*;

import com.finalist.tree.TreeCellRenderer;
import com.finalist.tree.TreeCellRendererAdapter;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * Renderer voor de pagina editor. Kijkt naar de rechten van de gebruiker of
 * wijzigen/ordenen mogelijk is.
 * 
 * @author gerard Date :Oct 20, 2003
 *  
 */
public class PaginaAllRenderer extends TreeCellRendererAdapter implements TreeCellRenderer {

   private static Logger log = Logging.getLoggerInstance(PaginaAllRenderer.class.getName());

   private static final String RUBRIEK_NODE_MANAGER = "rubriek";
   private Map roles;
   private PaginaTreeModel model;
   private String username;
   private Cloud cloud;
   private String targetFrame;
   private PaginaHelper paginaHelper;
   private String contextPath;
   
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
      if (RUBRIEK_NODE_MANAGER.equals(typedef)) {

         UserRole role = (UserRole) roles.get(new Integer(n.getNumber()));
         int level = n.getIntValue("level");
   
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
               String url = "../../mmbase/edit/wizard/jsp/wizard.jsp?wizard=config/rubriek/rubriek&nodepath=rubriek&referrer=/editors/empty.html&objectnumber=" + n.getNumber();
               out.println("<a href='" + url + "' target='workpane'><img src='../img/edit_w.gif' border='0' align='top' title='Bewerk rubriek'/></a>");
               
            }
         }

         if (role.getRol() >= Roles.EINDREDACTEUR) {
            
            out.print("<a href=\"RubriekInitAction.eb?parent=" + n.getNumber() + "\"  target='bottompane'>");
            out.print("<img src='../img/new_rubriek.gif' border='0' align='top' title='Nieuwe subrubriek'/>");
            out.print("</a>&nbsp;");
            
            out.print("<a href='PaginaInitAction.eb?parent=" + n.getNumber() + "'  target='bottompane'>");
            out.print("<img src='../img/new_pagina.gif' border='0' align='top' title='Nieuwe pagina'/>");
            out.print("</a>&nbsp;");      

            if (model.getSubObjectsCount(n) > 1) {
               out.print("<a href='reorder_pagina.jsp?parent=" + n.getNumber() + "'  target='bottompane'>");
               out.println("<img src='../img/reorder.gif' border='0' align='top' title='Volgorde rubrieken en paginas wijzigen'/>");
               out.print("</a>");
            }
            if ((model.getChildCount(n) == 0) && (level > 0)) {
               if (role.getRol() >= Roles.EINDREDACTEUR) {
                  out.print("&nbsp;<a href=\"../rubrieken/delete_rubriek.jsp?number=" + n.getNumber() + "\" onclick=\"openPopupWindow('delete_rubriek', 500, 300)\""+ " target=\"delete_rubriek\"" +"\">");
                  out.println("<img src='../img/remove.gif' border='0' align='top' title='Verwijder rubriek'/>");
                  out.print("</a>");
               }
            }
         }
      }
      
      // write the pages
      else {
         AuthorizationHelper authHelper = new AuthorizationHelper(cloud);
         UserRole userRole = authHelper.getRoleForUserWithPagina(authHelper.getUserNode(this.username), "" + n.getNumber());    
         if (userRole.getRol() >= Roles.EINDREDACTEUR) {
            out.println("<a href='PaginaInitAction.eb?number=" + n.getNumber() + "' target='bottompane'>" + n.getStringValue("titel") + "</a>");
         } else {
            out.println("<font class='notactive'>" + n.getStringValue("titel") + "</font>");
         }
         if (userRole.getRol() >= Roles.SCHRIJVER) {
            // ** hh: String url = paginaHelper.createPaginaUrl("" + n.getNumber(), contextPath);
            TreeMap ewUrls = paginaHelper.createEditwizardUrls("" + n.getNumber(), contextPath);
            while(!ewUrls.isEmpty()) {
               String nextUrl = (String) ewUrls.firstKey();
               String nextEw =  (String) ewUrls.get(nextUrl);
               out.println("<a href='" + nextUrl + "' target='" + targetFrame + "'");
               if(nextUrl.indexOf("list.jsp")>-1) {
                  out.println("><img src='../img/edit_l.gif");
               } else if(nextUrl.indexOf("wizard.jsp")>-1) {
                  out.println(" onClick=\"javascript:saveCookie('ew','on',1);\"><img src='../img/edit_w.gif");
               } else {
                  out.println("><img src='../img/edit.gif");
               }
               out.println("' border='0' align='top' title='" + nextEw + "'/></a>");
               ewUrls.remove(nextUrl);
            }
            NodeList ptNodeList = n.getRelatedNodes("paginatemplate");
            if(ptNodeList.size()>0) {
               String subDir = PaginaHelper.getSubDir(cloud,n.getStringValue("number"));
               String paginaTemplate = ((Node) ptNodeList.get(0)).getStringValue("url");
               out.println("<a href='" + contextPath + "/" + subDir + paginaTemplate + "?p=" + n.getNumber() + "&preview=on' target='" + targetFrame 
                  + "'><img src='../img/refresh.gif' border='0' align='top' onClick='return warnOnEditwizardOpen();' onmousedown='cancelClick=true;' title='Bekijk deze pagina in de preview'/></a>");
            }
         }
         if (n.getBooleanValue("verwijderbaar")) {
            // can be deleted add delete link
            if (userRole.getRol() >= Roles.EINDREDACTEUR) {
               out.print("&nbsp;<a href=\"delete_pagina.jsp?number=" + n.getNumber() + "\" onclick=\"openPopupWindow('delete_pagina', 500, 300)\""+ " target=\"delete_pagina\"" +"\">");
               out.println("<img src='../img/remove.gif' border='0' align='top' title='Verwijder pagina'/>");
               out.print("</a>");
            }
         }
      }
   }

   public String getIcon(Object node) {
      Node n = (Node) node;
      if (RUBRIEK_NODE_MANAGER.equals(n.getNodeManager().getName())) {
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
   public PaginaAllRenderer(PaginaTreeModel model, Map roles, String username, Cloud cloud, String targetFrame, String contextPath) {
      super();
      this.model = model;
      this.roles = roles;
      this.username = username;
      this.cloud = cloud;
      this.targetFrame = targetFrame;
      this.contextPath = contextPath;
      this.paginaHelper = new PaginaHelper(cloud);
   }
}
