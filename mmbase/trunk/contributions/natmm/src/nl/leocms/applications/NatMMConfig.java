package nl.leocms.applications;

/* This class contains settings specific for the set of templates in the natmm folder
*/

public class NatMMConfig {

   public final static String[] CONTENTELEMENTS = {
      "artikel",
      "attachments",
      "images",
      "link",
      "natuurgebieden",
      "organisatie",
      "pagina",
      "panno",
      "persoon",
      "provincies",
      "shorty",
      "teaser",
      "vacature",
      "vgv"
   };

   public final static String[] CONTAINERS = {
      "dossier",
      "linklijst",
      "paragraaf"
   };

   public final static String[] OBJECTS = {
      "ads",            // extends object
      "artikel",
      "artikel#",       // '#' is used to denote alternative paths to this contentelement
      "dossier",        // extends object
      "images",
      "link",
      "linklijst",      // extends object
      "natuurgebieden",
      "persoon",
      "provincies",
      "vacature"
   };

   public final static String[] PATHS_FROM_PAGE_TO_OBJECTS = {
      "object,contentrel,pagina",                                    // ads
      "object,contentrel,pagina",                                    // artikel
      "object,posrel1,dossier,posrel2,pagina",                       // artikel
      "object,posrel,pagina",                                        // dossier
      "object,posrel1,dossier,posrel2,pagina",                       // images
      "object,lijstcontentrel,linklijst,posrel,pagina",              // link
      "object,posrel,pagina",                                        // linklijst
      "object,pos4rel,provincies,contentrel,pagina",                 // natuurgebieden
      "object,contentrel,pagina",                                    // persoon
      "object,contentrel,pagina",                                    // provincies
      "object,contentrel,pagina"                                     // vacature
   };


   // there is a difference between layout and style:
   // - style are the colours and background images set in the css
   // - layout is the layout of blocks on the template
   // style set for every rubriek, layout may have value parent_layout

   public static int PARENT_STYLE = -1;
   public static int DEFAULT_STYLE = 7;

   public static int PARENT_LAYOUT = -1;
   public static int DEFAULT_LAYOUT = 0;
   public static int SUBSITE1_LAYOUT = 1;
   public static int SUBSITE2_LAYOUT = 2;
   public static int SUBSITE3_LAYOUT = 3;
   public static int DEMO_LAYOUT = 4;

   public static String [] layout = {"Natuurmonumenten", "Naardermeer", "ING-Perspectief", "Actiesite", "Demo" };
   public static String [] style1 = {"vereniging","steun" ,"nieuws","natuurin","natuurgebieden","links" ,"fun"   ,"default","zoeken","winkel","vragen","naardermeer" };
   public static String [] color1 = {"552500"    ,"990100","4A7934","D71920"  ,"BAC42B"        ,"9C948C","EC008C","1D1E94" ,"00AEEF","F37021","6C6B5C","F37021" }; // color + line leftnavpage
   public static String [] color2 = {"E4BFA3"    ,"F7D6C3","B0DF9B","FFBDB7"  ,"EEF584"        ,"EDE9E6","FABFE2","96ADD9" ,"B2E7FA","FED9B2","D6D6D1","F9B790" }; // background leftnavpage_high
   public static String [] color3 = {"050080"    ,"050080","050080","050080"  ,"050080"        ,"050080","050080","FFFFFF" ,"050080","050080","050080","FFFFFF"};  // footer links

   public static String cssPath = "hoofdsite/themas/";

   // Natuurmonumenten
   public static boolean urlConversion = true;
   public static boolean checkEmailByMailHost = false;
   public static boolean hasClosedUserGroup = false;
   public static boolean useCreationDateInURL = false;
   public static boolean isUISconnected = false;

   public static String companyName = "Natuurmonumenten";
   public static String fromEmailAddress = "website@natuurmonumenten.nl";
   public static String fromCADAddress = "denatuurin@natuurmonumenten.nl";
   public static String infoEmailAddress = "info@ledenservice.nl";

   public static String toEmailAddress = "hangyi@xs4all.nl";
   public static String [] liveUrl = { "http://www.natuurmonumenten.nl/", "http://www.prod.natuurmm.asp4all.nl/" };
   public static String tmpMemberId = "9002162";

   public static String toSubscribeAddress = "AanmeldingLidmaatschap@Natuurmonumenten.nl";
   public static String rootDir = "/export/www/natuurmm/jakarta-tomcat/webapps/ROOT/";
   public static String tempDir = "/export/www/natuurmm/jakarta-tomcat/temp/";
   public static String incomingDir = "/home/import/incoming/";

   // Life Line demo site
   /*
   public static boolean urlConversion = true;
   public static boolean checkEmailByMailHost = false;
   public static boolean hasClosedUserGroup = true;
   public static boolean useCreationDateInURL = true;
   public static boolean isUISconnected = true;

   public static String companyName = "Life Line";
   public static String fromEmailAddress = "demo@mediacompetence.com";
   public static String fromCADAddress = "demo@mediacompetence.com";
   public static String infoEmailAddress = "info@mediacompetence.com";
   public static String toEmailAddress = "hangyi@xs4all.nl";
   public static String [] liveUrl = { "http://demo.mediacompetence.com/" };
   public static String tmpMemberId = "9002162";

   public static String toSubscribeAddress = "AanmeldingLidmaatschap@mediacompetence.com";
   public static String rootDir = "C:/Apache/Tomcat/webapps/ROOT";
   public static String tempDir = "C:/temp/";
   public static String incomingDir = "C:/data/natmm/incoming/";
   */

   public NatMMConfig() {
   }
}
