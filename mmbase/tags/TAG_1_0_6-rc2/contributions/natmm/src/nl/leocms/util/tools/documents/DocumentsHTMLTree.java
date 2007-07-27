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
package nl.leocms.util.tools.documents;

import com.finalist.tree.HTMLTree;
import java.io.PrintWriter;
import java.io.Writer;
import javax.swing.tree.TreeModel;

import org.mmbase.bridge.*;

public class DocumentsHTMLTree extends HTMLTree {
	
	public DocumentsHTMLTree(TreeModel model, String treeId) {
      this.model = model;
      this.treeId = treeId;
   }
	
   public void render(Writer out) {
      PrintWriter pw = new PrintWriter(out);
		pw.println("<script>");
      renderCookieScripts(pw);
      pw.println("function clickNode(node) {");
      pw.println("   var level = node.split('_').length;");
      pw.println("   saveCookie('lastnode" + treeId + "'+level,node,1);");
      pw.println("   el=document.getElementById(node);");
      pw.println("   img = document.getElementById('img_'+node);");
      pw.println("   folder_img = document.getElementById('folder_img_'+node);");
      pw.println("   if (el!=null && img != null) {");
      pw.println("      if (el.style.display=='none') {");
      pw.println("         el.style.display='inline';");
      pw.println("         folder_img.src = '" + getImgBaseUrl() + "folder_open.gif';");
      pw.println("         if (img.src.indexOf('last.gif')!=-1 ) {");
      pw.println("            img.src='" + getImgBaseUrl() + "minlast.gif'; ");
      pw.println("         } else {");
      pw.println("            img.src='" + getImgBaseUrl() + "min.gif'; }");
      pw.println("         } ");
      pw.println("      else {");
      pw.println("         el.style.display='none';");
      pw.println("         folder_img.src = '" + getImgBaseUrl() + "folder_closed.gif';");
      pw.println("         if (img.src.indexOf('last.gif')!=-1) {");
      pw.println("            img.src='" + getImgBaseUrl() + "pluslast.gif';");
      pw.println("         } else { ");
      pw.println("            img.src='" + getImgBaseUrl() + "plus.gif';");
      pw.println("         }");
      pw.println("      }");
      pw.println("   }");
      pw.println("}");
		pw.println("</script>");
		
      renderNode(model.getRoot(), 0, pw, "node", "", getImage(false, true), true);
      pw.flush();
   }

   private void renderNode(Object node, int level, PrintWriter out, String base, String preHtml, String myImg, boolean isLast) {
      String nodeName = base + "_" + level;
      String icon = getCellRenderer().getIcon(node);
      String sFolderIconText = "";
      Node n = (Node) node;
      if (icon != null) {
         String imgName = "page.gif";
         String docType = "";
         if (icon.indexOf(":")!=-1){
            imgName = icon.substring(0,icon.indexOf(":"));
            docType = icon.substring(icon.indexOf(":")+1);
         }
         sFolderIconText = "<img src='"+buildImgUrl(imgName)+"' border='0' align='center' valign='middle' alt='" + docType + "'/>";
      } else {
         sFolderIconText = "<img src='"+getImgBaseUrl() + "folder_closed.gif"+"' border='0' align='center' valign='middle' id='folder_img_" + nodeName + "'/>";
      }
      if (!model.isLeaf(node)) {
         out.print("<a href='javascript:clickNode(\"" + nodeName + "\")'>");
         out.print("<img src='" + myImg + "' border='0' align='center' valign='middle' id='img_" + nodeName + "'/>");
         out.print(sFolderIconText);
         out.print(n.getStringValue("filename"));
         out.print("</a>");
      } else {
         out.print("<img src='" + myImg + "' border='0' align='center' valign='middle'/>&nbsp;");
         out.print(sFolderIconText);
      }
      getCellRenderer().render(node, out);
      out.println("</nobr><br/>");
      if (!model.isLeaf(node)) {
         String style = expandAll ? "block" : "none";
         out.println("<div id='" + nodeName + "' style='display: " + style + "'>");
			if(level==0) { // will be closed before <br/> (in the above statement)
            preHtml += "<nobr>";
         }
         // Render childs .....
         if (isLast) {
            preHtml += "<img src='" + buildImgUrl("spacer.gif") + "' align='center' valign='middle' border='0'/>";
         } else {
            preHtml += "<img src='" + buildImgUrl("vertline.gif") + "' align='center' valign='middle' border='0'/>";
         }
         int count = model.getChildCount(node);
         for (int i = 0; i < count; i++) {
            Object child = model.getChild(node, i);
            out.print(preHtml);
            String img = getImage(model.isLeaf(child), (i == count - 1));
            renderNode(child, level + 1, out, base + "_" + i, preHtml, img, (i == count - 1));
         }
         out.println("</div>\n");
      }
   }
}