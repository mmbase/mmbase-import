package com.finalist.tree.html;

import java.io.PrintWriter;

import com.finalist.tree.TreeModel;

/**
 * @author Nico Klasens
 */
public class ClientHTMLTree extends HTMLTree {

   private boolean expandAll = false;


   public ClientHTMLTree(TreeModel model, HTMLTreeCellRenderer cellRenderer) {
      super(model, cellRenderer);
   }


   /**
    * @param pw
    */
   @Override
   protected void getScript(PrintWriter pw) {
      pw.println("<script type=\"text/javascript\">");
      pw.println("function clickNode(node) {");
      pw.println("el=document.getElementById(node);");
      pw.println("img = document.getElementById('img_'+node);");
      pw.println("");
      pw.println("if (el.style.display=='none') {");
      pw.println("el.style.display='inline';");
      pw.println("if (img.src.indexOf('last.gif')!=-1 ) {");
      pw.println("  img.src='" + getImgBaseUrl() + "minlast.gif'; } else { ");
      pw.println("  img.src='" + getImgBaseUrl() + "min.gif'; }");
      pw.println("}");
      pw.println("else {");
      pw.println("el.style.display='none';");
      pw.println("if (img.src.indexOf('last.gif')!=-1) {");
      pw.println("  img.src='" + getImgBaseUrl() + "pluslast.gif'; } else { ");
      pw.println("img.src='" + getImgBaseUrl() + "plus.gif'; }");
      pw.println("}");
      pw.println("}");
      pw.println("</script>");
   }


   @Override
   protected String getExpandLink(Object node, String nodeName) {
      return "javascript:clickNode(\"" + nodeName + "\")";
   }


   @Override
   protected String getChildStyle() {
      return "display: " + (expandAll ? "block" : "none");
   }


   @Override
   protected boolean showChildren(Object node) {
      return !getModel().isLeaf(node);
   }


   @Override
   protected boolean isActive(Object node) {
      return false;
   }


   @Override
   protected void renderChild(int level, PrintWriter out, String base, String preHtml, int count, int i, Object child) {
      String img = getImage(getModel().isLeaf(child), (i == count - 1), expandAll);
      renderNode(child, level + 1, out, base + "_" + i, preHtml, img, (i == count - 1));
   }


   public boolean isExpandAll() {
      return expandAll;
   }


   /**
    * @param expandAll
    */
   public void setExpandAll(boolean expandAll) {
      this.expandAll = expandAll;
   }

}