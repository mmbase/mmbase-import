package nl.didactor.tree;

import java.io.PrintWriter;

public interface TreeCellRenderer {
   public void render(Object node, int level, String imgBaseUrl, PrintWriter out);
   public void renderCreateNew(Object node, int level, String imgBaseUrl, int createNewNumber, PrintWriter out);
   public String getIcon(Object node, int level);
   public int createNewCount(Object node, int level);
   public String getCreateNewIcon(Object node, int level, int createNewNumber);
}
