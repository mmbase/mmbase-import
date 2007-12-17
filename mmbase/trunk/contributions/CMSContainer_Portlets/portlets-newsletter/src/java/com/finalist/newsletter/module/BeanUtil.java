package com.finalist.newsletter.module;

import net.sf.mmapps.modules.cloudprovider.CloudProviderFactory;

import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeList;
import org.mmbase.bridge.NodeManager;

import com.finalist.newsletter.module.bean.GlobalOverviewBean;
import com.finalist.newsletter.module.bean.NewsletterDetailBean;
import com.finalist.newsletter.module.bean.NewsletterOverviewBean;
import com.finalist.newsletter.module.bean.SubscriptionOverviewBean;
import com.finalist.newsletter.util.NewsletterSubscriptionUtil;
import com.finalist.newsletter.util.NewsletterUtil;

public final class BeanUtil {

   public static NewsletterOverviewBean createNewsletterOverviewBean(String newsletterNumber) {
      NewsletterOverviewBean bean = new NewsletterOverviewBean();

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

   public static GlobalOverviewBean createGlobalOverviewBean() {
      Cloud cloud = CloudProviderFactory.getCloudProvider().getCloud();
      GlobalOverviewBean bean = new GlobalOverviewBean();

      NodeManager newsletterNodeManager = cloud.getNodeManager("newsletter");
      int numberOfNewsletters = 5;
      bean.setNumberOfNewsletters(numberOfNewsletters);

      NodeManager themeNodeManager = cloud.getNodeManager("newslettertheme");
      int numberOfThemes = 0;
      bean.setNumberOfThemes(numberOfThemes);

      NodeManager publicationNodeManager = cloud.getNodeManager("newsletterpublication");
      int numberOfPublications = 0;
      bean.setNumberOfPublications(numberOfPublications);
      return (bean);
   }

   public static SubscriptionOverviewBean getUserStatisticsBean(String userName) {
      SubscriptionOverviewBean bean = new SubscriptionOverviewBean();
      bean.setUserName(userName);

      String mimeType = NewsletterSubscriptionUtil.getPreferredMimeType(userName);
      bean.setPreferredMimeType(mimeType);

      String status = NewsletterSubscriptionUtil.getSubscriptionStatus(userName);
      bean.setSubscriptionStatus(status);

      int numberOfSubscriptions = NewsletterSubscriptionUtil.getNumberOfSubscribedNewsletters(userName);
      bean.setNumberOfSubscriptions(numberOfSubscriptions);

      return (bean);
   }

   public static NewsletterDetailBean createNewsletterDetailBean(String newsletterNumber) {
      NewsletterDetailBean bean = new NewsletterDetailBean();
      return (bean);
   }

}
