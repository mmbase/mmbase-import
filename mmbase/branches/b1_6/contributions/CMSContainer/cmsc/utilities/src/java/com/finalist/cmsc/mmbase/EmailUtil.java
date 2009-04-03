/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package com.finalist.cmsc.mmbase;

import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.mmbase.util.functions.Function;

import org.apache.commons.lang.StringUtils;
import net.sf.mmapps.modules.cloudprovider.CloudProviderFactory;

public class EmailUtil {

   private static final String EMAIL = "email";


   public static void send(String name, String email, String subject, String body) {
      send(null, name, email, null, null, subject, body);
   }


   public static void send(Cloud cloud, String name, String email, String nameFrom, String emailFrom, String subject,
         String body) {

      if (cloud == null) {
         cloud = CloudProviderFactory.getCloudProvider().getAdminCloud();
      }

      if (StringUtils.isEmpty(emailFrom) || StringUtils.isEmpty(nameFrom)) {
         emailFrom = PropertiesUtil.getProperty("mail.system.email");
         nameFrom = PropertiesUtil.getProperty("mail.system.name");
      }

      if (StringUtils.isEmpty(emailFrom)){
         throw new IllegalStateException("The 'mail.system.email' setting is missing in the System properties.");
      }      
      if (StringUtils.isEmpty(nameFrom)){
         throw new IllegalStateException("The 'mail.system.name' setting is missing in the System properties.");
      }
             
      Node emailNode = cloud.getNodeManager(EMAIL).createNode();
      emailNode.setStringValue("subject", subject);
      emailNode.setStringValue("body", body);

      String from = StringUtils.isEmpty(nameFrom) ? emailFrom : nameFrom + "<" + emailFrom + ">";
      String to = StringUtils.isEmpty(name) ? email : name + "<" + email + ">";

      emailNode.setStringValue("from", from);
      emailNode.setStringValue("to", to);

      emailNode.commit();

      Function func = emailNode.getFunction("mail");
      func.getFunctionValue(func.createParameters());
   }

}
