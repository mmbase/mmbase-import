/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package com.finalist.pluto.portalImpl.factory;

import javax.servlet.http.HttpServletRequest;

import org.apache.pluto.factory.Factory;
import org.apache.pluto.services.information.DynamicInformationProvider;
import org.apache.pluto.services.information.StaticInformationProvider;

/**
 * @version $Revision: 1.2 $
 */
public interface InformationProviderFactory extends Factory {

   StaticInformationProvider getStaticProvider();


   DynamicInformationProvider getDynamicProvider(HttpServletRequest request);

}
