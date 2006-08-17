package nl.leocms.connectors.Google.output.sitemap;

import com.finalist.mmbase.util.CloudFactory;
import org.mmbase.bridge.*;
import org.mmbase.util.logging.*;
import nl.leocms.util.*;
import java.util.*;
import java.util.zip.*;
import java.io.*;
import java.net.*;
import nl.leocms.applications.NatMMConfig;


public class SiteMapGenerator implements Runnable{

   private static final Logger log = Logging.getLoggerInstance(SiteMapGenerator.class);

   public SiteMapGenerator(){

   }

   public void generateSiteMap(Cloud cloud) {

      Date now = new Date();	                              // time in milliseconds
      long nowSec = (now.getTime() / 1000);                 // time in MMBase time
      int quarterOfAnHour = 60*15;
      nowSec = (nowSec/quarterOfAnHour)*quarterOfAnHour;    // help the query cache by rounding to quarter of an hour

      String sAllContent = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n\t" +
      "<sitemapindex xmlns=\"http://www.google.com/schemas/sitemap/0.84\">\n\t";

      RubriekHelper rubriekHelper = new RubriekHelper(cloud);
      PaginaHelper ph = new PaginaHelper(cloud);

      String sSiteUrl = NatMMConfig.liveUrl[0].substring(0,NatMMConfig.liveUrl[0].length()-1);

      NodeList nlRubrieks = cloud.getList(cloud.getNodeByAlias("root").getStringValue("number"),
        "rubriek,parent,rubriek2","rubriek2.number,rubriek2.url,rubriek2.naam",null,null,null,"DESTINATION",true);
      for (int i = 0; i < nlRubrieks.size(); i++){
         String sUrlName = nlRubrieks.getNode(i).getStringValue("rubriek2.url");
         if (sUrlName!=null&&sUrlName.indexOf(".")>-1){
            String sXMLName = getXMLName(sUrlName);
            String sContent = rubriekRun(cloud,
                                         nlRubrieks.getNode(i).getStringValue("rubriek2.number"),
                                         rubriekHelper, ph, nowSec,
                                         quarterOfAnHour, sSiteUrl);
            sContent = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
               "<urlset xmlns=\"http://www.google.com/schemas/sitemap/0.84\">\n\n" +
               sContent + "\n</urlset>";
            writingFile(NatMMConfig.rootDir + "/sitemap_" + sXMLName, sContent);
            createGZFile("sitemap_" + sXMLName);
            sAllContent += "<sitemap>\n\t\t<loc>" + sSiteUrl + "/sitemap_" + sXMLName + ".gz</loc>\n\t\t";
            File f = new File(NatMMConfig.rootDir + "/sitemap_" + sXMLName + ".gz");
            long lm = f.lastModified();
            sAllContent += getLastmod(lm) + "\t</sitemap>\n\t";
         } else {
            log.info("Rubriek " + nlRubrieks.getNode(i).getStringValue("rubriek2.naam") + " does not contain a valid url");
         }

      }

      sAllContent += "</sitemapindex>";
      writingFile(NatMMConfig.rootDir + "/sitemap_index.xml",sAllContent);

   }

