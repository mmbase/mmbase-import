/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package com.finalist.cmsc.mmbase;

import java.util.ArrayList;
import java.util.List;

public class TypeUtil {

   private TypeUtil() {
      // utility
   }

   private static final List<String> mmbaseTypes = new ArrayList<String>();
   private static final List<String> securityTypes = new ArrayList<String>();
   private static final List<String> publishTypes = new ArrayList<String>();

   private static final List<String> systemTypes = new ArrayList<String>();
   static {
      mmbaseTypes.add("typedef");
      mmbaseTypes.add("reldef");
      mmbaseTypes.add("typerel");
      mmbaseTypes.add("mmservers");
      mmbaseTypes.add("oalias");
      mmbaseTypes.add("daymarks");
      mmbaseTypes.add("syncnodes");
      mmbaseTypes.add("icaches");
      mmbaseTypes.add("versions");

      securityTypes.add("user");
      securityTypes.add("mmbasegroups");
      securityTypes.add("mmbaseranks");
      securityTypes.add("mmbaseusers");
      securityTypes.add("mmbasecontexts");
      securityTypes.add("rightsrel");

      publishTypes.add("remotenodes");
      publishTypes.add("cloud");
      publishTypes.add("publishqueue");

      systemTypes.addAll(mmbaseTypes);
      systemTypes.addAll(securityTypes);
      systemTypes.addAll(publishTypes);

      systemTypes.add("editwizards");
      systemTypes.add("workflowitem");
      systemTypes.add("properties");

      systemTypes.add("cronjobs");
      systemTypes.add("email");
   }


   public static boolean isMmbaseType(String name, boolean includeRootTypes) {
      if (includeRootTypes && ("object".equals(name) || "insrel".equals(name))) {
         return true;
      }
      return mmbaseTypes.contains(name);
   }


   public static boolean isMmbaseType(String name) {
      return isMmbaseType(name, false);
   }


   public static boolean isSecurityType(String name) {
      return securityTypes.contains(name);
   }


   public static boolean isPublishType(String name) {
      return publishTypes.contains(name);
   }


   public static boolean isSystemType(String name, boolean includeRootTypes) {
      if (includeRootTypes && ("object".equals(name) || "insrel".equals(name))) {
         return true;
      }
      return systemTypes.contains(name);
   }


   public static boolean isSystemType(String name) {
      return isSystemType(name, false);
   }

}
