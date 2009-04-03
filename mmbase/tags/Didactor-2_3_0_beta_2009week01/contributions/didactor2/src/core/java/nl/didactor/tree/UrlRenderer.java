package nl.didactor.tree;

import java.io.PrintWriter;

import org.mmbase.bridge.Node;

public class UrlRenderer extends TreeCellRendererAdapter implements TreeCellRenderer {

   public void render(Object node, PrintWriter out) {
      Node n = (Node)node;
      out.println("<a href='javascript:void(0);'>"+node+"("+n.getNumber()+")</a>"); 
   }
   
   public String getIcon(Object node) {
      return "page.gif";
   }

}
