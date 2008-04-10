package com.finalist.newsletter.services;

import net.sf.mmapps.modules.cloudprovider.CloudProvider;
import net.sf.mmapps.modules.cloudprovider.CloudProviderFactory;







import com.finalist.newsletter.cao.NewsletterSubscriptionCAO;
import com.finalist.newsletter.cao.impl.NewsletterSubscriptionCAOImpl;
import com.finalist.newsletter.services.impl.NewsletterSubscriptionServicesImpl;

public class NewsletterServiceFactory {
	
	private static CloudProvider cloudProvider = CloudProviderFactory.getCloudProvider();
	
	public static NewsletterSubscriptionServices getNewsletterSubscriptionServices(){
		
		NewsletterSubscriptionCAO subscriptionCAO = new NewsletterSubscriptionCAOImpl(cloudProvider.getCloud());
		NewsletterSubscriptionServicesImpl newsletterSubscriptionServices = new NewsletterSubscriptionServicesImpl();
		newsletterSubscriptionServices.setCao(subscriptionCAO); 
		return newsletterSubscriptionServices;
		
	}
}
