package com.finalist.newsletter.forms;

import com.finalist.cmsc.services.community.person.Person;
import com.finalist.newsletter.domain.Subscription;
import com.finalist.newsletter.domain.Term;
import com.finalist.newsletter.services.NewsletterSubscriptionServices;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
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

   protected void onInit() {
      super.onInit();
      subscriptionServices = (NewsletterSubscriptionServices) getWebApplicationContext().getBean("newsletterSubscriptionServices");
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
            List<Subscription> s = subscriptionServices.getSubscriptionsByNewsletterId(Integer.parseInt(newsletterid));
            subscriptions.addAll(s);
         }
      }
      else {
         subscriptions = subscriptionServices.getAllSubscription();
      }


      String xml = convertToXML(subscriptions);
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

   private String convertToXML(List<Subscription> subscriptions) {
      String xml;
      XStream xstream = new XStream(new DomDriver());
      xstream.alias("person", Person.class);
      xstream.alias("subscription", Subscription.class);
      xstream.alias("term", Term.class);

      xml = xstream.toXML(subscriptions);
      return xml;
   }
}
