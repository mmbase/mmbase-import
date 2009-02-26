package com.finalist.newsletter.forms;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.springframework.web.struts.DispatchActionSupport;

import com.finalist.cmsc.paging.PagingUtils;
import com.finalist.newsletter.domain.Publication;
import com.finalist.newsletter.services.NewsletterPublicationService;

/**
 * using for newsletter statistic searching
 *
 * @author Lisa
 */

public class NewsletterStatisticSearchAction extends DispatchActionSupport {

   private static Log log = LogFactory.getLog(NewsletterPublicationManagementAction.class);

   private NewsletterPublicationService publicationService;

   /**
    * Initialize publicationService
    */
   protected void onInit() {

      super.onInit();
      publicationService = (NewsletterPublicationService) getWebApplicationContext().getBean("publicationService");

   }

   /**
    * unspecified searching newsletter publication with sorting, ordering, paging
    *
    * @param mapping
    * @param form
    * @param request
    * @param response
    * @return ActionForward refresh Newsletter statistic list
    */
   protected ActionForward unspecified(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                       HttpServletResponse response) throws Exception {

      log.debug("No parameter specified,go to edit page ,show related publications");

      PagingUtils.initStatusHolder(request);
      int newsletterId = Integer.parseInt(request.getParameter("newsletterId"));
      Date endDate = new Date();
      int resultCount = publicationService.searchPublication(newsletterId, "", "", null, endDate, false).size();
      List<Publication> publications;
      publications = publicationService.searchPublication(newsletterId, "", "", null, endDate, true);
      List<Map<String, String>> results = convertPublicationsToMap(publications);
      if (results.size() > 0) {
         request.setAttribute("results", results);
      }
      request.setAttribute("resultCount", resultCount);
      request.setAttribute("newsletterId", newsletterId);
      return mapping.findForward("success");

   }

   /**
    * @param mapping  Description of Parameter
    * @param form     Description of Parameter
    * @param request  Description of Parameter
    * @param response Description of Parameter
    * @return ActionForward Showing newsletter statistic list
    */
   public ActionForward searchPublicationStatistic(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                                   HttpServletResponse response) {
      log.debug("parameter specified, search newsletterpublication ");
      PagingUtils.initStatusHolder(request);

      int newsletterId = Integer.parseInt(request.getParameter("newsletterId"));
      NewsletterPublicationManageForm myForm = (NewsletterPublicationManageForm) form;
      String tmpTitle = myForm.getTitle();
      String tmpSubject = myForm.getSubject();
      String tmpPeriod = myForm.getPeriod();

      Calendar calendar = Calendar.getInstance();
      Date endTime = calendar.getTime();
      Date startTime = null;
      switch (Integer.parseInt(tmpPeriod)) {
         case 1:
            calendar.add(Calendar.DAY_OF_YEAR, -1);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            startTime = calendar.getTime();
            break;
         case 7:
            calendar.add(Calendar.DAY_OF_YEAR, -7);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            startTime = calendar.getTime();
            break;
         case 14:
            calendar.add(Calendar.DAY_OF_YEAR, -14);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            startTime = calendar.getTime();
            break;
         case 30:
            calendar.add(Calendar.MONTH, -1);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            startTime = calendar.getTime();
            break;
         case 365:
            calendar.set(Calendar.YEAR, -1);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            startTime = calendar.getTime();
            break;
         default:
            break;
      }
      List<Publication> publications;
      int resultCount = publicationService.searchPublication(newsletterId, tmpTitle, tmpSubject, startTime, endTime,
               false).size();
      publications = publicationService.searchPublication(newsletterId, tmpTitle, tmpSubject, startTime, endTime, true);
      List<Map<String, String>> results = convertPublicationsToMap(publications);
      if (results.size() > 0) {
         request.setAttribute("results", results);
      }
      request.setAttribute("resultCount", resultCount);
      request.setAttribute("newsletterId", newsletterId);
      return mapping.findForward("success");
   }

   /**
    * convert newsletter publication to map
    *
    * @param publications publication list used for containing the result of the searching.
    * @return List containing some information of newsletter publication and send time, subscriptions number, send
    *         successfully number, and bounced number
    */
   private List convertPublicationsToMap(List<Publication> publications) {

      List<Map> results = new ArrayList<Map>();
      for (Publication publication1 : publications) {
         Map result = new HashMap();
         Publication publication = publication1;
         result.put("id", publication.getId());
         result.put("title", publication.getTitle());
         result.put("subject", publication.getSubject());
         result.put("sendtime", publication.getPublishdate());
         result.put("subscriptions", publication.getSubscriptions());
         result.put("sendsuccessful", publication.getSubscriptions() - publication.getBounced());
         result.put("bounced", publication.getBounced());
         results.add(result);
      }
      return results;
   }

}