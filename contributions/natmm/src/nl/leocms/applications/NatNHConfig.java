package nl.leocms.applications;

/* This class contains settings specific for the set of templates in the natmm folder
*/

public class NatNHConfig {
   
   public final static String[] CONTENTELEMENTS = {  
      "artikel",
     };
   
   public final static String[] PATHS_FROM_PAGE_TO_ELEMENTS = {  
      "object,contentrel,pagina",                     // artikel
   };
	
	public static int DEFAULT_STYLE = -1;
	public static String [] style1 = { "natuurherstel" };

	public static String cssPath = "css/";
	
   public static boolean urlConversion = true;

   /*
   public static String rootDir = "/export/www/natuurmm/jakarta-tomcat/webapps/ROOT/WEB-INF/data/";
    */

   public static String rootDir =  "C:/data/natmm/webapps/ROOT/WEB-INF/data/";
	 
   public NatNHConfig() {
   }    
}