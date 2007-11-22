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
import nl.leocms.util.tools.SearchUtil;
import org.mmbase.bridge.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

public class ApplicationHelper {
   
   /** Logger instance. */
   private static Logger log = Logging.getLoggerInstance(ApplicationHelper.class.getName());
   
   Cloud cloud;
   boolean isInstalledNatMM;
   boolean isInstalledNatNH;
   boolean isInstalledNMIntra;
   boolean isInstalledVanHam;
   
   
   public ApplicationHelper(Cloud cloud) {
      this.cloud = cloud;
      this.isInstalledNatMM = isInstalled("NatMM");
      this.isInstalledNatNH = isInstalled("NatNH");
      this.isInstalledNMIntra = isInstalled("NMIntra");      
      this.isInstalledVanHam = isInstalled("VanHam");
   }
	
   public boolean isInstalled(String sApplication) {
      NodeManager versionManager = cloud.getNodeManager("versions");
      return (versionManager.getList("type='application' AND name='" + sApplication + "'", null, null).size()>0);
   }
	
   /**
    * Returns all "meaningfull" content types (typedefs/names) for the installed application.
    * The list of all contentelements ( cloud.getNodeManager("contentelement").getDescendants() ) contains to many entries to be usefull
    * @return
    */
   public ArrayList getContentTypes(boolean addContainers) {
     
      ArrayList contentTypes = new ArrayList(25);
      
      // todo: create a more generic version for this piece of code
      if(isInstalledNatMM) {
        for(int f = 0; f < NatMMConfig.CONTENTELEMENTS.length; f++) {
          contentTypes.add(NatMMConfig.CONTENTELEMENTS[f]);
        }
      }
      if(isInstalledNatNH) {
        for(int f = 0; f < NatNHConfig.CONTENTELEMENTS.length; f++) {
          contentTypes.add(NatNHConfig.CONTENTELEMENTS[f]);
        }
      }
      if(isInstalledNMIntra) {
        for(int f = 0; f < NMIntraConfig.CONTENTELEMENTS.length; f++) {
          contentTypes.add(NMIntraConfig.CONTENTELEMENTS[f]);
        }
      }
      if(isInstalledVanHam) {
        for(int f = 0; f < VanHamConfig.CONTENTELEMENTS.length; f++) {
          contentTypes.add(VanHamConfig.CONTENTELEMENTS[f]);
        }
      }
      if(addContainers) {
        contentTypes.addAll(getContainerTypes());
      }
      if(contentTypes.isEmpty()) {
        log.error("CONTENTELEMENTS not defined by the available applications");
      }

      return contentTypes;
   }
	
   
   /**
    * Returns all list of containers for the installed application.
    * Containers are types that are no contentelement but are used as containers for contelements
    * @return
    */
   public ArrayList getContainerTypes() {
     
      ArrayList containerTypes = new ArrayList(25);
      
      // todo: create a more generic version for this piece of code
      if(isInstalledNatMM) {
        for(int f = 0; f < NatMMConfig.CONTAINERS.length; f++) {
          containerTypes.add(NatMMConfig.CONTAINERS[f]);
        }
      }
      if(isInstalledNMIntra) {
        for(int f = 0; f < NMIntraConfig.CONTAINERS.length; f++) {
          containerTypes.add(NMIntraConfig.CONTAINERS[f]);
        }
      }
      return containerTypes;
   }
   
