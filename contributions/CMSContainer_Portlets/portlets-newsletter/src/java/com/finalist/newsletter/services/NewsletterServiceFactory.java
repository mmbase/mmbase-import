package com.finalist.newsletter.services;

import net.sf.mmapps.modules.cloudprovider.CloudProvider;
import net.sf.mmapps.modules.cloudprovider.CloudProviderFactory;







import com.finalist.newsletter.cao.NewsLetterStatisticCAO;
import com.finalist.newsletter.cao.NewsletterPublicationCAO;
import com.finalist.newsletter.cao.NewsletterSubscriptionCAO;
import com.finalist.newsletter.cao.impl.NewsLetterStatisticCAOImpl;
import com.finalist.newsletter.cao.impl.NewsletterPublicationCAOImpl;
import com.finalist.newsletter.cao.impl.NewsletterSubscriptionCAOImpl;
import com.finalist.newsletter.publisher.NewsletterPublisher;
import com.finalist.newsletter.services.impl.NewsletterPublicationServiceImpl;
import com.finalist.newsletter.services.impl.NewsletterSubscriptionServicesImpl;

public class NewsletterServiceFactory {
	
	private static CloudProvider cloudProvider = CloudProviderFactory.getCloudProvider();
	
	public static NewsletterSubscriptionServices getNewsletterSubscriptionServices(){
		NewsletterSubscriptionCAO subscriptionCAO = new NewsletterSubscriptionCAOImpl(cloudProvider.getCloud());
		NewsletterSubscriptionServicesImpl newsletterSubscriptionServices = new NewsletterSubscriptionServicesImpl();
		newsletterSubscriptionServices.setCao(subscriptionCAO); 
		return newsletterSubscriptionServices;
		
	}
	
	 public static NewsletterPublicationService getNewsletterPublicationService(){
	      
	      NewsletterSubscriptionCAO subscriptionCAO = new NewsletterSubscriptionCAOImpl(cloudProvider.getCloud());
	      NewsLetterStatisticCAO    statisticCAO = new NewsLetterStatisticCAOImpl(cloudProvider.getCloud());	      
	      NewsletterPublicationCAO  publicationCAO = new NewsletterPublicationCAOImpl(cloudProvider.getCloud());
	      NewsletterPublisher       publisher = new NewsletterPublisher();
	      NewsletterPublicationServiceImpl newsletterPublicationService = new NewsletterPublicationServiceImpl();
	      newsletterPublicationService.setSubscriptionCAO(subscriptionCAO); 
	      newsletterPublicationService.setStatisticCAO(statisticCAO);
	      newsletterPublicationService.setPublicationCAO(publicationCAO);
	      newsletterPublicationService.setMailSender(publisher);
	      return newsletterPublicationService;
	      
	   }
}
