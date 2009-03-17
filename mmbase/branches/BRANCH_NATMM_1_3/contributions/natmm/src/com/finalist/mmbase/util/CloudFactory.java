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
package com.finalist.mmbase.util;

import org.mmbase.bridge.*;
import org.apache.log4j.Category;

import java.util.Map;
import java.util.HashMap;

/**
 * Created by vincent (Finalist IT Group)
 * User: vincent
 * Date: Mar 27, 2003
 * Time: 6:40:16 PM
 * @author vincent (Finalist IT Group)
 * @version $Id: CloudFactory.java,v 1.3 2006-11-08 14:14:52 henk Exp $
 */
public class CloudFactory {
	private static final Category log = Category.getInstance(CloudFactory.class);

	private static Cloud mmbase;

	/**
	 * Returns the admin instance of the Cloud
	 * @return The admin Cloud
	 */
	public static Cloud getCloud() {
		if (mmbase == null) {
         log.info("Cloud not yet initialized. Doing now!");
         final CloudContext context = ContextProvider.getCloudContext("local");
         final Map loginInfo = getAdminUserCredentials();
         mmbase = context.getCloud("mmbase", "name/password", loginInfo);
      }
      return mmbase;
	}

	/**
	 * Returns the admin instance of the Cloud
	 * @return The admin Cloud
	 * @deprecated Use getCloud() instead
	 */
	public static Cloud createCloud() {
		return getCloud();
	}
   
   public static Map getAdminUserCredentials() {
      // Retrieve username/password using node alias for admin users node.
      CloudContext context = ContextProvider.getCloudContext("local");
      Cloud cloud = context.getCloud("mmbase");
      Node node = cloud.getNode("users.admin");
      String username = node.getStringValue("account");
      String password = node.getStringValue("password");
      Map result = new HashMap(3, 0.7f);
      result.put("username", username);
      result.put("password", password);
      return result;
   }
   
   public static Map getUserCredentials(String username) {
      // Retrieve username/password using username
      Cloud cloud = getCloud();
      Node user = null;
      NodeManager nm = cloud.getNodeManager("users");
      NodeList nl = nm.getList("users.account = '" + username + "'",null,null);
      if(nl.size()==0) {
        // better create one, since it probably is needed somewhere
        user = nm.createNode();
        user.setStringValue("account",username);
        user.setStringValue("password","p" + Math.random());
        user.setStringValue("rank","basic user");
        user.setStringValue("expiredate","2145881590");
        user.setStringValue("voornaam","Website");
        user.setStringValue("achternaam","User");
        user.commit();
      } else {
        user = nl.getNode(0);
      }
      String password = user.getStringValue("password");
      Map result = new HashMap(3, 0.7f);
      result.put("username", username);
      result.put("password", password);
      return result;
   }
}