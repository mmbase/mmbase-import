package com.finalist.portlets.banner.search;

import net.sf.mmapps.modules.cloudprovider.CloudProviderFactory;

import org.mmbase.bridge.Cloud;

import com.finalist.cmsc.navigation.select.forms.SelectorAction;
import com.finalist.cmsc.services.publish.Publish;

public class RemoteSelectorAction extends SelectorAction {

   @Override
   public Cloud getCloud() {
      return getCloudForAnonymousUpdate(true);
   }

   public Cloud getCloudForAnonymousUpdate(boolean isRemote) {
      Cloud cloud = CloudProviderFactory.getCloudProvider().getCloud();
      if (isRemote) {
         return Publish.getRemoteCloud(cloud);
      }
      return cloud;
   }


   @Override
   public String getRequiredRankStr() {
      return null;
   }

}
