import org.mmbase.bridge.*;

public class Tester {

    public Tester() {} 
    
    public static void main(String[] args) throws Exception {
        // load the db driver
        // java.sql.DriverManager.registerDriver(new org.hsqldb.jdbcDriver());
        // java.sql.DriverManager.registerDriver(new com.microsoft.jdbc.sqlserver.SQLServerDriver());
        // java.sql.DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver())        
        java.sql.DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
        
        // set the config dir with the property
        String configPath = java.lang.System.getProperty("user.dir");
        configPath += java.io.File.separatorChar + "config";
        //configPath += java.io.File.separatorChar + "default";        
        java.lang.System.setProperty("mmbase.config", configPath);
        
        // try to get the cloud
        CloudContext cx = LocalContext.getCloudContext();
        Cloud cl = cx.getCloud("mmbase");
    } 
}