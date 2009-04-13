/*

 This software is OSI Certified Open Source Software.
 OSI Certified is a certification mark of the Open Source Initiative.

 The license (Mozilla version 1.0) can be read at the MMBase site.
 See http://www.MMBase.org/license

 */
package com.finalist.cmsc.directreaction;

import net.sf.mmapps.modules.cloudprovider.CloudProviderFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mmbase.bridge.Cloud;
import org.mmbase.module.Module;

/**
 * @author Marco Fang
 */
public class DirectreactionModule extends Module {
   static Log log = LogFactory.getLog(DirectreactionModule.class);

   private boolean doListeners = true;

   @Override
   public void init() {
      loadInitParameters("com/directreaction");

      String userDoListeners = getInitParameter("disable-listeners");
      if (userDoListeners != null) {
         if (userDoListeners.equalsIgnoreCase("true")) {
            doListeners = false;
         }
         else {
            doListeners = true;
         }
      }

      if (doListeners) {
         new ContentElementEventListener(this);
      }
   }

   public void deleteContentReaction(int reNumber) {
      Cloud cloud = CloudProviderFactory.getCloudProvider().getCloud();
      cloud.getNode(reNumber).delete(true);
   }

}
