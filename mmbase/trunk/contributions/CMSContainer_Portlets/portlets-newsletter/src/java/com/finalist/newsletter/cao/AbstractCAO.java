package com.finalist.newsletter.cao;

import net.sf.mmapps.modules.cloudprovider.CloudProvider;
import net.sf.mmapps.modules.cloudprovider.CloudProviderFactory;
import org.mmbase.bridge.Cloud;

public abstract class AbstractCAO {
   protected Cloud getCloud (){
      CloudProvider provider = CloudProviderFactory.getCloudProvider();
      return provider.getCloud();
   }
}
