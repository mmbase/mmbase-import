package nl.leocms.util.tools;

import java.io.*;
import java.util.*;

import jxl.Workbook;
import jxl.write.WritableWorkbook;
import jxl.write.WritableSheet;
import jxl.write.WritableCellFormat;
import jxl.write.Label;
import jxl.write.WriteException;

import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * Convert a Excel file to LeoCMS navigation structure
 * 
 * @author Alexey Zemskov
 * @version $Revision: 1.1 $
 */
public class Excel2Menu {

   private static final Logger log = Logging.getLoggerInstance(Excel2Menu.class);
   
   private Cloud cloud;
   
   public Excel2Menu(Cloud cloud) {
     this.cloud = cloud;
   }
      
   public void convert(String name) {
     try {
         log.debug("Convert xls file:" + name);
         InputStream inputStream = new FileInputStream(name);
         convert(inputStream);
     } catch (IOException e) {
         log.error(e);
     }
   }
   
   public void convert(InputStream inputStream) {
   // creates a rubrieken tree from the Excel file and relates it to the rubriek with alias 'root'
   // only the first worksheet is used and the title of the worksheet is the naam of the new rubrieken tree
   // the leaves of the rubrieken tree are pagina's
   // the templates specified in the Excel are related to the paginas
 
   }

}
