package nl.didactor.tree;

import java.io.PrintWriter;

public class TreeCellRendererAdapter implements TreeCellRenderer {

   public void render(Object node, int level, String imgBaseUrl, PrintWriter out) {  
   }

   public String getIcon(Object node, int level) {
      return null;
   }

   public void renderCreateNew(Object node, int level, String imgBaseUrl, int createNewNumber, PrintWriter out) {
   }

   public int createNewCount(Object node, int level) {
      return 0;
   }

   public String getCreateNewIcon(Object node, int level, int createNewNumber) {
      return null;
   }

}
