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
 * The Original Code is LeoCMS.
 *
 * The Initial Developer of the Original Code is
 * 'De Gemeente Leeuwarden' (The dutch municipality Leeuwarden).
 *
 * See license.txt in the root of the LeoCMS directory for the full license.
 */
package nl.leocms.authorization.forms;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.mmbase.bridge.Cloud;
// import org.mmbase.bridge.CloudManager;
import org.mmbase.bridge.Node;

import com.finalist.mmbase.util.CloudFactory;

import nl.leocms.authorization.UserRole;

/**
 * @author Edwin van der Elst
 * Date :Oct 15, 2003
 * 
 */
public class Util {
   
   public static Map buildRolesFromRequest(HttpServletRequest request) {
      Map rollen = new HashMap();
      Enumeration pNames = request.getParameterNames();
      while (pNames.hasMoreElements()) {
         String name= (String) pNames.nextElement();
         if (name.startsWith("rol_")) {
            String rol = request.getParameter(name);
            if (!rol.equals("-1")) {
               rollen.put( new Integer(name.substring(4)) , new UserRole( Integer.parseInt(rol),false));
            }
         }
      }
      return rollen;
   }
   
   /** Changes password in remote cloud (cloudnode & usernod)
    * 
    * @param password
    */
   public static void updateAdminPassword(String password) {
      /* hh remote cloud not implemented at this moment

      Cloud remoteCloud = CloudManager.getCloud( CloudFactory.getCloud(),"live.leeuwarden");
      Node cloudNode = remoteCloud.getNode("cloud.remote");
      cloudNode.setStringValue("password",password);
      cloudNode.commit();
      Node adminNode = remoteCloud.getNode("users.admin");
      adminNode.setStringValue("password",password);
      adminNode.commit();
      Node defCloud = remoteCloud.getNode("cloud.default");
      defCloud.setStringValue("password",password);
      defCloud.commit();
      
      Cloud localCloud = CloudFactory.getCloud();
      Node node = localCloud.getNode("cloud.remote");
      node.setStringValue("password",password);
      node.commit();
      node = localCloud.getNode("cloud.default");
      node.setStringValue("password",password);
      node.commit();
		*/
      
   }

}
