/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package com.finalist.mmbase.util;

import net.sf.mmapps.modules.cloudprovider.CloudProvider;
import net.sf.mmapps.modules.cloudprovider.CloudProviderFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.ContextProvider;

/**
 * Implementation to make the LeoCMS cloud accessible via the CloudProvider
 * @author Henk Hangyi 
 */
public class LeoCMSCloudProvider implements CloudProvider {

    private static Log log = LogFactory.getLog(LeoCMSCloudProvider.class);
    
    public final static String CONFIGURATION_RESOURCE_NAME = CloudProviderFactory.CLOUD_PROVIDER_FACTORY_CONFIGURATION_RESOURCE_NAME;
    
    private Properties properties;

    public LeoCMSCloudProvider(){
        readConfiguration();
    }
    
    public Cloud getAnonymousCloud() {
        return ContextProvider.getDefaultCloudContext().getCloud("mmbase");
    }

    public Cloud getAdminCloud() {
        return CloudFactory.getCloud();
    }

    public Cloud getCloud() {
        return getAdminCloud();
    }
    
    private void readConfiguration(){
        String cloudProviderClassName = null;
        try {
            properties = new Properties();
            properties.load(getClass().getResourceAsStream(CONFIGURATION_RESOURCE_NAME));
        } catch (IOException e) {
            log.warn("No  "+CONFIGURATION_RESOURCE_NAME +" configuration found for configuring the CloudProvider");
        }
    }
}
