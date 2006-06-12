package com.finalist.tree.html;

import java.io.PrintWriter;

import org.mmbase.bridge.Node;

import com.finalist.tree.TreeInfo;
import com.finalist.tree.TreeModel;


/**
 * @author Nico Klasens
 *
 */
public class ServerHTMLTree extends HTMLTree {

   protected TreeInfo info;

   private String jsppage = null;

   public ServerHTMLTree(TreeModel model, HTMLTreeCellRenderer cellRenderer,
           TreeInfo info, String jsppage) {
      super(model, cellRenderer);
      this.info = info;
      this.jsppage = jsppage;
   }

   
   protected void getScript(PrintWriter pw) {
      pw.println("<script type=\"text/javascript\">");
      pw.println("function clickNode(channel, expand) {");
      pw.println("   document.getElementById('savetree').value = 'false';");
      pw.println("   document.getElementById('channel').value = channel;");
      pw.println("   document.getElementById('expand').value = expand;");
      pw.println("   document.forms[0].submit();");
      pw.println("}");
      pw.println("</script>");
   }

   protected String getExpandLink(Object node, String nodeName) {
      Node n = (Node) node;
      boolean openChannel = info.isOpen(n);
      if ("javascript".equals(jsppage)) {
         //@TODO: add anchor to form submit
         return "javascript:clickNode('" + n.getNumber() + "', '" + !openChannel + "');";
      }
      char sep = jsppage.indexOf('?')>0 ? '&' : '?'; 
      return jsppage +  sep+ "channel=" + n.getNumber() + "&amp;expand="+!openChannel+"#" + nodeName;
   }

   protected String getChildStyle() {
      return "display: block";
   }

   protected boolean showChildren(Object node) {
      return info.isOpen(node) && !getModel().isLeaf(node);
   }

   protected boolean isActive(Object node) {
      return info.isOpen(node);
   }

   protected void renderChild(int level, PrintWriter out, String base, String preHtml, int count, int i, Object child) {
      String img = getImage(getModel().isLeaf(child), (i == count - 1), info.isOpen(child));
      renderNode(child, level + 1, out, base + "_" + i, preHtml, img, (i == count - 1));
   }

}
