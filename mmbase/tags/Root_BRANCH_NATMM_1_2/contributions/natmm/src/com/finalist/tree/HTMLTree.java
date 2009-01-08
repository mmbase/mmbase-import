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
package com.finalist.tree;

import java.io.PrintWriter;
import java.io.Writer;

import javax.swing.tree.TreeModel;

/**
 * Class reponsible for rendering the HTML tree (+/-, lines, scripts etc.)
 * The HTML uses a number of gif's that are located using the ImgBaseUrl.
 * Gifs needed:<UL>
 * <LI>leaflast.gif</LI>
 * <LI>vertline-leaf.gif</LI>
 * <LI>minlast.gif</LI>
 * <LI>pluslast.gif</LI>
 * <LI>min.gif</LI>
 * <LI>plus.gif</LI>
 * <LI>vertline.gif</LI>
 * <LI>spacer.gif</LI>
 * </UL>
 *
 * @author Edwin van der Elst
 * Date :Sep 15, 2003
 * 
 */
public class HTMLTree {
   private TreeCellRenderer cellRenderer = new DefaultCellRenderer();
   private String imgBaseUrl = "editors/img/";

   public boolean expandAll = false;
   public String treeId = "";

   public TreeModel model;

   public HTMLTree() {
      model = null;
   }

   public HTMLTree(TreeModel model) {
      this.model = model;
   }

   public HTMLTree(TreeModel model, String treeId) {
      this.model = model;
      this.treeId = treeId;
   }

   public String buildImgUrl(String image) {
      return getImgBaseUrl() + image;
   }

   /**
    * @return
    */
   public TreeCellRenderer getCellRenderer() {
      return cellRenderer;
   }

   /**
   * Determine which image to show in the tree structure
   * @return complete URL
   * @param isLeaf - boolean, true if a node has no children (no + sign in front)
   * @param isLast - boolean, true if a node is the last child of it's parent
   */
   public String getImage(boolean isLeaf, boolean isLast) {
      String img;
      if (isLeaf) {
         if (isLast) {
            img = buildImgUrl("leaflast.gif");
         } else {
            img = buildImgUrl("vertline-leaf.gif");
         }
      } else {
         if (isLast) {
            if (expandAll) {
               img = buildImgUrl("minlast.gif");
            } else {
               img = buildImgUrl("pluslast.gif");
            }
         } else {
            if (expandAll) {
               img = buildImgUrl("min.gif");
            } else {
               img = buildImgUrl("plus.gif");
            }
         }
      }
      return img;
   }

   /**
    * @return
    */
   public String getImgBaseUrl() {
      return imgBaseUrl;
   }

   /**
    * @return
    */
   public TreeModel getModel() {
      return model;
   }

   /**
    * @return
    */
   public boolean isExpandAll() {
      return expandAll;
   }

   public void renderCookieScripts(PrintWriter pw) {
      pw.println("function saveCookie(name,value,days) {");
      pw.println("   if (days) {");
      pw.println("      var date = new Date();");
      pw.println("      date.setTime(date.getTime()+(days*24*60*60*1000))");
      pw.println("      var expires = '; expires='+date.toGMTString()");
      pw.println("   } else expires = ''");
      pw.println("   document.cookie = name+'='+value+expires+'; path=/'");
      pw.println("}");
      pw.println("function readCookie(name) {");
      pw.println("   var nameEQ = name + '='");
      pw.println("   var ca = document.cookie.split(';')");
      pw.println("   for(var i=0;i<ca.length;i++) {");
      pw.println("      var c = ca[i];");
      pw.println("      while (c.charAt(0)==' ') c = c.substring(1,c.length)");
      pw.println("      if (c.indexOf(nameEQ) == 0) return c.substring(nameEQ.length,c.length)");
      pw.println("   }");
      pw.println("   return null");
      pw.println("}");
      pw.println("function deleteCookie(name) {");
      pw.println("   saveCookie(name,'',-1)");
      pw.println("}");
      pw.println("function restoreTree() {");
      pw.println("   for(var i=1; i<10; i++) {");
      pw.println("      var lastclicknode = readCookie('lastnode" + treeId + "'+i);");
      pw.println("      if(lastclicknode!=null) { clickNode(lastclicknode); }");
      pw.println("   }");
      pw.println("}");
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
      pw.println("   if (el!=null && img != null) {");
      pw.println("      if (el.style.display=='none') {");
      pw.println("      el.style.display='inline';");
      pw.println("      if (img.src.indexOf('last.gif')!=-1 ) {");
      pw.println("         img.src='" + getImgBaseUrl() + "minlast.gif'; } else { ");
      pw.println("         img.src='" + getImgBaseUrl() + "min.gif'; }");
      pw.println("      } else {");
      pw.println("         el.style.display='none';");
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
      if (!model.isLeaf(node)) {
         out.print("<a href='javascript:clickNode(\"" + nodeName + "\")'>");
         out.print("<img src='" + myImg + "' border='0' align='center' valign='middle' id='img_" + nodeName + "'/>");
         out.print("</a>&nbsp;");
      } else {
         out.print("<img src='" + myImg + "' border='0' align='center' valign='middle'/>&nbsp;");
      }
      String icon = getCellRenderer().getIcon(node);
      if (icon != null) {
         out.print("<img src='"+buildImgUrl(icon)+"' border='0' align='center' valign='middle'/> ");
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

   /**
    * @param cellRenderer
    */
   public void setCellRenderer(TreeCellRenderer cellRenderer) {
      this.cellRenderer = cellRenderer;
   }

   /**
    * @param expandAll
    */
   public void setExpandAll(boolean expandAll) {
      this.expandAll = expandAll;
   }

   /**
    * @param imgBaseUrl
    */
   public void setImgBaseUrl(String imgBaseUrl) {
      this.imgBaseUrl = imgBaseUrl;
   }

   /**
    * @param model
    */
   public void setModel(TreeModel model) {
      this.model = model;
   }

}

