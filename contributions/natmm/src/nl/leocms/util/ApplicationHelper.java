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

}
