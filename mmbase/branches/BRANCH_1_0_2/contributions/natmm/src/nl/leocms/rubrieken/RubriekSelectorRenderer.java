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

import org.mmbase.bridge.Node;

import com.finalist.tree.TreeCellRenderer;
import com.finalist.tree.TreeCellRendererAdapter;

/**
 * Renderer voor de pagina selector.
 *
 * @author Gerard van de Weerd Date :Oct 20, 2003
 *
 */
public class RubriekSelectorRenderer extends TreeCellRendererAdapter implements TreeCellRenderer {

   private static final String RUBRIEK_NODE_MANAGER = "rubriek";
   private RubriekTreeModel model;
   private Map roles;
   private String refreshTarget;
   private boolean jsModus;
   private String refreshFrame;

   /*
   * Render een node van de tree voor de selector pagina.
   *
   * @see com.finalist.tree.TreeCellRenderer#render(java.lang.Object,
   *      java.io.PrintWriter)
   */
   public void render(Object node, PrintWriter out) {
      Node n = (Node) node;
      UserRole role = (UserRole) roles.get(new Integer(n.getNumber()));
      // write the rubrieken
      if (role.getRol() >= nl.leocms.authorization.Roles.SCHRIJVER) {
         if (jsModus) {
            if (refreshFrame==null || "".equals(refreshFrame)) {
                out.println("<span style='width:200px'><a href='javascript:callJsFunction(\"" + refreshTarget
                            + "\", \""
                            + n.getNumber() + "\", \"" + n.getStringValue("naam") + "\")'>"
                            + n.getStringValue("naam") +"</a>");
                out.print("</span>");
            }
            else {
                out.println("<span style='width:200px'><a href='javascript:callJsFunctionInFrame(\"" + refreshTarget
                            + "\", \"" + refreshFrame + "\", \""
                            + n.getNumber() + "\", \"" + n.getStringValue("naam") + "\")'>"
                            + n.getStringValue("naam") +"</a>");
                out.print("</span>");
            }
         } 
         else {
            if (refreshFrame == null || "".equals(refreshFrame)) {
               out.println("<span style='width:200px'><a href='javascript:refreshOpenerAndClose(\""
                           + refreshTarget + "\", \"" + n.getNumber() + "\")'>" + n.getStringValue("naam") + "</a>");
               out.print("</span>");
            } else {
               out.println("<span style='width:200px'><a href='javascript:refreshOpenerFrameAndClose(\"" + refreshTarget
                           + "\", \"" + refreshFrame + "\", \""
                           + n.getNumber() + "\")'>" + n.getStringValue("naam") + "</a>");
               out.print("</span>");
            }
         }
      }
      else {
         out.println("<span style='width:200px'>" + n.getStringValue("naam") + "</span>");
      }
   }

   public String getIcon(Object node) {
      Node n = (Node) node;
      if (RUBRIEK_NODE_MANAGER.equals(n.getNodeManager().getName())) {
         return "rubriek.gif";
      } else {
         return "page.gif";
      }
   }

   /**
    * @param model - reference to the RubriekTreeModel for this tree
    * @param refreshTarget - the target to refresh to (either an url or a javascript function)
    * @param refreshFrame - the frame to refresh to
    * @param jsModus - modus jsp redirect or javascript call
    */
   public RubriekSelectorRenderer(RubriekTreeModel model, Map roles, String refreshTarget, String refreshFrame, boolean jsModus) {
      super();
      this.model = model;
      this.roles = roles;
      this.refreshTarget = refreshTarget;
      this.refreshFrame = refreshFrame;
      this.jsModus = jsModus;
   }
}
