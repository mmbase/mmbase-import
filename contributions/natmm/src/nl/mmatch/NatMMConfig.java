package nl.mmatch;

/* This class contains settings specific for the set of templates in the natmm folder
*/

public class NatMMConfig {
   
   public final static String[] CONTENTELEMENTS = {  
      "dossier",
      "natuurgebieden",
      "provincies",
      "artikel",
      "artikel#", // '#' is used to denote alternative paths to this contentelement
      "images",
      "persoon",
      "ads"
   };
   
   public final static String[] PATHS_FROM_PAGE_TO_ELEMENTS = {  
      "object,posrel,pagina",                         // dossier
      "object,pos4rel,provincies,contentrel,pagina",  // natuurgebieden
      "object,contentrel,pagina",                     // provincies
      "object,contentrel,pagina",                     // artikel
      "object,posrel,dossier,posrel,pagina",          // artikel
      "object,posrel,dossier,posrel,pagina",          // images
      "object,contentrel,pagina",                     // persoon
      "object,contentrel,pagina",                     // ads
   };

   
    public static String fromEmailAddress = "website@natuurmonumenten.nl";
    public static String fromCADAddress = "denatuurin@natuurmonumenten.nl";
    public static String infoEmailAddress = "info@ledenservice.nl";

    public static String toEmailAddress = "hangyi@xs4all.nl";
    public static String [] liveUrl = { "http://www.natuurmonumenten.nl/", "http://www.prod.natuurmm.asp4all.nl/" };
    public static String tmpMemberId = "9002162";
    public static boolean urlConversion = false;

    public static String toSubscribeAddress = "AanmeldingLidmaatschap@Natuurmonumenten.nl";
    public static String rootDir = "/export/www/natuurmm/jakarta-tomcat/webapps/ROOT/WEB-INF/data/";
    public static String tempDir = "/export/www/natuurmm/jakarta-tomcat/temp/";
    public static String incomingDir = "/home/import/incoming/";

    /*
    public static String toSubscribeAddress = "hangyi@xs4all.nl";
    public static String rootDir =  "C:/data/natmm/incoming/";
    public static String tempDir = "C:/temp/";
    public static String incomingDir =  "C:/data/natmm/incoming/";
    */
   
    public NatMMConfig() {
    }    
}