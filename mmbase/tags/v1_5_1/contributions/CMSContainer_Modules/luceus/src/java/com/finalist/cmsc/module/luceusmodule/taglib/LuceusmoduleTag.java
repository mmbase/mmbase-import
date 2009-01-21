/*

 This software is OSI Certified Open Source Software.
 OSI Certified is a certification mark of the Open Source Initiative.

 The license (Mozilla version 1.0) can be read at the MMBase site.
 See http://www.MMBase.org/license

 */
package com.finalist.cmsc.module.luceusmodule.taglib;

import javax.servlet.jsp.tagext.SimpleTagSupport;

import net.sf.mmapps.modules.cloudprovider.CloudProviderFactory;

import org.mmbase.bridge.Cloud;
import org.mmbase.module.Module;

import com.finalist.cmsc.module.luceusmodule.LuceusModule;

/**
 * Base Luceusmodule tag
 * 
 * @author Wouter Heijke
 */
public class LuceusmoduleTag extends SimpleTagSupport {

   private LuceusModule mod;

   /**
    * JSP variable name.
    */
   public String var;


   protected LuceusModule getModule() {
      if (mod == null) {
         mod = (LuceusModule) Module.getModule("luceusmodule");
      }
      if (mod.hasStarted()) {
         return mod;
      }
      return null;
   }


   protected boolean isRunning() {
      if (getModule() != null) {
         return true;
      }
      return false;
   }


   protected Cloud getAnonymousCloud() {
      return CloudProviderFactory.getCloudProvider().getAnonymousCloud();
   }


   public void setVar(String var) {
      this.var = var;
   }
}
