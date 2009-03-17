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
 * See license.txt in the root of the LeoCMS directory for the full license.
 */
package nl.leocms.util.tools.documents;

import java.io.PrintWriter;

import org.mmbase.bridge.*;

import com.finalist.tree.TreeCellRenderer;
import com.finalist.tree.TreeCellRendererAdapter;

/**
 * Renderer voor de pagina editor. Kijkt naar de rechten van de gebruiker of
 * wijzigen/ordenen mogelijk is.
 *
 * @author gerard Date :Oct 20, 2003
 *
 */
public class DocumentsRenderer extends TreeCellRendererAdapter implements TreeCellRenderer {

   private static final String DOCUMENTS_NODE_MANAGER = "documents";

   private DocumentsTreeModel model;
   private Cloud cloud;
   private String pageId;

   /*
    * Render een node van de tree.
    *
    * @see com.finalist.tree.TreeCellRenderer#render(java.lang.Object,
    *      java.io.PrintWriter)
    */
  public void render(Object node, PrintWriter out) {
      Node n = (Node) node;
      String typedef = n.getNodeManager().getName();
      // write the documents
      if (DOCUMENTS_NODE_MANAGER.equals(typedef)) {
         out.println("&nbsp;<span style='width:100px; white-space: nowrap; valign:bottom;' >");
         if (n.getStringValue("type").equals("file")){
            out.println("<a href='" + n.getStringValue("url") + "' target='_blank'>");
            out.println(n.getStringValue("filename") + "</a>");
         }
         out.println("</span>");
      }
      else {
         out.print("<span style='width:200px; white-space: nowrap'>");
         out.print("</span>");
      }
   }

   public String getIcon(Object node) {

      Node n = (Node) node;
      if (n.getStringValue("filename").lastIndexOf(".")!=-1){
         String extension = n.getStringValue("filename").substring(n.getStringValue("filename").lastIndexOf(".")+1).toLowerCase();
         //return extensions;
         if (extension.equals("doc")) {
            return "icword.gif:Word bestand";
         } else if (extension.equals("xls")){
            return "icexcel.gif:Excel bestand";
         } else if (extension.equals("ppt")) {
            return "icppt.gif:Powerpoint bestand";
         } else if (extension.equals("pdf")) {
            return "icpdf.gif:PDF file";
         } else if (extension.equals("txt")) {
            return "ictxt.gif:Tekst file";
         }
         else {
            return "page.gif";
         }
      } else {
            return null;
      }
   }

   /**
    * @param model - reference to the DocumentsTreeModel for this tree
    * @param roles - Map of UserRoles for the current user
    */
   public DocumentsRenderer(Cloud cloud, String pageId) {
      super();
      this.cloud = cloud;
      this.pageId = pageId;
   }
}
