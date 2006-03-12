package nl.didactor.metadata.tree;

import java.io.PrintWriter;
import java.util.*;
import java.util.TreeMap;

import org.mmbase.bridge.*;

import nl.didactor.tree.TreeCellRenderer;
import nl.didactor.tree.TreeCellRendererAdapter;
import nl.didactor.metadata.tree.MetadataTreeModel;
import nl.didactor.taglib.TranslateTable;

import org.mmbase.util.logging.Logging;
import org.mmbase.util.logging.Logger;


public class MetadataRenderer extends TreeCellRendererAdapter implements TreeCellRenderer {
  
   private static Logger log = Logging.getLoggerInstance(MetadataTreeModel.class);

   private MetadataTreeModel model;
   private Cloud cloud;
   private String wizardjsp;
   private String listjsp;
   private TranslateTable tt;
   
   public void render(Object node, int level, String imgBaseUrl, PrintWriter out) {
      Node n = (Node) node;
      String sTypeDef = n.getNodeManager().getName();
      out.println("<span style='width:100px;white-space:nowrap;valign:middle;' >");
      if(level>0) {
         String title = n.getStringValue((sTypeDef.equals("metavocabulary")?"value":"name"));
         if(title.length()>19) {  title = title.substring(0,19) + "&hellip;"; }
         out.println("<a href='" 
                        + wizardjsp
                        + "&wizard=config/" + sTypeDef + "/" + sTypeDef + "&objectnumber=" 
                        + n.getNumber()
                        + "' title='" + tt.translate("metadata.treat" + sTypeDef) + "' target='text'>&nbsp;"
                        + title
                        + "</a>");
         if (sTypeDef.equals("metastandard")) {
            out.println("<a href='metaedit.jsp?number="
                           + n.getNumber()
                           + "&set_defaults=true' target='text'>"
                           + "<img src='" + imgBaseUrl + "metavalid.gif' border='0' alt='" + tt.translate("metadata.editdefaultmetadata") + "'>"
                           + "</a>");
         }
      } else {
        out.println("<a href='" 
                        + listjsp
                        + "&origin=" + n.getNumber()
                        + "&wizard=config/metastandard/metastandard-origin&nodepath=metastandard&fields=name,owner&search=yes&searchfields=name&orderby=name&directions=UP'" 
                        + " title='" + tt.translate("metadata.listmetastandards") + "' target='text'>&nbsp;"
                        + tt.translate("metadata.metadata")
                        + "</a>");
      }
      out.println("</span>");
   }

   public void renderCreateNew(Object node, int level, String imgBaseUrl, int createNewNumber, PrintWriter out) {
      Node n = (Node) node;
      String sTypeDef = n.getNodeManager().getName();
      if(level>0) {
         if(sTypeDef.equals("metastandard")) { 
            sTypeDef = "metadefinition"; 
         } else if(sTypeDef.equals("metadefinition")) {
            sTypeDef = "metavocabulary"; 
         }
      }
      String title = tt.translate("metadata.createnew" + sTypeDef);
      if(title.length()>19&&level>2) {
          title = title.substring(0,19) + "&hellip;"; 
      }
      out.println("<span style='width:100px;white-space:nowrap;valign:middle;' >");
      out.println("<a href='" 
                     + wizardjsp
                     + "&wizard=config/" + sTypeDef + "/" + sTypeDef + "-origin&objectnumber=new&origin=" + n.getNumber() + "'" 
                     + " title='" + tt.translate("metadata.createnewdescription" + sTypeDef) + "' target='text'>&nbsp;"
                     + title
                     + "</a>");
      out.println("</span>");
   }

   public String getIcon(Object node, int level) {
      Node n = (Node) node;
      String sTypeDef = n.getNodeManager().getName();
      String altText =  tt.translate("metadata.treat" + sTypeDef);
      if (sTypeDef.equals("metastandard")){
         if(level>0) {
            return "folder_closed.gif:" + altText;
         } else {
            return "menu_root.gif:" + altText;
         }
      } else if (sTypeDef.equals("metadefinition")){
         return "learnblock.gif:" + altText;
      } else if (sTypeDef.equals("metavocabulary")){
         return "edit_learnobject.gif:" + altText;
      } else {
         return null;
      }
   }

   public int createNewCount(Object node, int level) {
      return 1;   
   }

   public String getCreateNewIcon(Object node, int level, int createNewNumber) {
      Node n = (Node) node;
      String sTypeDef = n.getNodeManager().getName();
      return "new_education.gif:" + tt.translate("metadata.createnewdescription" + sTypeDef);
   }

   public MetadataRenderer(Cloud cloud, String wizardjsp, String listjsp, String translateLocale) {
      super();
      this.cloud = cloud;
      this.wizardjsp = wizardjsp;
      this.listjsp = listjsp;
      this.tt = new TranslateTable(translateLocale);
   }
}
