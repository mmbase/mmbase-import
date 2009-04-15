package com.finalist.newsletter.cao;

import org.mmbase.bridge.Cloud;
import net.sf.mmapps.modules.cloudprovider.CloudProviderFactory;
import net.sf.mmapps.modules.cloudprovider.CloudProvider;

public class CloudProviderBean {
   public Cloud getCloud() {
      CloudProvider cloudProvider = CloudProviderFactory.getCloudProvider();
      return cloudProvider.getCloud();
   }
}
