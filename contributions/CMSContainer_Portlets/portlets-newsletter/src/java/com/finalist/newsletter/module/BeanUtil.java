package com.finalist.newsletter.module;

import java.util.ArrayList;
import java.util.List;

import com.finalist.newsletter.module.bean.GlobalOverviewBean;
import com.finalist.newsletter.module.bean.NewsletterDetailBean;
import com.finalist.newsletter.module.bean.NewsletterOverviewBean;
import com.finalist.newsletter.module.bean.NewsletterSubscriberBean;
import com.finalist.newsletter.module.bean.SubscriptionDetailBean;
import com.finalist.newsletter.module.bean.SubscriptionOverviewBean;
import com.finalist.newsletter.util.NewsletterSubscriptionUtil;
import com.finalist.newsletter.util.NewsletterUtil;

public final class BeanUtil {

   public static NewsletterOverviewBean createNewsletterOverviewBean(String newsletterNumber) {
      NewsletterOverviewBean bean = new NewsletterOverviewBean();
      bean.setNumber(Integer.parseInt(newsletterNumber));

      String title = NewsletterUtil.getTitle(newsletterNumber);
      bean.setTitle(title);

      int numberOfThemes = NewsletterUtil.countThemes(newsletterNumber);
      bean.setNumberOfThemes(numberOfThemes);

      int numberOfPublications = NewsletterUtil.countPublications(newsletterNumber);
      bean.setNumberOfPublications(numberOfPublications);

      int numberOfSubscribers = NewsletterSubscriptionUtil.countSubscriptions(newsletterNumber);
      bean.setNumberOfSubscriptions(numberOfSubscribers);

      return (bean);
   }

   public static SubscriptionOverviewBean createSubscriptionOverviewBean(String userName) {
      SubscriptionOverviewBean bean = new SubscriptionOverviewBean();
      bean.setUserName(userName);

      String status = NewsletterSubscriptionUtil.getSubscriptionStatus(userName);
      bean.setStatus(status);

      String mimeType = NewsletterSubscriptionUtil.getPreferredMimeType(userName);
      bean.setMimeType(mimeType);

      int numberOfNewsletters = NewsletterSubscriptionUtil.getNumberOfSubscribedNewsletters(userName);
      bean.setNumberOfNewsletters(numberOfNewsletters);

      int numberOfThemes = 0;
      bean.setNumberOfThemes(numberOfThemes);

      return (bean);
   }

   public static GlobalOverviewBean createGlobalOverviewBean() {
      GlobalOverviewBean bean = new GlobalOverviewBean();

      int numberOfNewsletters = NewsletterUtil.countNewsletters();
      bean.setNumberOfNewsletters(numberOfNewsletters);

      int numberOfThemes = NewsletterUtil.countThemes();
      bean.setNumberOfThemes(numberOfThemes);

      int numberOfPublications = NewsletterUtil.countPublications(); 
      bean.setNumberOfPublications(numberOfPublications);
      
      int numberOfSubscribtions = NewsletterSubscriptionUtil.countSubscriptions();
      bean.setNumberOfSentEmails(numberOfSubscribtions);
      
      return (bean);
   }

   public static NewsletterDetailBean createNewsletterDetailBean(String newsletterNumber) {
      NewsletterDetailBean bean = new NewsletterDetailBean();
      bean.setNumber(Integer.parseInt(newsletterNumber));

      String title = NewsletterUtil.getTitle(String.valueOf(newsletterNumber));
      bean.setTitle(title);

      List<NewsletterSubscriberBean> subscribers = new ArrayList<NewsletterSubscriberBean>();
      List<String> subscribersList = NewsletterSubscriptionUtil.getSubscribersForNewsletter(newsletterNumber);
      if (subscribersList != null) {
         for (int s = 0; s < subscribersList.size(); s++) {
            NewsletterSubscriberBean subscriberBean = new NewsletterSubscriberBean();
            String userName = subscribersList.get(s);
            subscriberBean.setUserName(userName);

            int numberOfThemes = NewsletterSubscriptionUtil.getNumberOfSubscribedThemes(userName, newsletterNumber);
            bean.setNumber(numberOfThemes);

            subscribers.add(subscriberBean);
         }
      }
      bean.setSubscribers(subscribers);

      return (bean);
   }

   public static SubscriptionDetailBean createSubscriptionDetailBean(String userName) {
      SubscriptionDetailBean bean = new SubscriptionDetailBean();
      bean.setUserName(userName);

      String status = NewsletterSubscriptionUtil.getSubscriptionStatus(userName);
      bean.setStatus(status);

      String mimeType = NewsletterSubscriptionUtil.getPreferredMimeType(userName);
      bean.setMimeType(mimeType);

      String emailAddress = ""; // TODO: Get email address from session
      bean.setEmailAddress(emailAddress);

      return (bean);
   }
}