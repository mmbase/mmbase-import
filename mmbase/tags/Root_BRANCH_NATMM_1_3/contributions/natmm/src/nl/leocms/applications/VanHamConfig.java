package nl.leocms.applications;

/* This class contains settings specific for the set of templates in the vanham folder
*/

public class VanHamConfig {
   
   public final static String[] CONTENTELEMENTS = {  
      "artikel",
      "projects",
      "items",
      "images"
   };
   
   public final static String[] OBJECTS = {  
      "artikel",
      "projects",
      "items",
      "images"
   };
   
   public final static String[] PATHS_FROM_PAGE_TO_OBJECTS = {  
      "object,contentrel,pagina",                 		// artikel
      "object,contentrel,pagina",                 		// projects
      "object,posrel,projects,contentrel,pagina", 		// items
      "object,posrel,items,posrel,projects,contentrel,pagina", 	// images
   };
	
   public static int DEFAULT_STYLE = -1;
   public static String [] style1 = { "vanham" };

   public static String cssPath = "css/";
	
   public static boolean urlConversion = true;

   public static String rootDir =  "C:/data/natmm/webapps/ROOT/WEB-INF/data/";
	 
   public VanHamConfig() {
   }    
}