package nl.leocms.applications;

import org.mmbase.bridge.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/* This class contains the settings specific for the set of templates in the nmintra folder
*/

public class NMIntraConfig {
   
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
      "vacature"
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
    "lnh_stjansvlinder","lnh_spreeuwenverg","lnh_wierdijk"  , "lnh_mieren",  "lnh_libellegeel" };
  public static String [] color1 = {
     "#1E0064",          "#2C620C",          "#6585DF",        "#1E0064",     "#458FC9",    "#1E0064",  "#000099",
     "#A95500",          "#FF8E00",          "#FD7F00",        "#3F9D20",     "#3FAFB3",    "#A37C1C",  "#84854C",
     "#224D7E",          "#B94036",          "#76A043",        "#76A043",     "#A17C1B" };
   public static String [] color2 = {
      "#963A29",          "#3F9D20",          "#506BB5",        "#1E0064",     "#458FC9",    "#E15603",  "#6C6D01",
      "#BC9610",          "#CB8631",          "#CC6C0A",        "#3F9D20",     "#9DDAEC",    "#A37C1C",  "#B5B666",
      "#799FCC",          "#B94036",          "#A3D568",        "#A6C185",     "#EDC047" };
   public static String [] color3 = {
      "#EA5C3D",          "#6B9F30",          "#6585DF",        "#81A5DC",     "#80BFF0",    "#EDBD22",  "#000099",
      "#A95500",          "#FF8E00",          "#FD7F00",        "#6FAD22",     "#9DDAEC",    "#DFB542",  "#B5B666",
      "#799FCC",          "#F54D3F",          "#A3D568",        "#A6C185",     "#EDC047" };
   public static String [] color4 = {
      "#FD8D73",          "#6CD230",          "#96A9DE",        "#81A5DC",     "#ADD3F0",    "#FFD54F",  "#9999CC",
      "#F5D84A ",         "#F3BF7D",          "#FEDFBF",        "#86C438",     "#9DDAEC",    "#DFB542",  "#B5B666",
      "#799FCC",          "#F54D3F",          "#A3D568",        "#A6C185",     "#EDC047" };

   public static String cssPath = "css/";

   // Natuurmonumenten
   public static boolean showFirstSubpage = false;

   public static String fromEmailAddress = "@nmintraconf.fromEmailAddress@";
   public static String defaultPZAddress = "@nmintraconf.defaultPZAddress@";
   public static String defaultFZAddress = "@nmintraconf.defaultFZAddress@";
   public static String toEmailAddress   = "@nmintraconf.toEmailAddress@";
   public static String newsEmailAddress = "@nmintraconf.newsEmailAddress@";

   public static String sDocumentsUrl = "@nmintraconf.sDocumentsUrl@";
   public static String sDocumentsRoot = "@nmintraconf.sDocumentsRoot@";
   public static String incomingDir = "@nmintraconf.incomingDir@";
   public static String tempDir = "@nmintraconf.tempDir@";
   public static String sCorporateWebsite = "@nmintraconf.sCorporateWebsite@";
   public static String sCorporateEditors = "@nmintraconf.sCorporateEditors@";
   
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
   
   static {
	   Logger log = Logging.getLoggerInstance(NMIntraConfig.class);
	   log.info("fromEmailAddress: " + fromEmailAddress);
	   log.info("defaultPZAddress: " + defaultPZAddress);
	   log.info("defaultFZAddress: " + defaultFZAddress);
	   log.info("toEmailAddress: " + toEmailAddress);
	   log.info("newsEmailAddress: " + newsEmailAddress);	 
   }
}