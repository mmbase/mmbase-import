package nl.mmatch;

/* This class contains the server specific settings
*/

public class NMIntraConfig {
   
    public static String adminAccount = "admin";
    public static String adminPassword = "admin2k";
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
    public static String rootDir =  "C:/data/nmintra/webapps/ROOT/WEB-INF/data/";
    public static String sDocumentsRoot = "C:/data/nmintra/webapps/ROOT/PenO";
    public static String sCorporateWebsite = "http://www.acc.natuurmm.asp4all.nl/";
    public static String sCorporateEditors = "http://www.acc.natuurmm.asp4all.nl/editors/";

    public NMIntraConfig() {
    }    
}