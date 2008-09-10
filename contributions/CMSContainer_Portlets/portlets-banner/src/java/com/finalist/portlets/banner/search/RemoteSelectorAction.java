package com.finalist.portlets.banner.search;

import org.mmbase.bridge.Cloud;

import com.finalist.cmsc.navigation.select.forms.SelectorAction;

public class RemoteSelectorAction extends SelectorAction {

   @Override
   public Cloud getCloud() {
      return getCloudForAnonymousUpdate(true);
   }


   @Override
   public String getRequiredRankStr() {
      return null;
   }

}
