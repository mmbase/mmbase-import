package nl.didactor.tree;

import java.io.PrintWriter;

public class DefaultCellRenderer extends TreeCellRendererAdapter implements TreeCellRenderer {

   public void render(Object node, PrintWriter out) {
     out.print( node.toString());
   }

}
