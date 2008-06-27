/*
 * Created on Sep 15, 2003 by edwin
 * 
 */
package com.finalist.tree.html;

import java.io.PrintWriter;
import java.io.Writer;

import com.finalist.tree.Tree;
import com.finalist.tree.TreeModel;

/**
 * Class reponsible for rendering the HTML tree (+/-, lines, scripts etc.) The
 * HTML uses a number of gif's that are located using the ImgBaseUrl. Gifs
 * needed:
 * <UL>
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
 * @author edwin Date :Sep 15, 2003
 */
public abstract class HTMLTree extends Tree {

   protected HTMLTreeCellRenderer cellRenderer = new DefaultCellRenderer();


   public HTMLTree(TreeModel model, HTMLTreeCellRenderer cellRenderer) {
      super(model, null);
      this.cellRenderer = cellRenderer;
   }


   public HTMLTreeCellRenderer getCellRenderer() {
      return cellRenderer;
   }


   /**
    * Determine which image to show in the tree structure
    * 
    * @return complete URL
    * @param isLeaf -
    *           boolean, true if a node has no children (no + sign in front)
    * @param isLast -
    *           boolean, true if a node is the last child of it's parent
    * @param expand -
    *           boolean, true if a node has children and is expanded
    */
   protected String getImage(boolean isLeaf, boolean isLast, boolean expand) {
      String img;
      if (isLeaf) {
         if (isLast) {
            img = buildImgUrl("tree/leaflast.gif");
         }
         else {
            img = buildImgUrl("tree/vertline-leaf.gif");
         }
      }
      else {
         if (isLast) {
            if (expand) {
               img = buildImgUrl("tree/minlast.gif");
            }
            else {
               img = buildImgUrl("tree/pluslast.gif");
            }
         }
         else {
            if (expand) {
               img = buildImgUrl("tree/min.gif");
            }
            else {
               img = buildImgUrl("tree/plus.gif");
            }
         }
      }
      return img;
   }


   public void render(Writer out) {
      PrintWriter pw = new PrintWriter(out);
      getScript(pw);
      Object rootNode = getModel().getRoot();
      boolean isExpanded = showChildren(rootNode);
      renderNode(rootNode, 0, pw, "node", "<nobr>", getImage(false, true, isExpanded), true);
      pw.flush();
   }


   protected void renderNode(Object node, int level, PrintWriter out, String base, String preHtml, String myImg,
         boolean isLast) {
      String nodeName = base + "_" + level;
      if (!getModel().isLeaf(node)) {
         out.print("<a name=\"" + nodeName + "\" href=\"" + getExpandLink(node, nodeName) + "\">");
         out.print("<img src='" + myImg + "' alt='' border='0' align='top' valign='top' id='img_" + nodeName + "'/>");
         out.print("</a>&nbsp;");
      }
      else {
         out.print("<img src='" + myImg + "' alt='' border='0' align='top' valign='top'/>&nbsp;");
      }

      HTMLTreeElement te = getCellRenderer().getElement(getModel(), node, nodeName);
      if (te != null) {
         if (isActive(node)) {
            out.print("<b>");
            out.print(te.render(getImgBaseUrl()));
            out.print("</b>");
         }
         else {
            out.print(te.render(getImgBaseUrl()));
         }
      }
      out.print("</nobr>");
      out.println("<br>");

      if (showChildren(node)) {
         out.println("<div id='" + nodeName + "' style='" + getChildStyle() + "'>");
         // Render childs .....
         if (isLast) {
            preHtml += "<img src='" + buildImgUrl("tree/spacer.gif") + "' alt='' align='top' valign='top' border='0'/>";
         }
         else {
            preHtml += "<img src='" + buildImgUrl("tree/vertline.gif")
                  + "' alt='' align='top' valign='top' border='0'/>";
         }

         int count = getModel().getChildCount(node);
         for (int i = 0; i < count; i++) {
            Object child = getModel().getChild(node, i);
            out.print(preHtml);
            renderChild(level, out, base, preHtml, count, i, child);
         }
         out.println("</div>\n");
      }
   }


   protected abstract void getScript(PrintWriter pw);


   protected abstract String getExpandLink(Object node, String nodeName);


   protected abstract String getChildStyle();


   protected abstract boolean showChildren(Object node);


   protected abstract boolean isActive(Object node);


   protected abstract void renderChild(int level, PrintWriter out, String base, String preHtml, int count, int i,
         Object child);

}