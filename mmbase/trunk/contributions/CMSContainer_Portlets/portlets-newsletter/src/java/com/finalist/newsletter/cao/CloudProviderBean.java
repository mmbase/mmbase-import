package com.finalist.newsletter.cao;

import net.sf.mmapps.modules.cloudprovider.CloudProvider;
import net.sf.mmapps.modules.cloudprovider.CloudProviderFactory;

import org.mmbase.bridge.Cloud;

public class CloudProviderBean {
   
   public Cloud getCloud() {
      CloudProvider cloudProvider = CloudProviderFactory.getCloudProvider();
         return cloudProvider.getCloud();
   }
}
