package com.finalist.portlets.banner.search;

import net.sf.mmapps.modules.cloudprovider.CloudProvider;
import net.sf.mmapps.modules.cloudprovider.CloudProviderFactory;

import org.mmbase.bridge.Cloud;

import com.finalist.cmsc.navigation.select.forms.SelectorAction;
import com.finalist.cmsc.services.publish.Publish;

public class RemoteSelectorAction extends SelectorAction {

   @Override
   public Cloud getCloud() {
      CloudProvider cloudProvider = CloudProviderFactory.getCloudProvider();
      Cloud cloud = cloudProvider.getCloud();
      return Publish.getRemoteCloud(cloud);
   }


   @Override
   public String getRequiredRankStr() {
      return null;
   }

}
