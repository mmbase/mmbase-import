package nl.leocms.applications;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/* This class contains the settings specific for the set of templates in the nmintra folder
 */

public class NMIntraConfig {
   
   private static Logger log = Logging.getLoggerInstance(NMIntraConfig.class.getName());
   
   // used for creating the options in the /editors/beheerbibliotheek
   public final static String[] CONTENTELEMENTS = {
      "artikel",
      "attachments",
      "educations",
      "evenement_blueprint",
      "images",
      "items",
      "link",
      "medewerkers",
      "pagina",
      "products",
      "projects",
      "teaser",
      "vacature",
      "vraagbaak"
   };
   
   // used to determine in which objects a contentelement is used (ContentHelper.usedInItems)
   public final static String[] CONTAINERS = {
      "paragraaf",
      "producttypes",
      "contentblocks",
      "linklijst"
   };
   
   // used to determine to which pagina an object belongs
   // see ContentHelper.addDefaultRelations and PaginaHelper.getContentElementNode, findIDs, etc
   public final static String[] OBJECTS = {
      "ads",            // extends object
      "artikel",
      "artikel#",
      "artikel##",
      "documents",      // extends object
      "formulier",      // extends object
      "forums",         // extends object
      "items",
      "link",
      "link#",
      "linklijst",      // extends object
      "locations",
      "medewerkers",
      "products",
      "products#",
      "producttypes",   // extends object
      "teaser",
      "vacature"
   };
   
   public final static String[] PATHS_FROM_PAGE_TO_OBJECTS = {
      "object,contentrel,pagina",                                     // ads, lucene index -1
      "object,contentrel,pagina",                                     // artikel, lucene index 0
      "object,readmore,pagina",                                       // artikel (ippolygon.jsp & vacature_info.jsp)
      "object,pos4rel,images,posrel,pagina",                          // artikel (ipoverview.jsp)
      "object,posrel,pagina",                                         // documents, lucene index 9
      "object,posrel,pagina",                                         // formulier, lucene index -1
      "object,posrel,pagina",                                         // forums, lucene index -1
      "object,posrel,pagina",                                         // items (shop_items.jsp), lucene index 8
      "object,posrel,contentblocks,readmore,pagina",                  // link, lucene index -1
      "object,lijstcontentrel1,linklijst,lijstcontentrel2,pagina",    // link, lucene index -1
      "object,lijstcontentrel,pagina",                                // linklijst, lucene index -1
      "object,readmore,products,posrel1,producttypes,posrel2,pagina", // locations (producttypes.jsp), lucene index -1
      "object,contentrel,pagina",                                     // medewerkers, lucene index -1
      "object,posrel1,producttypes,posrel2,pagina",                   // products (producttypes.jsp), lucene index 7
      "object,posrel1,producttypes,posrel2,teaser,rolerel,pagina",    // products (producttypes.jsp), lucene index 7
      "object,posrel,pagina",                                         // producttypes, lucene index 6
      "object,rolerel,pagina",                                        // teaser, lucene index 5
      "object,contentrel,pagina"                                      // vacature, lucene index 10
   };
   
   public static int PARENT_LAYOUT = -1;
   public static int DEFAULT_LAYOUT = 0;
   public static int SUBSITE1_LAYOUT = 1;
   
   public static String [] layout = {"Natuurmonumenten", "Landschap Noord-Holland" };
   
   public static int PARENT_STYLE = -1;
   public static int DEFAULT_STYLE = 4;
   
