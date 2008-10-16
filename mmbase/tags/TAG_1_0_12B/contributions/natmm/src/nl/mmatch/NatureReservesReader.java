package nl.mmatch;

import java.util.*;
import java.io.*;
import java.text.*;
import java.util.zip.*;
import javax.servlet.*;
import org.mmbase.bridge.*;
import org.mmbase.module.core.*;
import org.mmbase.util.logging.*;
import com.finalist.mmbase.util.CloudFactory;

import nl.leocms.evenementen.Evenement;
import nl.leocms.applications.NatMMConfig;
import nl.leocms.util.ApplicationHelper;

/**
 * @ author Henk Hangyi (MMatch)
 */
public class NatureReservesReader implements Runnable {
	
   private String loadNatureReserves(Cloud cloud, String dataFile){
      String logMessage = "";  
      // *** read the input file with nature reserve (Natuurgebieden) ***
      try {
      BufferedReader dataFileReader = new BufferedReader(new FileReader(dataFile));
      String nextLine = dataFileReader.readLine();
      if(nextLine.indexOf("natuurgebiednummer;natuurgebiednaam;oppervlakte;centerpunt_x;centerpunt_y")==-1) {
         log.info("natuurgebiednummer;natuurgebiednaam;oppervlakte;centerpunt_x;centerpunt_y" + dataFile);
      }
      
      int entries=0;
      int nOfAreas = 0;
      int nOfCPX = 0;
      int nOfCPY = 0;
      
      nextLine = dataFileReader.readLine();
      while(nextLine!=null&&!nextLine.equals("")) {
         int bPos = 0; int ePos = nextLine.indexOf(";"); 
         String number = nextLine.substring(bPos,ePos);
         bPos = ePos+1; ePos = nextLine.indexOf(";",bPos); 
         String name = nextLine.substring(bPos,ePos);
         if(name!=null&&!name.equals("")) {
            name = name.substring(1,name.length()-1);
         }
         bPos = ePos+1; ePos = nextLine.indexOf(";",bPos); 
         String area = nextLine.substring(bPos,ePos);
         if(area!=null&&!area.equals("")) {
            area = area.substring(1,area.length()-1);
         }
         if(area!=null&&!area.equals("")) nOfAreas++;
         bPos = ePos+1; ePos = nextLine.indexOf(";",bPos); 
         String centerPointX = nextLine.substring(bPos,ePos);
         if(centerPointX!=null&&!centerPointX.equals("")) nOfCPX++;
         bPos = ePos+1;
         String centerPointY = nextLine.substring(bPos);
         if(centerPointY!=null&&!centerPointY.equals("")) nOfCPY++;
         
         log.info(number+"|"+name+"|"+area+"|"+centerPointX+"|"+centerPointY);
         
         nextLine = dataFileReader.readLine();
         entries++;
      }
      dataFileReader.close();
      
      logMessage += "\n<br>Number of nature reserves read from Natuurgebieden.cvs: " + entries
                 + "\n<br>Areas=" + nOfAreas + " CenterPointsX=" + nOfCPX + " CenterPointsY=" + nOfCPY;
      
      } catch(Exception e) {
      log.info(e);
      }
      return logMessage;
   } 
   
   private static final Logger log = Logging.getLoggerInstance(NatureReservesReader.class);

   public void readFiles(Cloud cloud) { 
                
        String logSubject = "Log import website";

        String toEmailAddress = NatMMConfig.getToEmailAddress();
        String fromEmailAddress = NatMMConfig.getFromEmailAddress(); 
        String root = NatMMConfig.getRootDir();
        String incoming = NatMMConfig.getIncomingDir();
        String temp = NatMMConfig.getTempDir();
        
        String natureReservesFile = root + "Natuurgebieden.csv";
            
        try {
            
            log.info("Started import of: " + natureReservesFile);
            String logMessage =  "\n<br>Started import at " + new Date();
            
            logMessage += loadNatureReserves(cloud,natureReservesFile);
           
            logMessage += "\n<br>Finished import at " + new Date();
            
            Node emailNode = cloud.getNodeManager("email").createNode();
            emailNode.setValue("to", toEmailAddress);
            emailNode.setValue("from", fromEmailAddress);
            emailNode.setValue("subject", logSubject);
            emailNode.setValue("replyto", fromEmailAddress);
            emailNode.setValue("body","<multipart id=\"plaintext\" type=\"text/plain\" encoding=\"UTF-8\"></multipart>"
                            + "<multipart id=\"htmltext\" alt=\"plaintext\" type=\"text/html\" encoding=\"UTF-8\">"
                            + "<html>" + logMessage + "</html>"
                            + "</multipart>");
            emailNode.commit();
            emailNode.getValue("mail(oneshot)");
            
            log.info(logMessage);
            log.info("Finished import of: " + natureReservesFile);
            
        } catch(Exception e) {
            log.info(e);
        }
    }
    
    private Thread getKicker(){
       Thread  kicker = Thread.currentThread();
       if(kicker.getName().indexOf("NatureReservesReaderThread")==-1) {
            kicker.setName("NatureReservesReaderThread / " + (new Date()));
            kicker.setPriority(Thread.MIN_PRIORITY+1); // *** does this help ?? ***
       }
       return kicker;
    }
    
    public NatureReservesReader() {
      Thread kicker = getKicker();
      log.info("NatureReservesReader(): " + kicker);
    }
    
    public void run () {
      Thread kicker = getKicker();
      log.info("run(): " + kicker);
		Cloud	cloud = CloudFactory.getCloud();
		ApplicationHelper ap = new ApplicationHelper(cloud);
		if(ap.isInstalled("NatMM")) {
			readFiles(cloud);
		}
    }
}