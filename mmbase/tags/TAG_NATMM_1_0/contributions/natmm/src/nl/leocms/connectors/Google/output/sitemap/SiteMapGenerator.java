package nl.leocms.connectors.Google.output.sitemap;

import com.finalist.mmbase.util.CloudFactory;
import org.mmbase.bridge.*;
import org.mmbase.module.core.*;
import org.mmbase.util.logging.*;
import nl.leocms.evenementen.*;
import nl.leocms.util.*;
import nl.leocms.util.tools.SearchUtil;
import java.util.*;
import java.util.zip.*;
import java.io.*;
import java.net.*;
import javax.servlet.*;


public class SiteMapGenerator implements Runnable{

   private static final Logger log = Logging.getLoggerInstance(SiteMapGenerator.class);

   boolean SKIP_URL_CHECKING = true;
   
   public SiteMapGenerator(){

   }

   public void generateSiteMap(Cloud cloud) {

      String sAllContent = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n\t" +
      "<sitemapindex xmlns=\"http://www.google.com/schemas/sitemap/0.84\">\n\t";

      RubriekHelper rh = new RubriekHelper(cloud);
      PaginaHelper ph = new PaginaHelper(cloud);
      ApplicationHelper ap = new ApplicationHelper(cloud);
      String sRootDir = ap.getRootDir();
      String sSiteUrl = ap.getSiteUrl();

      NodeList nlSubsites = cloud.getList(cloud.getNodeByAlias("root").getStringValue("number"),
        "rubriek,parent,rubriek2","rubriek2.number,rubriek2.url,rubriek2.naam",null,null,null,"DESTINATION",true);
      for (int i = 0; i < nlSubsites.size(); i++){
         String sUrlName = nlSubsites.getNode(i).getStringValue("rubriek2.url");
         if (sUrlName!=null&&sUrlName.indexOf(".")>-1){
            String sXMLName = getXMLName(sUrlName);
            String sContent = rubriekRun(cloud, nlSubsites.getNode(i).getStringValue("rubriek2.number"), rh, ph, ap);
            sContent = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
               "<urlset xmlns=\"http://www.google.com/schemas/sitemap/0.84\">\n\n" +
               sContent + "\n</urlset>";
            writingFile(sRootDir + "/sitemap_" + sXMLName, sContent);
            ZipUtil zu = new ZipUtil();
            zu.createArchiveFile(sRootDir + "/sitemap_" + sXMLName,"/sitemap_" + sXMLName + ".gz");
            sAllContent += "<sitemap>\n\t\t<loc>" + sSiteUrl + "/sitemap_" + sXMLName + ".gz</loc>\n\t\t";
            File f = new File(sRootDir + "/sitemap_" + sXMLName + ".gz");
            long lm = f.lastModified();
            sAllContent += getLastmod(lm) + "\t</sitemap>\n\t";
         } else {
            log.info("Rubriek " + nlSubsites.getNode(i).getStringValue("rubriek2.naam") + " does not contain a valid url");
         }

      }

      sAllContent += "</sitemapindex>";
      writingFile(sRootDir + "/sitemap_index.xml",sAllContent);

   }