   // lnh_stjansvlinder and lnh_spreeuwenverg should not be used
   public static String [] style1 = {
      "rode_zee",         "groene_zee",       "blauwe_zee",     "bibliotheek", "blauw_wad",  "gele_zee", "groene_boomrand",
      "geel_strand",      "oranje_helmgras",  "oranje_pompoen", "mieren",      "lnh_libelle","lnh_molen","lnh_schermerhorn",
      "lnh_stjansvlinder","lnh_spreeuwenverg","lnh_wierdijk"  , "lnh_mieren",  "lnh_libellegeel", "vraagbaak" };
   public static String [] color1 = {
      "#1E0064",          "#2C620C",          "#6585DF",        "#1E0064",     "#458FC9",    "#1E0064",  "#000099",
      "#A95500",          "#FF8E00",          "#FD7F00",        "#3F9D20",     "#3FAFB3",    "#A37C1C",  "#84854C",
      "#224D7E",          "#B94036",          "#76A043",        "#76A043",     "#A17C1B",    "#1E0064" };
   public static String [] color2 = {
      "#963A29",          "#3F9D20",          "#506BB5",        "#1E0064",     "#458FC9",    "#E15603",  "#6C6D01",
      "#BC9610",          "#CB8631",          "#CC6C0A",        "#3F9D20",     "#9DDAEC",    "#A37C1C",  "#B5B666",
      "#799FCC",          "#B94036",          "#A3D568",        "#A6C185",     "#EDC047",    "#1E0064" };
   public static String [] color3 = {
      "#EA5C3D",          "#6B9F30",          "#6585DF",        "#81A5DC",     "#80BFF0",    "#EDBD22",  "#000099",
      "#A95500",          "#FF8E00",          "#FD7F00",        "#6FAD22",     "#9DDAEC",    "#DFB542",  "#B5B666",
      "#799FCC",          "#F54D3F",          "#A3D568",        "#A6C185",     "#EDC047",    "#81A5DC" };
   public static String [] color4 = {
      "#FD8D73",          "#6CD230",          "#96A9DE",        "#81A5DC",     "#ADD3F0",    "#FFD54F",  "#9999CC",
      "#F5D84A ",         "#F3BF7D",          "#FEDFBF",        "#86C438",     "#9DDAEC",    "#DFB542",  "#B5B666",
      "#799FCC",          "#F54D3F",          "#A3D568",        "#A6C185",     "#EDC047",    "#81A5DC" };
   
   public static String cssPath = "css/";
   
   // Natuurmonumenten
   public static boolean showFirstSubpage = false;
   
   public static String getFromEmailAddress() {
      try {
         InitialContext context = new InitialContext();
         Context env = (Context) context.lookup("java:comp/env");
         String fromEmailAddress = (String) env.lookup("nmintraconf.fromEmailAddress");
         return fromEmailAddress;
      } catch (NamingException ne) {
         log.info("Context not found: " + ne.toString());
         return null;
      }
   }
   
   public static String getDefaultPZAddress() {
      try {
         InitialContext context = new InitialContext();
         Context env = (Context) context.lookup("java:comp/env");
         String defaultPZAddress = (String) env.lookup("nmintraconf.defaultPZAddress");
         return defaultPZAddress;
      } catch (NamingException ne) {
         log.info("Context not found: " + ne.toString());
         return null;
      }
   }
   
   public static String getDefaultFZAddress() {
      try {
         InitialContext context = new InitialContext();
         Context env = (Context) context.lookup("java:comp/env");
         String defaultFZAddress = (String) env.lookup("nmintraconf.defaultFZAddress");
         return defaultFZAddress;
      } catch (NamingException ne) {
         log.info("Context not found: " + ne.toString());
         return null;
      }
   }
   
   public static String getToEmailAddress() {
      try {
         InitialContext context = new InitialContext();
         Context env = (Context) context.lookup("java:comp/env");
         String toEmailAddress = (String) env.lookup("nmintraconf.toEmailAddress");
         return toEmailAddress;
      } catch (NamingException ne) {
         log.info("Context not found: " + ne.toString());
         return null;
      }
   }
   
   public static String getNewsEmailAddress() {
      try {
         InitialContext context = new InitialContext();
         Context env = (Context) context.lookup("java:comp/env");
         String newsEmailAddress = (String) env.lookup("nmintraconf.newsEmailAddress");
         return newsEmailAddress;
      } catch (NamingException ne) {
         log.info("Context not found: " + ne.toString());
         return null;
      }
   }  
   
   public static String getGisEmailAddress() {
      try {
         InitialContext context = new InitialContext();
         Context env = (Context) context.lookup("java:comp/env");
         String gisEmailAddress = (String) env.lookup("nmintraconf.gisEmailAddress");
         return gisEmailAddress;
      } catch (NamingException ne) {
         log.info("Context not found: " + ne.toString());
         return null;
      }
   }
   
