package com.finalist.newsletter.util;

import java.util.ArrayList;
import java.util.List;

import com.finalist.newsletter.generator.NewsletterGeneratorFactory;
import com.finalist.newsletter.module.bean.GlobalOverviewBean;
import com.finalist.newsletter.module.bean.NewsletterOverviewBean;
import com.finalist.newsletter.module.bean.NewsletterSubscriberBean;
import com.finalist.newsletter.module.bean.SubscriptionDetailBean;
import com.finalist.newsletter.module.bean.SubscriptionOverviewBean;

public final class BeanUtil {

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

   public static List<NewsletterSubscriberBean> getSubscriberBeans(int newsletterNumber) {
      List<NewsletterSubscriberBean> subscribers = new ArrayList<NewsletterSubscriberBean>();
      List<String> subscribersList = NewsletterSubscriptionUtil.getSubscribersForNewsletter(newsletterNumber);
      if (subscribersList != null) {
         for (int s = 0; s < subscribersList.size(); s++) {
            NewsletterSubscriberBean subscriberBean = new NewsletterSubscriberBean();
            String userName = subscribersList.get(s);
            subscriberBean.setUserName(userName);

            int numberOfThemes = NewsletterSubscriptionUtil.getNumberOfSubscribedThemes(userName, newsletterNumber);
            subscriberBean.setNumberOfThemes(numberOfThemes);

            subscribers.add(subscriberBean);
         }
      }
      return (subscribers);
   }

   public static NewsletterOverviewBean createNewsletterOverviewBean(int newsletterNumber) {
      NewsletterOverviewBean bean = new NewsletterOverviewBean();
      bean.setNumber(newsletterNumber);

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

   public static SubscriptionDetailBean createSubscriptionDetailBean(String userName) {
      SubscriptionDetailBean bean = new SubscriptionDetailBean();
      bean.setUserName(userName);

      List<String> availableMimeTypes = NewsletterGeneratorFactory.getMimeTypes();
      bean.setAvailableMimeTypes(availableMimeTypes);
      List<Integer> availableStatusOptions = NewsletterSubscriptionUtil.getStatusOptions();
      bean.setAvailableStatusOptions(availableStatusOptions);

      List<Integer> subscribedThemes = NewsletterSubscriptionUtil.getUserSubscribedThemes(userName);
      List<Integer> subscribedNewsletters = NewsletterSubscriptionUtil.getUserSubscribedNewsletters(userName);

      if (subscribedNewsletters != null && subscribedNewsletters.size() > 0) {
         bean.setSubscribedNewsletters(subscribedNewsletters);
      }

      if (subscribedThemes != null && subscribedThemes.size() > 0) {
         bean.setSubscribedThemes(subscribedThemes);
      }

      if ((subscribedThemes != null && subscribedThemes.size() > 0) || (subscribedNewsletters != null && subscribedNewsletters.size() > 0)) {
         String status = NewsletterSubscriptionUtil.getSubscriptionStatus(userName);
         bean.setStatus(status);

         String mimeType = NewsletterSubscriptionUtil.getPreferredMimeType(userName);
         bean.setMimeType(mimeType);

         String emailAddress = ""; // TODO: Get email address from session
         bean.setEmailAddress(emailAddress);

         return (bean);
      }
      return (null);

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
}