package nl.mmatch;

/* This class contains the settings specific for the set of templates in the nmintra folder
*/

public class NMIntraConfig {
   /*
   Some remarks:
   - not related to a page are: educations, event_blueprints, terms, projects
   - id finding on documents related to documents is not supported
   */
   
   public final static String[] CONTENTELEMENTS = {  
      "ads",
      "artikel",
      "medewerkers",
      "vacature",
      "artikel#",
      "documents",
      "formulier",
      "forums",
      "items",
      "producttypes",
      "vacature"
   };
   
   public final static String[] PATHS_FROM_PAGE_TO_ELEMENTS = {  
      "object,contentrel,pagina",                     // ads
      "object,contentrel,pagina",                     // artikel
      "object,contentrel,pagina",                     // medewerkers
      "object,contentrel,pagina",                     // vacature
      "object,readmore,pagina",                       // artikel (ippolygon.jsp)
      "object,posrel,pagina",                         // documents
      "object,posrel,pagina",                         // formulier
      "object,posrel,pagina",                         // forums
      "object,posrel,pagina",                         // items (shop_items.jsp)
      "object,posrel,pagina",                         // producttypes
      "object,contentrel,pagina"                      // vacature
   };
   
   public static String fromEmailAddress = "intranet@natuurmonumenten.nl";
   public static String sDocumentsUrl = "/PenO";
   
   /*    
   public static String toEmailAddress = "beheerder@natuurmonumenten.nl";
   public static String rootDir = "D:/Intranet_Input/";
   public static String sDocumentsRoot = "D:/WIN32APP/Tomcat_Intranet/webapps/ROOT/PenO";
   public static String sCorporateWebsite = "http://www.natuurmonumenten.nl/";
   public static String sCorporateEditors = "http://www.natuurmonumenten.nl/editors/";
   */
   
   public static String toEmailAddress = "hangyi@xs4all.nl";    
   public static String rootDir =  "C:/data/natmm/webapps/ROOT/WEB-INF/data/";
   public static String sDocumentsRoot = "C:/data/natmm/webapps/ROOT/PenO";
   public static String sCorporateWebsite = "http://www.acc.natuurmm.asp4all.nl/";
   public static String sCorporateEditors = "http://www.acc.natuurmm.asp4all.nl/editors/";
   
   public NMIntraConfig() {
      
   }    
}