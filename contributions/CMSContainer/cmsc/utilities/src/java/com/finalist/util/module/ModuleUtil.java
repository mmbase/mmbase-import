package com.finalist.util.module;

import net.sf.mmapps.modules.cloudprovider.CloudProviderFactory;

import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.NodeManager;
import org.mmbase.module.Module;

public class ModuleUtil {
	public static boolean checkFeature(String featureName) {
		if (featureName != null) {
			
			// check for module
			// TODO: use the new hasModule method, which will not give warning is the log when the module is not available
			Module mod = Module.getModule(featureName);
			if (mod != null) {
				if (mod.hasStarted()) {
					return true;
				}
			}
			
			
			// check for node manager
			Cloud cloud = CloudProviderFactory.getCloudProvider().getCloud();
			if(cloud.hasNodeManager(featureName)) {
				return true;
			}
			
		}
		return false;
	}
}
