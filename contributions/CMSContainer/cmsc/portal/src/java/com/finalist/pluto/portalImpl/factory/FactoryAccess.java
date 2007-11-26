/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package com.finalist.pluto.portalImpl.factory;

import org.apache.pluto.services.information.DynamicInformationProvider;
import org.apache.pluto.services.information.InformationProviderService;
import org.apache.pluto.services.information.StaticInformationProvider;

import com.finalist.pluto.portalImpl.services.factorymanager.FactoryManager;

public class FactoryAccess {

   public static StaticInformationProvider getStaticProvider() {
      return getProviderFactory().getStaticProvider();
   }


   public static DynamicInformationProvider getDynamicProvider(javax.servlet.http.HttpServletRequest request) {
      return getProviderFactory().getDynamicProvider(request);
   }


   public static InformationProviderService getInformationProviderContainerService() {
      return getProviderService();
   }


   private static InformationProviderFactory getProviderFactory() {
      return (InformationProviderFactory) FactoryManager.getFactory(InformationProviderFactory.class);
   }


   private static InformationProviderService getProviderService() {
      return (InformationProviderService) FactoryManager.getFactory(InformationProviderFactory.class);
   }

}