   String rubriekRun (Cloud cloud, String subsiteID, RubriekHelper rh, PaginaHelper ph, ApplicationHelper ap){
      
      log.info("**** rubriekRun for " + cloud.getNode(subsiteID).getStringValue("naam"));

      Date now = new Date();	                              // time in milliseconds
      long nowSec = (now.getTime() / 1000);                 // time in MMBase time
      int quarterOfAnHour = 60*15;
      nowSec = (nowSec/quarterOfAnHour)*quarterOfAnHour;    // help the query cache by rounding to quarter of an hour

      HashMap pathsFromPageToElements =  ap.pathsFromPageToElements();
      String sRootDir = ap.getRootDir();
      String sSiteUrl = ap.getSiteUrl();
      SearchUtil su = new SearchUtil();

      TreeSet ts = new TreeSet();
      TreeMap [] nodesAtLevel = new TreeMap[10];
      nodesAtLevel[0] = new TreeMap();
      nodesAtLevel[0].put(new Integer(0),subsiteID);
      int depth = 0;
      
      String objectUrl = null;
      String sUrlElement = null;
      
      while(depth>-1&&depth<10) {
         String lastSubObject = "";
         if(nodesAtLevel[depth].isEmpty()) {
            // *** if this level is empty, try one level back ***
            depth--;
         }
         if(depth>-1&&!nodesAtLevel[depth].isEmpty()) {
            // *** take next rubriek of highest level ***
            Integer firstKey = (Integer) nodesAtLevel[depth].firstKey();
            lastSubObject =  (String) nodesAtLevel[depth].get(firstKey);
            nodesAtLevel[depth].remove(firstKey);
            depth++;

            nodesAtLevel[depth] = (TreeMap) rh.getSubObjects(lastSubObject);
            TreeMap thisSubObjects = (TreeMap) nodesAtLevel[depth].clone();

            log.info("adding rubriek " + cloud.getNode(lastSubObject).getStringValue("naam"));
            
            while(!thisSubObjects.isEmpty()) {
               Integer thisKey = (Integer) thisSubObjects.firstKey();
               String sThisObject = (String) thisSubObjects.get(thisKey);
               thisSubObjects.remove(thisKey);
               String nType = cloud.getNode(sThisObject).getNodeManager().getName();

               if(nType.equals("pagina")){
                 
                  objectUrl = sSiteUrl + ph.createPaginaUrl(sThisObject,"");
                  if (SKIP_URL_CHECKING || bUrlIsAlive(objectUrl)){
                     sUrlElement = element(objectUrl, getLastmod(cloud, sThisObject), getchangeFreq(cloud,sThisObject,nowSec), FormattedPriority(depth)); 
                     ts.add(sUrlElement);
                  } else {
                     log.info(objectUrl + " can not be found for page " +  sThisObject);
                  }

                  nodesAtLevel[depth].remove(thisKey);
                  
                  int numberOfItems = 0;
                  for (Iterator it=pathsFromPageToElements.keySet().iterator();it.hasNext();) {

                     String sBuilderName = (String) it.next();
                     String sPath = (String) pathsFromPageToElements.get(sBuilderName);
                     sBuilderName = sBuilderName.replaceAll("#","");
                     sPath = sPath.replaceAll("object",sBuilderName);
                     String sRealConstraint = su.getConstraint(sBuilderName, nowSec, quarterOfAnHour);
                     sRealConstraint += (sRealConstraint.equals("") ? "" : " AND ") + "pagina.number = " + sThisObject;
                     
                     NodeList nlObjects = cloud.getList("", sPath,sBuilderName + ".number",sRealConstraint,null,"down",null,false);
                     for (int j = 0; j < nlObjects.size(); j++){
                        String sItemNumber = nlObjects.getNode(j).getStringValue(sBuilderName +".number");
                        objectUrl = sSiteUrl + ph.createItemUrl(sItemNumber,sThisObject,null,"");
                        if (SKIP_URL_CHECKING || bUrlIsAlive(objectUrl)){
                          sUrlElement = element(objectUrl, getLastmod(cloud, sItemNumber), getchangeFreq(cloud,sItemNumber,nowSec), FormattedPriority(depth+1)); 
                          ts.add(sUrlElement);
                          numberOfItems++;
                        } else {
                          log.info(objectUrl + " can not be found for item " + sItemNumber + " on page " +  sThisObject);
                        }
                     }
                  }
                  if(ap.isInstalled("NatMM") && Evenement.isAgenda(cloud,sThisObject) ) {
                    // make an exception for the agenda page: evenementen are not related to the page
                    // see also templates/natmm/includes/zoek/resultaten and events/searchresults.jsp
                    MMBaseContext mc = new MMBaseContext();
                    ServletContext application = mc.getServletContext();
                    HashSet parentEvents = (HashSet) application.getAttribute("events");
                    if(parentEvents==null) {
                       EventNotifier.updateAppAttributes(cloud);
                       parentEvents = (HashSet) application.getAttribute("events");
                    }
                    for(Iterator ite = parentEvents.iterator(); ite.hasNext(); ) {
                        String sItemNumber = Evenement.getNextOccurence( (String) ite.next());
                        objectUrl = sSiteUrl + ph.createPaginaUrl(sThisObject,"") + "?e=" + sItemNumber;
                        if (SKIP_URL_CHECKING || bUrlIsAlive(objectUrl)){
                          sUrlElement = element(objectUrl, getLastmod(cloud, sItemNumber), "daily", FormattedPriority(depth+1)); 
                          ts.add(sUrlElement);
                          numberOfItems++;
                        } else {
                          log.info(objectUrl + " can not be found for item " + sItemNumber + " on page " +  sThisObject);
                        }
                     }
                  }
                  log.info("added page " + cloud.getNode(sThisObject).getStringValue("titel") + " with " + numberOfItems + " items");
               }
            }
         }
      }

      String sContent = "";
      Iterator it = ts.iterator();
      while (it.hasNext()) {
        sContent += (String) it.next();
      }
      return sContent;

   }

