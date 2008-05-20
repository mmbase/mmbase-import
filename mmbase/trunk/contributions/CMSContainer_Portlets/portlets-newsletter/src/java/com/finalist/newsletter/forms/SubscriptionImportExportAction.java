package com.finalist.newsletter.forms;

import com.finalist.cmsc.services.community.person.Person;
import com.finalist.newsletter.domain.Subscription;
import com.finalist.newsletter.domain.Term;
import com.finalist.newsletter.domain.Newsletter;
import com.finalist.newsletter.services.NewsletterSubscriptionServices;
import com.finalist.newsletter.services.NewsletterService;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.*;
import org.apache.struts.upload.FormFile;
import org.springframework.web.struts.DispatchActionSupport;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class SubscriptionImportExportAction extends DispatchActionSupport {
   private static Log log = LogFactory.getLog(SubscriptionImportExportAction.class);

   NewsletterSubscriptionServices subscriptionServices;
   NewsletterService newsletterService;

   protected void onInit() {
      super.onInit();
      subscriptionServices = (NewsletterSubscriptionServices) getWebApplicationContext().getBean("subscriptionServices");
      newsletterService = (NewsletterService) getWebApplicationContext().getBean("newsletterServices");
   }

   public ActionForward export(ActionMapping mapping, ActionForm form,
                               HttpServletRequest request, HttpServletResponse response)
         throws IOException {

      log.debug("Export Susbscriptions");

      List<Subscription> subscriptions = new ArrayList<Subscription>();
      String type = request.getParameter("type");
      if ("subscription".equals(type)) {
         String[] subscriptionIds = request.getParameterValues("subscriptionId");
         for (String subscriptionId : subscriptionIds) {
            subscriptions.add(subscriptionServices.getSubscription(subscriptionId));
         }
      }
      else if ("newsletter".equals(type)) {
         String[] newsletterIds = request.getParameterValues("newsletterIds");
         for (String newsletterid : newsletterIds) {
            List<Subscription> s = subscriptionServices.getSubscriptionsByNewsletterId(newsletterid);
            subscriptions.addAll(s);
         }
      }
      else if ("person".equals(type)) {
         String[] newsletterIds = request.getParameterValues("userId");
         for (String newsletterid : newsletterIds) {
            List<Subscription> s = subscriptionServices.getSubscriptionBySubscriber(newsletterid);
            subscriptions.addAll(s);
         }
      }
      else {
         subscriptions = subscriptionServices.getAllSubscription();
      }


      String xml = getXStream().toXML(subscriptions);
      byte[] bytes = xml.getBytes();

      response.setContentType("text/xml");
      response.setContentLength(bytes.length);
      response.setHeader("Content-Disposition", "attachment; filename=subscriptions.xml");
      response.setHeader("Cache-Control", "no-store");

      response.flushBuffer();
      OutputStream outStream = response.getOutputStream();

      outStream.write(bytes);
      outStream.flush();

      return mapping.findForward(null);
   }


   public ActionForward importsubscription(ActionMapping mapping, ActionForm form,
                                           HttpServletRequest request, HttpServletResponse response)
         throws IOException {
      SubscriptionImportUploadForm myForm = (SubscriptionImportUploadForm) form;

      FormFile myFile = myForm.getDatafile();
      byte[] fileData = myFile.getFileData();
      String contentType = myFile.getContentType();
      String fileName = myFile.getFileName();

      boolean isXML = "text/xml".equals(contentType);
      boolean isPlain = "text/plain".equals(contentType);
      boolean isCSV = ".csv".endsWith(fileName.toLowerCase());

      ActionMessages messages = new ActionMessages();
      
      if(!isXML&&!(isPlain&isCSV)){
         messages.add("file", new ActionMessage("datafile.unsupport"));
      }else{
         try {
            importFromFile(fileData);
         } catch (Exception e) {
            log.error(e);
            messages.add("file", new ActionMessage("datafile.invalid"));
         }
      }

      if(messages.size() <1){
         return mapping.findForward("success");
      }else{
         saveMessages(request,messages);
         return mapping.findForward("failed");
      }

   }


   private void importFromFile(byte[] fileData) throws Exception {
      String xml = new String(fileData);
      List<Subscription> subscriptionList;
      try {
         subscriptionList = (List<Subscription>) getXStream().fromXML(xml);
      } catch (Exception e) {
         throw new Exception(e);
      }

      for (Subscription subscription : subscriptionList) {
         Person subscrier = subscription.getSubscriber();
         int sbId = subscrier.getId().intValue();
         int nId = subscription.getNewsletter().getId();

         if (null == subscriptionServices.getSubscription(sbId, nId)) {


            log.debug(String.format("try to import user %s's subscription of %s which is not exist", sbId, nId));

            subscriptionServices.addNewRecord(sbId, nId);
         }
      }
   }


   private String convertToXML(List<Subscription> subscriptions) {
      String xml;
      XStream xstream = new XStream(new DomDriver());
      xstream.alias("person", Person.class);
      xstream.alias("subscription", Subscription.class);
      xstream.alias("term", Term.class);

      xml = xstream.toXML(subscriptions);
      return xml;
   }

   private XStream getXStream() {
      XStream xstream = new XStream(new DomDriver());
      xstream.alias("person", Person.class);
      xstream.alias("subscription", Subscription.class);
      xstream.alias("term", Term.class);
      return xstream;
   }
}