   public HashMap pathsFromPageToElements() {
	
      HashMap pathsFromPageToElements = new HashMap();
      // todo: create a more generic version for this piece of code
      if(isInstalledNatMM) {
        for(int f = 0; f < NatMMConfig.OBJECTS.length; f++) {
          pathsFromPageToElements.put(
            NatMMConfig.OBJECTS[f],
            NatMMConfig.PATHS_FROM_PAGE_TO_OBJECTS[f]);
        }
      }
      if(isInstalledNatNH) {
        for(int f = 0; f < NatNHConfig.OBJECTS.length; f++) {
          pathsFromPageToElements.put(
            NatNHConfig.OBJECTS[f],
            NatNHConfig.PATHS_FROM_PAGE_TO_OBJECTS[f]);
        }
      }
      if(isInstalledNMIntra) {
        for(int f = 0; f < NMIntraConfig.OBJECTS.length; f++) {
          pathsFromPageToElements.put(
            NMIntraConfig.OBJECTS[f],
            NMIntraConfig.PATHS_FROM_PAGE_TO_OBJECTS[f]);
        }
      }
      if(isInstalledVanHam) {
        for(int f = 0; f < NMIntraConfig.OBJECTS.length; f++) {
          pathsFromPageToElements.put(
            VanHamConfig.OBJECTS[f],
            VanHamConfig.PATHS_FROM_PAGE_TO_OBJECTS[f]);
        }
      }
      if(pathsFromPageToElements.size()==0) {
        log.error("OBJECTS and PATHS_FROM_PAGE_TO_OBJECTS are not defined by the available applications");
      }
      return pathsFromPageToElements;
   }
   
   public String getDefaultPage(String thisNode, String thisType){

      // some exceptions of objects belonging to pages, but not actually related
      String sPaginaNumber = null;
      if (isInstalledNatMM) {
         if (thisType.equals("evenementen")) {
            sPaginaNumber = cloud.getNodeByAlias("agenda").getStringValue("number");
         }
      }
      if (isInstalledNMIntra) {
         if (thisType.equals("medewerkers")
            && thisNode!=null
            && cloud.getList(thisNode,"medewerkers","medewerkers.number",SearchUtil.sEmployeeConstraint,null,null,null,false).size()==1 ) {
            sPaginaNumber = cloud.getNodeByAlias("wieiswie").getStringValue("number");
         }
         if (thisType.equals("afdelingen")
            && thisNode!=null
            && cloud.getList(thisNode,"afdelingen","afdelingen.number",SearchUtil.sAfdelingenConstraints,null,null,null,false).size()==1 ) {
            sPaginaNumber = cloud.getNodeByAlias("wieiswie").getStringValue("number");
         }
         if (thisType.equals("educations")) {
            sPaginaNumber = cloud.getNodeByAlias("educations").getStringValue("number");
         }
         if (thisType.equals("evenement_blueprint")) {
            sPaginaNumber = cloud.getNodeByAlias("events").getStringValue("number");               
         }
         if (thisType.equals("projects")) {
            sPaginaNumber = cloud.getNodeByAlias("projects").getStringValue("number");
         }
      }
      return sPaginaNumber;
   }

   public String getRootDir() {
      if (isInstalledNatMM) {
         return NatMMConfig.getRootDir();
      }
      return null;
   }

   public String getTempDir() {
      if (isInstalledNatMM) {
         return NatMMConfig.getTempDir();
      }
      if (isInstalledNMIntra) {
         return NMIntraConfig.getTempDir();
      }
      return null;
   }

   public String getIncomingDir() {
      if (isInstalledNatMM) {
         return NatMMConfig.getIncomingDir();
      }
      if (isInstalledNMIntra) {
         return NMIntraConfig.getIncomingDir();
      } 
      return null;
   }
   
   public String getToEmailAddress() {
      if (isInstalledNatMM) {
         return NatMMConfig.getToEmailAddress();
      }
      if (isInstalledNMIntra) {
         return NMIntraConfig.getToEmailAddress();
      }
      return null;
   }

   public String getFromEmailAddress() {
      if (isInstalledNatMM) {
         return NatMMConfig.getFromEmailAddress();
      }
      if (isInstalledNMIntra) {
         return NMIntraConfig.getFromEmailAddress();
      }
      return null;
   }

   public String getSiteUrl() {
     String siteUrl = null;
      if (isInstalledNatMM) {
         siteUrl = NatMMConfig.getLiveUrl();
      }
      if(siteUrl!=null) {
         siteUrl = siteUrl.substring(0,siteUrl.length()-1);
      }
      return siteUrl;
   }
   
   /**
    * Returns an comma separated list for all content types names
    * @return
    */
   public String getContentTypesCommaSeparated() {
      StringBuffer ret = new StringBuffer();

      for (Iterator iter = getContentTypes(false).iterator(); iter.hasNext();) {
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
