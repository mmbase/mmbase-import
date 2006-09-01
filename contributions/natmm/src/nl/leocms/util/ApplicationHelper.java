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
   
   Cloud cloud;
   
   public ApplicationHelper(Cloud cloud) {
      this.cloud = cloud;
   }
	
	public boolean isInstalled(String sApplication) {
		NodeManager versionManager = cloud.getNodeManager("versions");
		return (versionManager.getList("type='application' AND name='" + sApplication + "'", null, null).size()>0);
	}
	
	
   /**
    * Returns all content types (typedefs/names) for the installed applications.
    * @return
    */
   public ArrayList getContentTypes() {
      
      ArrayList contentTypes = new ArrayList(25);
  
      // todo: create a more generic version for this piece of code
		if(isInstalled("NatMM")) {
			for(int f = 0; f < NatMMConfig.CONTENTELEMENTS.length; f++) {
            contentTypes.add(NatMMConfig.CONTENTELEMENTS[f]);
            contentTypes.add("dossier"); // dossier is not a content element, but content elements can be added to it
			}
		}
		if(isInstalled("NatNH")) {
			for(int f = 0; f < NatNHConfig.CONTENTELEMENTS.length; f++) {
            contentTypes.add(NatNHConfig.CONTENTELEMENTS[f]);
			}
      }
		if(isInstalled("NMIntra")) {
			for(int f = 0; f < NMIntraConfig.CONTENTELEMENTS.length; f++) {
            contentTypes.add(NMIntraConfig.CONTENTELEMENTS[f]);
         }
      }
		if(contentTypes.isEmpty()) {
			log.error("CONTENTELEMENTS not defined by the available applications");
		}
      
      return contentTypes;
   }

	
	
	public HashMap pathsFromPageToElements() {
	
	   HashMap pathsFromPageToElements = new HashMap();
		// todo: create a more generic version for this piece of code
		if(isInstalled("NatMM")) {
			for(int f = 0; f < NatMMConfig.OBJECTS.length; f++) {
				pathsFromPageToElements.put(
					NatMMConfig.OBJECTS[f],
					NatMMConfig.PATHS_FROM_PAGE_TO_OBJECTS[f]);
			}
		}
		if(isInstalled("NatNH")) {
			for(int f = 0; f < NatNHConfig.OBJECTS.length; f++) {
				pathsFromPageToElements.put(
					NatNHConfig.OBJECTS[f],
					NatNHConfig.PATHS_FROM_PAGE_TO_OBJECTS[f]);
			}
      }
		if(isInstalled("NMIntra")) {
			for(int f = 0; f < NMIntraConfig.OBJECTS.length; f++) {
				pathsFromPageToElements.put(
					NMIntraConfig.OBJECTS[f],
					NMIntraConfig.PATHS_FROM_PAGE_TO_OBJECTS[f]);
			}
      }
		if(pathsFromPageToElements.size()==0) {
			log.error("OBJECTS and PATHS_FROM_PAGE_TO_OBJECTS are not defined by the available applications");
		}
		return pathsFromPageToElements;
   }
   			
   /**
    * Returns an comma separated list for all content types names
    * @return
    */
   public String getContentTypesCommaSeparated() {
      StringBuffer ret = new StringBuffer();

      for (Iterator iter = getContentTypes().iterator(); iter.hasNext();) {
         String t = (String) iter.next();
         ret.append("'");
         ret.append(t);
         ret.append("'");
         if (iter.hasNext()) {
            ret.append(",");
         }
      }
      return ret.toString();
   }
}