   String element(String objectUrl, String lastmod, String changefreq, String priority) {
     return "\t<url>\n\t\t<loc>" 
          + objectUrl
          + "</loc>\n\t\t" 
          + lastmod
          + "\t\t" 
          + changefreq
          + "\t\t<priority>" 
          + priority
          + "</priority>\n\t</url>\n";
   }
   
   String getLastmod(Cloud cloud,String sNumber){
      return getLastmod((cloud.getNode(sNumber).getLongValue("datumlaatstewijziging")*1000));
   }

   String getLastmod(long time){
      Calendar cal = Calendar.getInstance();
      cal.setTimeInMillis(time);
      String date = "<lastmod>20";
      if (cal.get(Calendar.YEAR) % 100 < 10) { date += "0"; }
      date += (cal.get(Calendar.YEAR) % 100) + "-";
      if (cal.get(Calendar.MONTH) + 1 < 10) { date += "0"; }
      date +=(cal.get(Calendar.MONTH) + 1) + "-";
      if (cal.get(Calendar.DAY_OF_MONTH) < 10) { date += "0"; }
      date +=(cal.get(Calendar.DAY_OF_MONTH));
      date += "</lastmod>\n";

      return date;

   }

   String FormattedPriority(int iPriority){
      String sPriority = (new Float((float)1/iPriority)).toString();
      return sPriority.substring(0,3);
   }

   void writingFile(String sFileName, String sContent){
      log.info("writingFile " + sFileName);
      try {
         File file = new File(sFileName);
         if (file.exists()) {
            file.delete();
         }
         file.createNewFile();

         FileOutputStream fos = new FileOutputStream(sFileName);
         OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
         BufferedWriter bw = new BufferedWriter(osw);
         bw.write(sContent);
         bw.close();
      } catch (Exception e){
         log.info(e.toString());
      }
   }

   String getXMLName(String sUrl){
      int iFirstIndex = sUrl.indexOf(".");
      int iSecondIndex = sUrl.indexOf(".",iFirstIndex+1);
      if ((iFirstIndex>-1)&&(iSecondIndex>-1)){
         sUrl = sUrl.substring(iFirstIndex+1,iSecondIndex);
      }
      return sUrl + ".xml";
   }

   boolean bUrlIsAlive(String URLName){
      boolean flag = false;
      try {
         HttpURLConnection.setFollowRedirects(false);
         HttpURLConnection con = (HttpURLConnection) new URL(URLName).openConnection();
         flag = (con.getResponseCode() == HttpURLConnection.HTTP_OK);
      }
      catch (Exception e) {
         log.info(e.toString());
      }
      return flag;
  }

  String getchangeFreq(Cloud cloud,String sNumber, long nowSec){
     long lastmod = (cloud.getNode(sNumber).getLongValue("datumlaatstewijziging"));

     String changeFreq = "<changefreq>";
     if ((nowSec - lastmod) < 60*60*24) { changeFreq += "daily";}
     else if ((nowSec - lastmod) < 60*60*24*7) { changeFreq += "weekly";}
     else if ((nowSec - lastmod) < 60*60*24*30) { changeFreq += "monthly";}
     else { changeFreq += "yearly";}
     changeFreq += "</changefreq>\n";

     return changeFreq;

  }

  private Thread getKicker(){
     Thread  kicker = Thread.currentThread();
     if(kicker.getName().indexOf("SiteMapGeneratorThread")==-1) {
        kicker.setName("SiteMapGeneratorThread / " + (new Date()));
        kicker.setPriority(Thread.MIN_PRIORITY+1); // *** does this help ?? ***
     }
     return kicker;
  }

  public void run () {
      Thread kicker = getKicker();
      log.info("run(): " + kicker);
      Cloud cloud = CloudFactory.getCloud();
      ApplicationHelper ap = new ApplicationHelper(cloud);
      if(ap.isInstalled("NatMM")) {
         generateSiteMap(cloud);
      }
   }

}