   String rubriekRun (Cloud cloud, String rootID, RubriekHelper rubriekHelper, PaginaHelper ph,
                    long nowSec, int quarterOfAnHour, String url){

      log.info("rubriekRun for " + rootID);
      TreeSet ts = new TreeSet();
      TreeMap [] nodesAtLevel = new TreeMap[10];
      nodesAtLevel[0] = new TreeMap();
      nodesAtLevel[0].put(new Integer(0),rootID);
      int depth = 0;

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

            nodesAtLevel[depth] = (TreeMap) rubriekHelper.getSubObjects(lastSubObject);

            TreeMap thisSubObjects = (TreeMap) nodesAtLevel[depth].clone();

            while(!thisSubObjects.isEmpty()) {
               Integer thisKey = (Integer) thisSubObjects.firstKey();
               String sThisObject = (String) thisSubObjects.get(thisKey);
               thisSubObjects.remove(thisKey);
               String nType = cloud.getNode(sThisObject).getNodeManager().getName();

               if(nType.equals("pagina")){
                  String sUrl = "";
                  if (bUrlIsAlive(url + ph.createPaginaUrl(sThisObject,""))){
                     sUrl = "\t<url>\n\t\t<loc>" + url +
                        ph.createPaginaUrl(sThisObject, "")
                        + "</loc>\n\t\t" + getLastmod(cloud, sThisObject) +
                        "\t\t" + getChangefreq(cloud,sThisObject,nowSec) +
                        "\t\t<priority>" + FormattedPriority(depth) +
                        "</priority>\n\t</url>\n";

                     ts.add(sUrl);
                  }

                  nodesAtLevel[depth].remove(thisKey);

                  //finding links to articles
                  TreeMap articles = new TreeMap();
                  String articleConstraint = "(artikel.embargo < '" +
                     (nowSec + quarterOfAnHour) +
                     "') AND (artikel.use_verloopdatum='0' OR artikel.verloopdatum > '" +
                     nowSec + "' )";
                  String articlePath = "pagina,contentrel,artikel";
                  NodeList nlArticles = cloud.getList(sThisObject,
                     articlePath, "artikel.number",
                     articleConstraint, "artikel.begindatum", "down", null, true);

                  for (int i = 0; i < nlArticles.size(); i++) {
                     String article_number = nlArticles.getNode(i).
                        getStringValue("artikel.number");
                     Long article_begindatum = new Long(nlArticles.getNode(i).
                        getLongValue("artikel.begindatum"));
                     while (articles.containsKey(article_begindatum)) {
                        article_begindatum = new Long(article_begindatum.
                           longValue() + 1);
                     }
                     articles.put(article_begindatum, article_number);
                  }

                  while (articles.size() > 0) {
                     Long thisArticle = (Long) articles.lastKey();
                     String article_number = (String) articles.get(
                        thisArticle);
                     if (bUrlIsAlive(url + ph.createItemUrl(article_number, sThisObject, "", ""))){
                        sUrl = "\t<url>\n\t\t<loc>" + url +
                        ph.createItemUrl(article_number, sThisObject, "","")
                        + "</loc>\n\t\t" + getLastmod(cloud, article_number) +
                        "\t\t" + getChangefreq(cloud,sThisObject,nowSec) +
                        "\t\t<priority>" + FormattedPriority(depth + 1) +
                        "</priority>\n\t</url>\n";
                        ts.add(sUrl);
                     }

                     //checking if article has dossier related
                     NodeList nlDossier = cloud.getList(article_number,
                        "artikel,posrel,dossier",
                        "dossier.number", null, null, null, null, true);
                     for (int j = 0; j < nlDossier.size(); j++) {
                        String params = "d=" +
                           nlDossier.getNode(j).
                           getStringValue("dossier.number");
                        if (bUrlIsAlive(url + ph.createItemUrl(article_number, sThisObject, params, ""))){
                           sUrl = "\t<url>\n\t\t<loc>" + url +
                           ph.createItemUrl(article_number, sThisObject, params, "")
                           + "</loc>\n\t\t" +
                           getLastmod(cloud, article_number) +
                           "\t\t" + getChangefreq(cloud,sThisObject,nowSec) +
                           "\t\t<priority>" + FormattedPriority(depth + 1) +
                           "</priority>\n\t</url>\n";
                           ts.add(sUrl);
                        }

                     }
                     articles.remove(thisArticle);
                  }

                  //finding links to natuurgebieden
                  String sNatuurgebiedenPath = "pagina,contentrel,provincies,pos4rel,natuurgebieden";
                  NodeList nlNatuurgebieden = cloud.getList(sThisObject,sNatuurgebiedenPath,
                  "natuurgebieden.number","natuurgebieden.bron!=''","natuurgebieden.bron","down",null,true);
                  for (int i = 0; i < nlNatuurgebieden.size(); i++){
                     if (bUrlIsAlive(url + ph.createItemUrl(nlNatuurgebieden.getNode(i).getStringValue("natuurgebieden.number"),sThisObject,null,""))){
                        sUrl = "\t<url>\n\t\t<loc>" + url +
                           ph.createItemUrl(nlNatuurgebieden.getNode(i).
                                            getStringValue(
                           "natuurgebieden.number"), sThisObject, null, "")
                           + "</loc>\n\t\t" +
                           getLastmod(cloud,
                                      nlNatuurgebieden.getNode(i).
                                      getStringValue("natuurgebieden.number")) +
                           "\t\t" + getChangefreq(cloud,sThisObject,nowSec) +
                           "\t\t<priority>" + FormattedPriority(depth + 1) +
                           "</priority>\n\t</url>\n";
                        ts.add(sUrl);
                     }
                  }
               }
            }
         }
      }

      String sContent = "";
      Iterator it = ts.iterator();
      while (it.hasNext()){
        sContent += (String)it.next();
      }
      return sContent;

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

   void createGZFile(String sFileName) {
      log.info("createGZFile " + sFileName);
      try {
         File f = new File(NatMMConfig.rootDir + "/" + sFileName);
         int bytesIn = 0;
         byte[] readBuffer = new byte[4096];
         ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(
            NatMMConfig.rootDir + "/" + sFileName + ".gz"));
         FileInputStream fis = new FileInputStream(f);
         ZipEntry anEntry = new ZipEntry(sFileName);
         zos.putNextEntry(anEntry);
         while ( (bytesIn = fis.read(readBuffer)) != -1) {
            zos.write(readBuffer, 0, bytesIn);
         }
         fis.close();
         zos.close();
         f.delete();
      } catch (Exception e){
         log.info(e.toString());
      }

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

  String getChangefreq(Cloud cloud,String sNumber, long nowSec){
     long lastmod = (cloud.getNode(sNumber).getLongValue("datumlaatstewijziging"));

     String Changefreq = "<changefreq>";
     if ((nowSec - lastmod) < 60*60*24) { Changefreq += "daily";}
     else if ((nowSec - lastmod) < 60*60*24*7) { Changefreq += "weekly";}
     else if ((nowSec - lastmod) < 60*60*24*30) { Changefreq += "monthly";}
     else { Changefreq += "yearly";}
     Changefreq += "</changefreq>\n";

     return Changefreq;

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
      ApplicationHelper ap = new ApplicationHelper();
      if(ap.isInstalled(cloud,"NatMM")) {
         generateSiteMap(cloud);
      }
   }

}
