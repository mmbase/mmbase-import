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

import org.mmbase.bridge.Node;

import com.finalist.tree.TreeCellRenderer;
import com.finalist.tree.TreeCellRendererAdapter;

/**
 * Renderer voor de pagina selector.
 * 
 * @author Gerard van de Weerd Date :Oct 20, 2003
 *  
 */
public class PaginaSelectorRenderer extends TreeCellRendererAdapter implements TreeCellRenderer {

   private static final String RUBRIEK_NODE_MANAGER = "rubriek";
   private PaginaTreeModel model;
   private boolean hasRefreshTarget;

    /*
	 * Render een node van de tree.
	 * 
	 * @see com.finalist.tree.TreeCellRenderer#render(java.lang.Object,
	 *      java.io.PrintWriter)
	 */
  public void render(Object node, PrintWriter out) {
      Node n = (Node) node;
      String typedef = n.getNodeManager().getName();
      //System.out.println("typedef: " + typedef);
      // write the rubrieken
      if (RUBRIEK_NODE_MANAGER.equals(typedef)) {
         out.println("<span style='width:100px'>" + n.getStringValue("naam") + "</span>");
      }
      // write the pages
      else {
         if (!hasRefreshTarget) {
             out.println("<span style='width:200px'><a href='javascript:setFieldInOpenerFrameAndClose(\"" 
                  + n.getNumber() + "\", \"" + n.getStringValue("titel") + "\")'>" + n.getStringValue("titel") + "</a></span>");
         }
         else {
            out.println("<span style='width:200px'><a href='javascript:refreshOpenerFrameAndCloseIt(\"" 
                  + n.getNumber() + "\")'>" + n.getStringValue("titel") + "</a></span>");
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
    * @param refreshTarget - the target to refresh to (either an url or a javascript function)
    * @param refreshFrame - the frame to refresh to
    * @param jsModus - modus jsp redirect or javascript call
	 */
   public PaginaSelectorRenderer(PaginaTreeModel model, boolean hasRefreshTarget) {
      super();
      this.model = model;
      this.hasRefreshTarget = hasRefreshTarget;
   }
}
