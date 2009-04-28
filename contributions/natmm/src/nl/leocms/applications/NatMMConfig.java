package nl.leocms.applications;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/* This class contains settings specific for the set of templates in the natmm folder */

public class NatMMConfig {
   
   private static Logger log = Logging.getLoggerInstance(NatMMConfig.class.getName());
   
   public NatMMConfig() {
   }
   
   public final static String[] CONTENTELEMENTS = {
      "artikel",
      "attachments",
      "images",
      "items",
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
      "items",
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
      "object,posrel,pagina",                                        // items
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
   public static boolean checkEmailByMailHost = false; // checking email by host can gives unacceptable long delays in booking on events
   public static boolean hasClosedUserGroup = false;
   public static boolean useCreationDateInURL = false;
   public static boolean isUISconnected = false;
   
   public static String getCompanyName() {
       try {
         InitialContext context = new InitialContext();
         Context env = (Context) context.lookup("java:comp/env");
         String companyName = (String) env.lookup("natmmconf.companyName");
         return companyName;
      }
      catch (NamingException ne) {
         log.info("Context not found: " + ne.toString());
         return null;
      }
   }
   
   public static String getFromEmailAddress() {
       try {
         InitialContext context = new InitialContext();
         Context env = (Context) context.lookup("java:comp/env");
         String fromEmailAddress = (String) env.lookup("natmmconf.fromEmailAddress");
         return fromEmailAddress;
      }
      catch (NamingException ne) {
         log.info("Context not found: " + ne.toString());
         return null;
      }
   }
   
   public static String getFromCADAddress() {
       try {
         InitialContext context = new InitialContext();
         Context env = (Context) context.lookup("java:comp/env");
         String fromCADAddress = (String) env.lookup("natmmconf.fromCADAddress");
         return fromCADAddress;
      }
      catch (NamingException ne) {
         log.info("Context not found: " + ne.toString());
         return null;
      }
   }
   
   public static String getInfoUrl() {
       try {
         InitialContext context = new InitialContext();
         Context env = (Context) context.lookup("java:comp/env");
         String infoUrl = (String) env.lookup("natmmconf.infoUrl");
         return infoUrl;
      }
      catch (NamingException ne) {
         log.info("Context not found: " + ne.toString());
         return null;
      }
   }
   
   public static String getToEmailAddress() {
       try {
         InitialContext context = new InitialContext();
         Context env = (Context) context.lookup("java:comp/env");
         String toEmailAddress = (String) env.lookup("natmmconf.toEmailAddress");
         return toEmailAddress;
      }
      catch (NamingException ne) {
         log.info("Context not found: " + ne.toString());
         return null;
      }
   }
   
   public static String getLiveUrl() {
       try {
         InitialContext context = new InitialContext();
         Context env = (Context) context.lookup("java:comp/env");
         String liveUrl = (String) env.lookup("natmmconf.liveUrl");
         return liveUrl;
      }
      catch (NamingException ne) {
         log.info("Context not found: " + ne.toString());
         return null;
      }
   }
   
   public static String getTmpMemberId() {
       try {
         InitialContext context = new InitialContext();
         Context env = (Context) context.lookup("java:comp/env");
         String tmpMemberId = (String) env.lookup("natmmconf.tmpMemberId");
         return tmpMemberId;
      }
      catch (NamingException ne) {
         log.info("Context not found: " + ne.toString());
         return null;
      }
   }
   
   public static String getToSubscribeAddress() {
       try {
         InitialContext context = new InitialContext();
         Context env = (Context) context.lookup("java:comp/env");
         String toSubscribeAddress = (String) env.lookup("natmmconf.toSubscribeAddress");
         return toSubscribeAddress;
      }
      catch (NamingException ne) {
         log.info("Context not found: " + ne.toString());
         return null;
      }
   }
   
   public static String getRootDir() {
       try {
         InitialContext context = new InitialContext();
         Context env = (Context) context.lookup("java:comp/env");
         String rootDir = (String) env.lookup("natmmconf.rootDir");
         return rootDir;
      }
      catch (NamingException ne) {
         log.info("Context not found: " + ne.toString());
         return null;
      }
   }
   
   public static String getTempDir() {
      try {
         InitialContext context = new InitialContext();
         Context env = (Context) context.lookup("java:comp/env");
         String tempDir = (String) env.lookup("natmmconf.tempDir");
         return tempDir;
      }
      catch (NamingException ne) {
         log.info("Context not found: " + ne.toString());
         return null;
      }
   }
   
   public static String getIncomingDir() {
      try {
         InitialContext context = new InitialContext();
         Context env = (Context) context.lookup("java:comp/env");
         String incomingDir = (String) env.lookup("natmmconf.incomingDir");
         return incomingDir;
      }
      catch (NamingException ne) {
         log.info("Context not found: " + ne.toString());
         return null;
      }
   }
   
   public static boolean isProductionApplication() {
      try {
         InitialContext context = new InitialContext();
         Context env = (Context) context.lookup("java:comp/env");
         boolean isProductionApplication = "true".equals((String) env.lookup("natmmconfig.is.production")) ? true : false;
         return isProductionApplication;
      }
      catch (NamingException ne) {
         log.debug("Context not found: " + ne.toString());
         return false;
      }
   }

   public static String getAllowedIPRanges() {
      try {
         InitialContext context = new InitialContext();
         Context env = (Context) context.lookup("java:comp/env");
         String allowedIPRanges = (String) env.lookup("natmmconfig.webservice.ip.ranges.allowed");
         return allowedIPRanges;
      }
      catch (NamingException ne) {
         log.info("Context not found: " + ne.toString());
         return null;
      }
   }   
   
}