   public static String getSDocumentsUrl() {
      try {
         InitialContext context = new InitialContext();
         Context env = (Context) context.lookup("java:comp/env");
         String sDocumentsUrl = (String) env.lookup("nmintraconf.sDocumentsUrl");
         return sDocumentsUrl;
      } catch (NamingException ne) {
         log.info("Context not found: " + ne.toString());
         return null;
      }
   }
   
   public static String getSDocumentsRoot() {
      try {
         InitialContext context = new InitialContext();
         Context env = (Context) context.lookup("java:comp/env");
         String sDocumentsRoot = (String) env.lookup("nmintraconf.sDocumentsRoot");
         return sDocumentsRoot;
      } catch (NamingException ne) {
         log.info("Context not found: " + ne.toString());
         return null;
      }
   }
   
   public static String getIncomingDir() {
      try {
         InitialContext context = new InitialContext();
         Context env = (Context) context.lookup("java:comp/env");
         String incomingDir = (String) env.lookup("nmintraconf.incomingDir");
         return incomingDir;
      } catch (NamingException ne) {
         log.info("Context not found: " + ne.toString());
         return null;
      }
   }
   
   public static String getTempDir() {
      try {
         InitialContext context = new InitialContext();
         Context env = (Context) context.lookup("java:comp/env");
         String tempDir = (String) env.lookup("nmintraconf.tempDir");
         return tempDir;
      } catch (NamingException ne) {
         log.info("Context not found: " + ne.toString());
         return null;
      }
   }
   
   public static String getSCorporateWebsite() {
      try {
         InitialContext context = new InitialContext();
         Context env = (Context) context.lookup("java:comp/env");
         String sCorporateWebsite = (String) env.lookup("nmintraconf.sCorporateWebsite");
         return sCorporateWebsite;
      } catch (NamingException ne) {
         log.info("Context not found: " + ne.toString());
         return null;
      }
   }
   
   public static String getSCorporateEditors() {
      try {
         InitialContext context = new InitialContext();
         Context env = (Context) context.lookup("java:comp/env");
         String sCorporateEditors = (String) env.lookup("nmintraconf.sCorporateEditors");
         return sCorporateEditors;
      } catch (NamingException ne) {
         log.info("Context not found: " + ne.toString());
         return null;
      }
   }
   
   // Landschap Noord-Holland
   /*
   public static boolean showFirstSubpage = true;
    
   public static String fromEmailAddress = "intranet@landschapnoordholland.nl";
   public static String defaultPZAddress = "intranet@landschapnoordholland.nl";
   public static String defaultFZAddress = "intranet@landschapnoordholland.nl";
   public static String toEmailAddress   = "beheerder@landschapnoordholland.nl";
   public static String newsEmailAddress = "intranieuws@landschapnoordholland.nl";
    
   public static String sDocumentsUrl = "/documents/Intranet";
   public static String sDocumentsRoot = "D:/apps/Tomcat_Intranet/webapps/documents/Intranet";
   public static String incomingDir = "E:/Intranet_Input/";
   public static String tempDir = "E:/Intranet_Temp/";
   public static String sCorporateWebsite = "http://www.landschapnoordholland.nl/";
   public static String sCorporateEditors = "http://www.landschapnoordholland.nl/editors/";
    */
   
   // Development
   /*
   public static boolean showFirstSubpage = true;
    
   public static String fromEmailAddress = "intranet@landschapnoordholland.nl";
   public static String defaultPZAddress = "hangyi@xs4all.nl";
   public static String defaultFZAddress = "hangyi@xs4all.nl";
   public static String toEmailAddress   = "hangyi@xs4all.nl";
   public static String newsEmailAddress = "hangyi@xs4all.nl";
    
   public static String sDocumentsUrl = "/documents/Intranet";
   public static String sDocumentsRoot = "C:/data/natmm/webapps/documents/Intranet";
   public static String incomingDir =  "C:/data/natmm/incoming/";
   public static String tempDir = "C:/temp/";
   public static String sCorporateWebsite = "http://www.acc.natuurmm.asp4all.nl/";
   public static String sCorporateEditors = "http://www.acc.natuurmm.asp4all.nl/editors/";
    */
   public NMIntraConfig() {
   }
}