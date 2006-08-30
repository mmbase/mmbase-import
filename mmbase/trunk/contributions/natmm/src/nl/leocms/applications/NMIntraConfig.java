package nl.leocms.applications;

import org.mmbase.bridge.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

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
      "object,readmore,pagina",                       // artikel (ippolygon.jsp)
      "object,posrel,pagina",                         // documents
      "object,posrel,pagina",                         // formulier
      "object,posrel,pagina",                         // forums
      "object,posrel,pagina",                         // items (shop_items.jsp)
      "object,posrel,pagina",                         // producttypes
      "object,contentrel,pagina"                      // vacature
   };
	
   public static int PARENT_STYLE = -1;
	public static int DEFAULT_STYLE = 4;
	
	public static String [] style1 = { "rode_zee","groene_zee","blauwe_zee","bibliotheek","blauw_wad","gele_zee","groene_boomrand","geel_strand","oranje_helmgras","oranje_pompoen","mieren" };
	public static String [] color1 = {"#1E0064", "#2C620C",	"#6585DF",	 "#1E0064",    "#458FC9",	"#1E0064", "#999A01",		  "#A95500",    "#FF8E00",        "#FD7F00",       "#1E0064" };
   public static String [] color2 = {"#963A29", "#3F9D20",	"#506BB5",	 "#1E0064",    "#458FC9",	"#E15603", "#6C6D01",		  "#BC9610",    "#CB8631",        "#CC6C0A",       "#3F9D20" };
   public static String [] color3 = {"#EA5C3D", "#6B9F30",	"#6585DF",	 "#81A5DC",    "#80BFF0",	"#EDBD22", "#999A01",		  "#A95500 ",   "#FF8E00",        "#FD7F00",       "#6FAD22" };
   public static String [] color4 = {"#FD8D73", "#6CD230",	"#96A9DE",	 "#81A5DC",    "#ADD3F0",	"#FFD54F", "#B7BE1F",		  "#F5D84A ",   "#F3BF7D",        "#FEDFBF",       "#86C438" };

	public static String cssPath = "css/";
	
   public static String fromEmailAddress = "intranet@natuurmonumenten.nl";
   public static String sDocumentsUrl = "/documents/PenO";
   
   public static String sDocumentsRoot = "D:/apps/Tomcat_Intranet/webapps/documents/PenO";
   public static String toEmailAddress = "beheerder@natuurmonumenten.nl";
   public static String rootDir = "E:/Intranet_Input/";
   public static String sCorporateWebsite = "http://www.natuurmonumenten.nl/";
   public static String sCorporateEditors = "http://www.natuurmonumenten.nl/editors/";
   
   /*    
   public static String sDocumentsRoot = "C:/data/nmintra/webapps/documents/PenO";
   public static String toEmailAddress = "hangyi@xs4all.nl";    
   public static String rootDir =  "C:/data/nmintra/webapps/ROOT/WEB-INF/data/";
   public static String sCorporateWebsite = "http://www.acc.natuurmm.asp4all.nl/";
   public static String sCorporateEditors = "http://www.acc.natuurmm.asp4all.nl/editors/";
   */

   public NMIntraConfig() {

   }
}