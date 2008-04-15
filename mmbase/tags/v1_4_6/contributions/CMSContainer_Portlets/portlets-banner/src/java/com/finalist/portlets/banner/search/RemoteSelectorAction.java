package com.finalist.portlets.banner.search;

import net.sf.mmapps.modules.cloudprovider.CloudProvider;
import net.sf.mmapps.modules.cloudprovider.CloudProviderFactory;

import org.mmbase.bridge.Cloud;
import org.mmbase.remotepublishing.CloudManager;

import com.finalist.cmsc.navigation.select.forms.SelectorAction;

public class RemoteSelectorAction extends SelectorAction {

   @Override
   public Cloud getCloud() {
      CloudProvider cloudProvider = CloudProviderFactory.getCloudProvider();
      Cloud cloud = cloudProvider.getCloud();
      return CloudManager.getCloud(cloud, "live.server");
   }


   @Override
   public String getRequiredRankStr() {
      return null;
   }

}
