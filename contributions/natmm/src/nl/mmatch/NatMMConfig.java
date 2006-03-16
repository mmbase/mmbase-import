package nl.mmatch;

/* This class contains the www.natuurmonumenten.nl specific settings
*/

public class NatMMConfig {
   
    public static String adminAccount = "admin";
    public static String adminPassword = "<removed>";
    public static String fromEmailAddress = "website@natuurmonumenten.nl";
    public static String fromCADAddress = "denatuurin@natuurmonumenten.nl";
    public static String toEmailAddress = "hangyi@xs4all.nl";
    public static String liveUrl = "http://www.natuurmonumenten.nl/";
    public static String tmpMemberId = "9002162";
    public static boolean urlConversion = true;
  

    public static String rootDir = "/export/www/natuurmm/jakarta-tomcat/webapps/ROOT/WEB-INF/data/";
    public static String tempDir = "/export/www/natuurmm/jakarta-tomcat/temp/";
    public static String incomingDir = "/home/import/incoming/";    

    /*  
    public static String rootDir =  "C:/data/natmm/incoming/";
    public static String tempDir = "C:/temp/";
    public static String incomingDir =  "C:/data/natmm/incoming/";
    */
   
    public NatMMConfig() {
    }    
}