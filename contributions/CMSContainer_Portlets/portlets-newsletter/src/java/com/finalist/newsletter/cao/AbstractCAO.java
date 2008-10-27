package com.finalist.newsletter.cao;

import org.mmbase.bridge.Cloud;

import com.finalist.cmsc.navigation.ServerUtil;
import com.finalist.cmsc.services.publish.Publish;

public abstract class AbstractCAO {
   protected Cloud cloud;

   protected boolean isLocal;
   public void setCloud(Cloud cloud) {
      this.cloud = cloud;
   }

   protected Cloud getCloud() {
      if(isLocal || ServerUtil.isSingle()) {
         return cloud;
      }
      else {
         return Publish.getRemoteCloud(cloud);
      }
   }
   public void setLocal() {
      isLocal = true;
   }
}
