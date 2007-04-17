package nl.didactor.tree;

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
 */
public class HTMLTree {
   private TreeCellRenderer cellRenderer = new DefaultCellRenderer();

   private boolean expandAll = false;
   private String imgBaseUrl = "";
   private String treeId = "";

   private TreeModel model;

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

   private String buildImgUrl(String image) {
      return getImgBaseUrl() + image;
   }

   public TreeCellRenderer getCellRenderer() {
      return cellRenderer;
   }

   /**
   * Determine which image to show in the tree structure
   * @return complete URL
   * @param isLeaf - boolean, true if a node has no children (no + sign in front)
   * @param isLast - boolean, true if a node is the last child of it's parent
   */
   private String getImage(boolean isLeaf, boolean isLast) {
      String img;
      if (isLeaf) {
         if (isLast) {
            img = buildImgUrl("tree_leaflast.gif");
         } else {
            img = buildImgUrl("tree_vertline-leaf.gif");
         }
      } else {
         if (isLast) {
            if (expandAll) {
               img = buildImgUrl("tree_minlast.gif");
            } else {
               img = buildImgUrl("tree_pluslast.gif");
            }
         } else {
            if (expandAll) {
               img = buildImgUrl("tree_min.gif");
            } else {
               img = buildImgUrl("tree_plus.gif");
            }
         }
      }
      return img;
   }

   public String getImgBaseUrl() {
      return imgBaseUrl;
   }

   public TreeModel getModel() {
      return model;
   }

   public boolean isExpandAll() {
      return expandAll;
   }

   public void render(Writer out) {
      PrintWriter pw = new PrintWriter(out);
      pw.println("<script>");
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
      pw.println("function restoreNavTree() {");
      pw.println("   for(var i=1; i<10; i++) {");
      pw.println("      var lastclicknode = readCookie('lastnode" + treeId + "'+i);");
      pw.println("      if(lastclicknode!=null) { clickNode(lastclicknode); }");
      pw.println("   }");
      pw.println("}");
      pw.println("function clickNavNode(node) {");
      pw.println("   var level = node.split('_').length;");
      pw.println("   saveCookie('lastnode" + treeId + "'+level,node,1);");
      pw.println("   el=document.getElementById(node);");
      pw.println("   img = document.getElementById('img_'+node);");
      pw.println("   folder_img = document.getElementById('folder_img_'+node);");
      pw.println("   if (el!=null && img != null) {");
      pw.println("      if (el.style.display=='none') {");
      pw.println("         el.style.display='inline';");
      pw.println("         if (folder_img != null) { folder_img.src = '" + getImgBaseUrl() + "folder_open.gif'; }");
      pw.println("         if (img.src.indexOf('last.gif')!=-1 ) {");
      pw.println("            img.src='" + getImgBaseUrl() + "tree_minlast.gif'; ");
      pw.println("         } else {");
      pw.println("            img.src='" + getImgBaseUrl() + "tree_min.gif'; }");
      pw.println("         } ");
      pw.println("      else {");
      pw.println("         el.style.display='none';");
      pw.println("         if (folder_img != null) { folder_img.src = '" + getImgBaseUrl() + "folder_closed.gif'; }");
      pw.println("         if (img.src.indexOf('last.gif')!=-1) {");
      pw.println("            img.src='" + getImgBaseUrl() + "tree_pluslast.gif';");
      pw.println("         } else { ");
      pw.println("            img.src='" + getImgBaseUrl() + "tree_plus.gif';");
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
         out.print("<a href='javascript:clickNavNode(\"" + nodeName + "\")'>");
         out.print("<img src='" + myImg + "' border='0' align='center' valign='middle' id='img_" + nodeName + "' />");
         out.print("</a>&nbsp;");
      } else {
         out.print("<img src='" + myImg + "' border='0' align='center' valign='middle'/>&nbsp;");
      }
      String icon = getCellRenderer().getIcon(node, level);
      if (icon != null) {
         String imgName = "";
         String altText = "";
         if (icon.indexOf(":")!=-1){
            imgName = icon.substring(0,icon.indexOf(":"));
            altText = icon.substring(icon.indexOf(":")+1);
         }
         out.print("<img src='"+buildImgUrl(imgName)+"' border='0' align='center' valign='middle' alt='" + altText + "' ");
         if(imgName.equals("folder_closed.gif")) {
            out.print("id='folder_img_" + nodeName + "' ");
         }
         out.print("/>");
      }
      getCellRenderer().render(node, level, imgBaseUrl, out);
      out.print("</nobr><br/>");
      if (!model.isLeaf(node)) {
         String style = expandAll ? "block" : "none";
         out.println("<div id='" + nodeName + "' style='display: " + style + "'>");
         if(level==0) { // will be closed before <br/> (in the above statement)
            preHtml += "<nobr>";
         }
         // Render childs .....
         if (isLast) {
            preHtml += "<img src='" + buildImgUrl("tree_spacer.gif") + "' align='center' valign='middle' border='0'/>";
         } else {
            preHtml += "<img src='" + buildImgUrl("tree_vertline.gif") + "' align='center' valign='middle' border='0'/>";
         }
         int count = model.getChildCount(node);
         int createNewCount = getCellRenderer().createNewCount(node, level);
         for (int i = 0; i < createNewCount; i++) {
            renderCreateNew(node, level, out, preHtml, i, (i+1)==(createNewCount+count));
         }
         for (int i = 0; i < count; i++) {
            Object child = model.getChild(node, i);
            out.print(preHtml);
            String img = getImage(model.isLeaf(child), (i == count - 1));
            renderNode(child, level + 1, out, base + "_" + i, preHtml, img, (i == count - 1));
         }
         out.println("</div>\n");
      }
   }

   private void renderCreateNew(Object node, int level, PrintWriter out, String preHtml, int createNewNumber, boolean isLast) {
      out.print(preHtml);
      out.print("<img src='" + getImage(true,isLast) + "' border='0' align='center' valign='middle'/>&nbsp;");
      String icon = getCellRenderer().getCreateNewIcon(node, level, createNewNumber);
      if (icon != null) {
         String imgName = "";
         String altText = "";
         if (icon.indexOf(":")!=-1){
            imgName = icon.substring(0,icon.indexOf(":"));
            altText = icon.substring(icon.indexOf(":")+1);
         }
         out.print("<img src='"+buildImgUrl(imgName)+"' border='0' align='center' valign='middle' alt='" + altText + "'/>");
      }
      getCellRenderer().renderCreateNew(node, level, imgBaseUrl, createNewNumber, out);
      out.println("</nobr><br/>");
   }
   public void setCellRenderer(TreeCellRenderer cellRenderer) {
      this.cellRenderer = cellRenderer;
   }

   public void setExpandAll(boolean expandAll) {
      this.expandAll = expandAll;
   }

   public void setImgBaseUrl(String imgBaseUrl) {
      this.imgBaseUrl = imgBaseUrl;
   }

   public void setModel(TreeModel model) {
      this.model = model;
   }

}
