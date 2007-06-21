package nl.leocms.vastgoed;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.TreeMap;

import org.mmbase.util.logging.Logging;
import org.mmbase.util.logging.Logger;
import org.mmbase.bridge.Cloud;
import com.finalist.mmbase.util.CloudFactory;
import nl.leocms.util.ApplicationHelper;


public class NelisReader
{ 
    private Map natGebMap;
    private Map gebiedMap;
    private long timeStamp;
    private final static long  EXPIRE_INTERVAL = 2 * 60 * 60 * 1000;  //2 * 60 * 1000; //2 * 60 * 60 * 1000; //two hours of refresh interval till last read 
    private static final Logger log = Logging.getLoggerInstance(NelisReader.class);
    
    
    // Private constructor suppresses generation of a (public) default constructor
  private NelisReader() {}
 
  private static class SingletonHolder
  { 
    private final static NelisReader INSTANCE = new NelisReader();
  }
 
  public static NelisReader getInstance()
  {
    return SingletonHolder.INSTANCE;
  }


public Map getNatGebMap() { 
    if ((natGebMap == null) || (System.currentTimeMillis() > timeStamp + EXPIRE_INTERVAL)) {
        readData();
    }
    return natGebMap;
}

public Map getGebiedMap() {
    if ((gebiedMap == null) || (System.currentTimeMillis() > timeStamp + EXPIRE_INTERVAL)) {
        readData();
    }
    return gebiedMap;
}

private void readData() {
    //set time stamp 
    timeStamp = System.currentTimeMillis();
    log.debug("timestamp:" + timeStamp);
    
    //
    natGebMap = new TreeMap();
    gebiedMap = new TreeMap();
    
    String separator = "\\|";
    String NELIS_FILE = "nelis.csv";
    
    Cloud cloud = CloudFactory.getCloud();
    ApplicationHelper ap = new ApplicationHelper(cloud);
    String temp = ap.getTempDir();
    
    String nelisPath= temp + NELIS_FILE;
    
    try {
      BufferedReader dataFileReader = getBufferedReader(nelisPath);
      String nextLine = dataFileReader.readLine();

      while(nextLine!=null) {
          
        String selectionType = "";
        String selectionCategory = "";
        String selectionValue = "";
        
        String[] tokens = nextLine.split(separator);
        
        if (tokens.length < 3) {
            log.warn("line in Nelis file contains less then expected tokens.");
        } else {
            selectionType = tokens[0].trim();
            selectionCategory = tokens[1].trim();
            selectionValue = tokens[2].trim();
            
            addLineToMaps(selectionType, selectionCategory, selectionValue);
        }
            nextLine = dataFileReader.readLine();
        }
        dataFileReader.close();
      } catch(Exception e) {
        log.info(e);
      }
            
            
     // these bloks should move to addLineToMaps() method below       
    Map dummy = new TreeMap();
    
//    dummy.put("Harger- en Pettemerpolder", new Boolean(false));
//    dummy.put("Loterijlanden", new Boolean(false));
//    dummy.put("Nijenburg", new Boolean(false));
//    dummy.put("Weidse Polder", new Boolean(false));
//    dummy.put("etc.", new Boolean(false));
//    natGebMap.put("Kennemerland", dummy);
//    
//    dummy = new TreeMap();
//    dummy.put("Kadelanden", new Boolean(false));
//    dummy.put("Nieuwkoopse plassen", new Boolean(false));
//    dummy.put("etc.", new Boolean(false));
//    natGebMap.put("Nieuwkoop", dummy);
//    
//    dummy = new TreeMap();
//    dummy.put("Beekbergerwoud", new Boolean(false));
//    dummy.put("Hoeve Delle", new Boolean(false));
//    dummy.put("Loenense Hooilanden", new Boolean(false));
//    dummy.put("etc.", new Boolean(false));
//    natGebMap.put("Oost-Veluwe", dummy);
//    
//    dummy = new TreeMap();
//    dummy.put("Ankeveense plassen", new Boolean(false));
//    dummy.put("Loosdrechtse plassen", new Boolean(false));
//    dummy.put("Tienhovense plassen", new Boolean(false));
//    dummy.put("etc.", new Boolean(false));
//    natGebMap.put("Vechtplassen", dummy);
//    
//    dummy = new TreeMap();
//    dummy.put("Chaamse Beek", new Boolean(false));
//    dummy.put("Markdal", new Boolean(false));
//    dummy.put("Oosterheide", new Boolean(false));
//    dummy.put("etc.", new Boolean(false));
//    natGebMap.put("West-Brabant", dummy);
//    
//    dummy = new TreeMap();
//    dummy.put("Genhoes", new Boolean(false));
//    dummy.put("Geuldal", new Boolean(false));
//    dummy.put("Gulpdal", new Boolean(false));
//    dummy.put("Sint-Pietersberg", new Boolean(false));
//    dummy.put("etc.", new Boolean(false));
//    natGebMap.put("Zuid-Limburg", dummy);
//  
    //
    
//    dummy = new TreeMap();
//    dummy.put("Noordenveld", new Boolean(false));
//    dummy.put("Waddengebied", new Boolean(false));
//    dummy.put("Zuid-Drenthe", new Boolean(false));
//    dummy.put("de Wieden", new Boolean(false));
//    dummy.put("Salland", new Boolean(false));
//    dummy.put("Twente", new Boolean(false));
//    dummy.put("etc.", new Boolean(false));
//    gebiedMap.put("Eenheid", dummy);
//    
    // Provincie and Regio's are constant
    dummy = new TreeMap();
    dummy.put("Groningen/Friesland/Drenthe", new Boolean(false));
    dummy.put("Overijssel en Flevoland", new Boolean(false));
    dummy.put("Gelderland", new Boolean(false));
    dummy.put("Noord-Holland en Utrecht", new Boolean(false));
    dummy.put("Zuid-Holland en Zeeland", new Boolean(false));
    dummy.put("Noord-Brabant en Limburg", new Boolean(false));
    gebiedMap.put("Regio", dummy);
    
    dummy = new TreeMap();
    dummy.put("Groningen", new Boolean(false));
    dummy.put("Friesland", new Boolean(false));
    dummy.put("Drenthe", new Boolean(false));
    dummy.put("Overijssel", new Boolean(false));
    dummy.put("Flevoland", new Boolean(false));
    dummy.put("Gelderland", new Boolean(false));
    dummy.put("Utrecht", new Boolean(false));
    dummy.put("Noord-Holland", new Boolean(false));
    dummy.put("Zuid-Holland", new Boolean(false));
    dummy.put("Zeeland", new Boolean(false));
    dummy.put("Noord-Brabant", new Boolean(false));
    gebiedMap.put("Provincie", dummy);    

}

private void addLineToMaps(String selectionType, String selectionCategory, String selectionValue){
    
    //we need to find a way to reflect these three lines to the maps
    // 1st line semms to be the regio
    //2nd 3rd seem to fill natuurgebied(en) selections 
    //other selects : province and eenheid ???
    
    //log.info("*" + selectionType + "*" + selectionCategory + "*" + selectionValue + "*");
insertKeyToSubMap(gebiedMap, "Eenheid", selectionCategory);
    insertKeyToSubMap(natGebMap, selectionCategory, selectionValue);
    
    
    
    
}

private void insertKeyToSubMap(Map topMap, String topKey, String subKey) {
    Map subMap = (Map) topMap.get(topKey);
    if (subMap == null) {
        TreeMap temp = new TreeMap();
        temp.put(subKey, new Boolean(false));
        topMap.put(topKey, temp);
        
    } else {
        subMap.put(subKey, new Boolean(false));
    }
    
}

private BufferedReader getBufferedReader(String sFileName) throws FileNotFoundException, UnsupportedEncodingException {
    FileInputStream fin = new FileInputStream(sFileName);
    InputStreamReader isr = new InputStreamReader(fin,"ISO-8859-1");
    return new BufferedReader(isr);
  }

}