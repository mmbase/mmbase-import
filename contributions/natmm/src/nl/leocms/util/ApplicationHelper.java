/*
 * Version: MPL 1.1/GPL 2.0/LGPL 2.1
 *
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * See license.txt in the root of the LeoCMS directory for the full license.
 */
package nl.leocms.util;

import java.util.*;
import nl.leocms.applications.*;
import org.mmbase.bridge.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

public class ApplicationHelper {
   
   /** Logger instance. */
   private static Logger log = Logging.getLoggerInstance(ApplicationHelper.class.getName());
   
   public ApplicationHelper() {
   }
	
	public boolean isInstalled(Cloud cloud, String sApplication) {
		NodeManager versionManager = cloud.getNodeManager("versions");
		return (versionManager.getList("type='application' AND name='" + sApplication + "'", null, null).size()>0);
	}
	
	public HashMap pathsFromPageToElements(Cloud cloud) {
	
	   HashMap pathsFromPageToElements = new HashMap();
		// todo: create a more generic version for this piece of code
		if(isInstalled(cloud,"NatMM")) {
			for(int f = 0; f < NatMMConfig.CONTENTELEMENTS.length; f++) {
				pathsFromPageToElements.put(
					NatMMConfig.CONTENTELEMENTS[f],
					NatMMConfig.PATHS_FROM_PAGE_TO_ELEMENTS[f]);
			}
		}
		if(isInstalled(cloud,"NatNH")) {
			for(int f = 0; f < NatNHConfig.CONTENTELEMENTS.length; f++) {
				pathsFromPageToElements.put(
					NatNHConfig.CONTENTELEMENTS[f],
					NatNHConfig.PATHS_FROM_PAGE_TO_ELEMENTS[f]);
			}
      }
		if(isInstalled(cloud,"NMIntra")) {
			for(int f = 0; f < NMIntraConfig.CONTENTELEMENTS.length; f++) {
				pathsFromPageToElements.put(
					NMIntraConfig.CONTENTELEMENTS[f],
					NMIntraConfig.PATHS_FROM_PAGE_TO_ELEMENTS[f]);
			}
      }
		if(pathsFromPageToElements.size()==0) {
			log.error("CONTENTELEMENTS and PATHS_FROM_PAGE_TO_ELEMENTS are not defined by the available applications");
		}
		return pathsFromPageToElements;
   }
   			
}
