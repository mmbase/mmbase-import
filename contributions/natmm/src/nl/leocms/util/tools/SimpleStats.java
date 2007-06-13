package nl.leocms.util.tools;

import java.util.*;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import org.mmbase.bridge.*;
import org.mmbase.module.core.*;
import org.mmbase.util.logging.*;
import nl.leocms.util.ApplicationHelper;
import com.finalist.mmbase.util.CloudFactory;

public class SimpleStats
   implements Runnable {

   private static final Logger log = Logging.getLoggerInstance(SimpleStats.class);

   public void run() {
      log.info("run()");
		Cloud cloud = CloudFactory.getCloud();
		ApplicationHelper ap = new ApplicationHelper(cloud);
		if(ap.isInstalled("NMIntra")) {
			MMBaseContext mc = new MMBaseContext();
			ServletContext application = mc.getServletContext();
			saveLast(application);
		}
      log.info("done");
   }

   public void saveLast(ServletContext application) {
      Hashtable pageCounter = (Hashtable) application.getAttribute("pageCounter");
      Integer visitorsCounter = (Integer) application.getAttribute("visitorsCounter");
      if (pageCounter != null && visitorsCounter != null) {
         Transaction transaction = CloudFactory.getCloud().createTransaction();
         Node this_event = transaction.getNodeManager("mmevents").createNode();
         this_event.setStringValue("name", visitorsCounter.toString());
         this_event.commit();
         Enumeration pages = pageCounter.keys();
         while (pages.hasMoreElements()) {
            String thisPage = (String) pages.nextElement();
            int thisPageCount = ( (Integer) pageCounter.get(thisPage)).intValue();
            // NMCMS-242 if node doesn't exist it will throw NodeNotFound exception
            try {
                Node this_page = transaction.getNode(thisPage);
                if(this_page!=null) {
                    RelationManager posrel = transaction.getRelationManager("posrel");
                    Relation posrelRelation = posrel.createRelation(this_event, this_page);
                    posrelRelation.setIntValue("pos", thisPageCount);
                    posrelRelation.commit();
                } else {
                    log.info("page " + this_page + " does not exist, probably it was deleted today");
                }
            } catch(Exception e) {
                log.info("page " + thisPage + " does not exist.");
            }
         }
         transaction.commit();
      }
  	   resetVariables(application);
   }

   public void pageCounter(Cloud cloud, ServletContext application,
                           String paginaID, HttpServletRequest request) {
                              
      Hashtable pageCounter = (Hashtable) application.getAttribute("pageCounter");
      HashSet visitorsSessions = (HashSet) application.getAttribute("visitorsSessions");
      Integer visitorsCounter = (Integer) application.getAttribute("visitorsCounter");

      if (pageCounter == null || visitorsCounter == null || visitorsSessions == null) {
         pageCounter = new Hashtable();
         visitorsSessions = new HashSet();
         visitorsCounter = new Integer(0);
      }

      String thisSession = request.getSession().toString();
      if (!visitorsSessions.contains(thisSession)) {
         visitorsSessions.add(thisSession);
         visitorsCounter = new Integer(visitorsCounter.intValue() + 1);
      }

      if (pageCounter != null) {
         Integer pageCount = (Integer) pageCounter.get(paginaID);
         if (pageCount == null) {
            pageCount = new Integer(0);
         }
         pageCounter.put(paginaID, new Integer(pageCount.intValue() + 1));
         thisSession = request.getSession().toString();
         if ( (visitorsSessions != null) &&
             !visitorsSessions.contains(thisSession)) {
            visitorsSessions.add(thisSession);
            if (visitorsCounter != null) {
               visitorsCounter = new Integer(visitorsCounter.intValue() + 1);
            }
         }
      }
      else {
         /* add one to the pageCounter for paginaID, check if page exists */
         Integer pageCount = new Integer(0);
         pageCounter.put(paginaID, new Integer(pageCount.intValue() + 1));
      }
      application.setAttribute("pageCounter", pageCounter);
      application.setAttribute("visitorsSessions", visitorsSessions);
      application.setAttribute("visitorsCounter", visitorsCounter);
   }

   public int[] calculate(Cloud cloud, String timeConstraint, int selection,
                          ServletContext application) {
      int[] result = new int[2];
      int maxPageCount = 1;
      int totalPages = 0;
      Hashtable pageCounts = new Hashtable();

      /* the following count does not take care of double and not attached pages,
             this will lead to minor deviations when using the selection */

      NodeList nlPages = cloud.getNodeManager("pagina").getList(null, "number", "DOWN");
      for (int i = 0; i < nlPages.size(); i++) {
         int pageCount = 0;
         String page_number = nlPages.getNode(i).getStringValue("number");
         NodeList nlPageEvents = cloud.getList(page_number,
                                               "pagina,posrel,mmevents",
                                               "posrel.pos",
                                               timeConstraint, null, null, null, false);
         for (int j = 0; j < nlPageEvents.size(); j++) {
            pageCount += nlPageEvents.getNode(j).getIntValue("posrel.pos");
         }
         if (pageCount > maxPageCount) {
            maxPageCount = pageCount;
         }
         Integer numberOfPages = (Integer) pageCounts.get(new Integer(pageCount));
         if (numberOfPages == null) {
            numberOfPages = new Integer(0);
         }
         pageCounts.put(new Integer(pageCount),
                        new Integer(numberOfPages.intValue() + 1));
         totalPages++;

      }

      /* throw away pages untill selection is satisfied */

      int pageCountTD = 0;
      int surplus = totalPages - selection;
      if (selection == -1) {
         surplus = 0;
      }
      while (surplus > 0) {
         Integer numberOfPages = (Integer) pageCounts.get(new Integer(
            pageCountTD));
         if (numberOfPages == null) {
            pageCountTD++;
         }
         else {
            int nOP = numberOfPages.intValue();
            if (surplus >= nOP) {
               pageCounts.remove(new Integer(pageCountTD));
               totalPages = totalPages - nOP;
               pageCountTD++;
            }
            else {
               pageCounts.put(new Integer(pageCountTD),
                              new Integer(nOP - surplus));
               totalPages = selection;
            }
            surplus = totalPages - selection;
         }
      }

      int visitorsCount = 0;
      NodeList nlEvents = cloud.getNodeManager("mmevents").getList(
         timeConstraint, null, null);
      for (int i = 0; i < nlEvents.size(); i++) {
         /* connected to a page ? */
         boolean isStatEvent = false;
         String mmevents_number = nlEvents.getNode(i).getStringValue("number");
         NodeList nlEventsPages = cloud.getList(mmevents_number,
                                                "mmevents,posrel,pagina",
                                                "posrel.pos",
                                                null, null, null, null, false);
         if (nlEventsPages.size() > 0) {
            isStatEvent = true;
         }
         if (isStatEvent) {
            String visitors_count = nlEvents.getNode(i).getStringValue(
               "name");
            visitorsCount += Integer.valueOf(visitors_count).intValue();

         }
      }
      result[0] = maxPageCount;
      result[1] = visitorsCount;
      application.setAttribute("pageCounts", pageCounts);
      return result;
   }

   public void resetVariables(ServletContext application) {
      application.setAttribute("pageCounter", null);
      application.setAttribute("visitorsCounter", null);
   }

   public boolean checkSelection(ServletContext application, int pageCount) {
      boolean showPage = false;
      Hashtable pageCounts = (Hashtable) application.getAttribute("pageCounts");
      if (pageCounts != null) {
         Integer numberOfPages = (Integer) pageCounts.get(new Integer(pageCount));
         if (numberOfPages != null) {
            showPage = true;
            if (numberOfPages.intValue() == 1) {
               pageCounts.remove(new Integer(pageCount));
            }
            else {
               pageCounts.put(new Integer(pageCount),
                              new Integer(numberOfPages.intValue() - 1));
            }
         }
      }
      return showPage;
   }
}
