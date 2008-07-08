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
package nl.leocms.builders;

import nl.leocms.util.PublishUtil;
import nl.leocms.versioning.PublishingAction;
import nl.leocms.versioning.PublishingActionDummy;

import org.mmbase.module.core.*;
import org.mmbase.module.corebuilders.FieldDefs;
import org.mmbase.util.logging.*;


/** This builder is used when a content node should be published when the node is saved.
 * A publish property specifies if the builder should publish nodes in the current cloud.
 * 
 * On a save the builder checks if there is a publishnow field to let the node decide
 * that it should be published. The navigation node uses this mechanisme to preview it
 * in the staging cloud.
 * 
 * Another unused feature at the moment is in plac to perform an action when a node is
 * published to the current cloud. This could be handy when something has to be done
 * in the live site when the node is published
 * 
 * @author Kees Jongenburger
 * @author Nico Klasens (Finalist IT Group)
 * @version PublishingBuilder.java,v 1.2 2003/07/28 09:43:51 nico Exp
 */
public class PublishingBuilder extends MMObjectBuilder {
   private static Logger log = Logging.getLoggerInstance(PublishingBuilder.class.getName());
   private boolean publish = false;
   
   /** PublishingAction class to use when a node is published to this cloud */
   private String publishActionClassName = null;

   /**
    * test so see if mmbase doens't mix up builders and java classes
    **/
   private String myRealName;

   public boolean init() {
      if ("true".equals(getInitParameter("publish"))) {
         publish = true;
         log.info(tableName + " is active");
      } else {
         log.info(tableName + " is inactive");
      }
      
      String classname = getInitParameter("publishaction");
      if (classname != null && "".equals(classname.trim())) {
         publishActionClassName = classname;
      }

      myRealName = tableName;

      return super.init();
   }

   public int insert(String owner, MMObjectNode node) {
      int number = super.insert(owner, node);

      if (number != -1) {
         if (publish) {
            PublishUtil.PublishOrUpdateNode(number);
         }
         else {
            PublishingAction pa = getPublishingAction();
            pa.inserted(number);
         }
      }
      return number;
   }

   public boolean commit(MMObjectNode objectNode) {
      if (!myRealName.equals(tableName)) {
         log.error(
            "object builder classes and object mixed the builder class was created for objects of type(" +
            myRealName + ") but is used for (" + tableName + ")");
      }

      log.info(tableName + " commit");

      boolean retval = super.commit(objectNode);

      if (publish) {
         boolean publishnow = true;
         
         FieldDefs fielddefs = getField("publishnow");
         if (fielddefs != null) {
            publishnow = objectNode.getBooleanValue("publishnow");
         }
         // no publishnow field or publishnow field was true.
         if (publishnow) {
            PublishUtil.PublishOrUpdateNode(objectNode);
         }
      }
      else {
          PublishingAction pa = getPublishingAction();
          pa.committed(objectNode);
      }

      return retval;
   }

   public void removeNode(MMObjectNode objectNode) {
      log.info(tableName + " remove");

      if (publish) {
         PublishUtil.removeNode(objectNode);
      }
      else {
         PublishingAction pa = getPublishingAction();
         pa.removed(objectNode);
      }

      super.removeNode(objectNode);
   }
   
   /** Get a PublishingAction instance
    * 
    * @return PublishingAction instance
    */
   private PublishingAction getPublishingAction() {
      if (publishActionClassName != null) {
         try {
              Class publishActionClass = Class.forName(publishActionClassName);
              PublishingAction pa = (PublishingAction) publishActionClass.newInstance();
              return pa;
         }
         catch (ClassNotFoundException e) {
            log.warn("PublishingAction class not found: " + publishActionClassName);
         }
         catch (InstantiationException e) {
            log.warn("Unable to instantiate: " + publishActionClassName);
         }
         catch (IllegalAccessException e) {
            log.warn("Not allowed to load: " + publishActionClassName);
         }
      }
     
     return new PublishingActionDummy();
  }
}
