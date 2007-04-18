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
package nl.leocms.util;

import org.mmbase.bridge.*;
import org.mmbase.util.logging.Logging;
import org.mmbase.util.logging.Logger;

/**
 * Utility methods to create searchforms.
 *
 * @author Henk Hangyi Date : Aug 11, 2006
 */
public class ListUtil {

  Cloud cloud;
  private static final Logger log = Logging.getLoggerInstance(ListUtil.class);
  
  public ListUtil(Cloud cloud) {
      this.cloud = cloud;
  }
  
  public String getObjects(String objects, String source, String role, String destination, String nodeId) {
     StringBuffer sbObjects = new StringBuffer();
     String constraint =  "(" + destination + ".number = '" + nodeId + "')";
     log.debug("getObjects objects=" + objects + ", path=" + source + "," + role + "," + destination + ", fields=" + source + ".number" + ", constraints=" + constraint);
     if(!nodeId.equals("")) {
        NodeList nlObjects = cloud.getList(objects,
                                   source + "," + role + "," + destination,
                                   source + ".number",
                                   constraint,
                                   null,null,null,true);
        for(int n=0; n<nlObjects.size(); n++) {
           if(n>0) { sbObjects.append(','); }
           sbObjects.append(nlObjects.getNode(n).getStringValue(source + ".number"));
        }
        objects = sbObjects.toString();
        log.debug(destination + ": " + objects);
     }
     return objects;
  }
  
  public String getObjectsConstraint(String objects, String source, String path, String constraint) {
     StringBuffer sbObjects = new StringBuffer();
     log.debug("objects=" + objects + ", path=" + path + ", fields=" + source + ".number" + ", constraints=" + constraint);
     NodeList nlObjects = cloud.getList(objects, path,source + ".number","(" + constraint + ")",null,null,null,true);
     for(int n=0; n<nlObjects.size(); n++) {
        if(n>0) { sbObjects.append(','); }
        sbObjects.append(nlObjects.getNode(n).getStringValue(source + ".number"));
     }
     objects = sbObjects.toString();
     log.debug("getObjectsConstraint: " + objects);
     return objects;
  }
  
  public NodeList getRelated(String objects, String source, String role, String destination, String field, String field2, String language) {
     LocaleUtil lu = new LocaleUtil();
     String fields = destination + ".number," + destination + "." + lu.getLangFieldName(field,language);
     if (!field2.equals("")) {
        fields += "," + destination + "." + lu.getLangFieldName(field2,language);
     }
     log.debug("getRelated objects=" + objects + ", path=" + source + "," + role + "," + destination + ", fields=" + fields + ", destination=" + destination + "." + field);
     NodeList nlRelated = cloud.getList(objects,
                                   source + "," + role + "," + destination,
                                   fields,
                                   null,destination + "." + field,"UP",null,true);
     int n=0;
     String lastFields = "";
     while(n<nlRelated.size()) { // make list unique
        String thisFields = nlRelated.getNode(n).getStringValue(destination + "." + field);
        if (!field2.equals("")) {
          fields += "," + nlRelated.getNode(n).getStringValue(destination + "." + field2);
        }
        if(thisFields.equals(lastFields)) {
          nlRelated.remove(n);
        } else {
          n++;
        }
        lastFields = thisFields;
     }
     return nlRelated;
  }

  public NodeList getRelated(String objects, String source, String role, String destination, String field) {
     return getRelated(objects,source,role,destination,field,"","nl");
  }
  
  public String setSelected(String object, NodeList objectList, String field) {
     if("".equals(object)&&objectList.size()==1) { 
       object = (String) objectList.getNode(0).getStringValue(field);
     }
     return object;
  }
  
  public int count(String base, String searchFor) {
      int len = searchFor.length();
      int result = 0;
    
      if (len > 0) {  // search only if there is something
          int start = base.indexOf(searchFor);
          while (start != -1) {
              result++;
              start = base.indexOf(searchFor, start+len);
          }
      }
      return result;
  }

}

/**
 * $Log: not supported by cvs2svn $
 * Revision 1.1  2006/08/11 09:24:16  henk
 * Moved searchform related functions to class
 *
 *
 */