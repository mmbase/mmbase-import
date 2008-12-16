package com.finalist.util.module;

import net.sf.mmapps.modules.cloudprovider.CloudProviderFactory;

import org.mmbase.bridge.Cloud;
import org.mmbase.module.Module;

public class ModuleUtil {
   /**
    * A feature can be both a module or a node manager
    * 
    * @param featureName
    * @return
    */
   public static boolean checkFeature(String featureName) {
      if (featureName != null) {

         // check for module
         if (Module.hasModule(featureName)) {
            return true;
         }

         // check for node manager
         Cloud cloud = CloudProviderFactory.getCloudProvider().getCloud();
         if (cloud.hasNodeManager(featureName)) {
            return true;
         }

      }
      return false;
   }
}
