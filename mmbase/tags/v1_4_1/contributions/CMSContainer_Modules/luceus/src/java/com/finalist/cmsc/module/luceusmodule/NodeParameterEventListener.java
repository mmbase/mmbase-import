/*

 This software is OSI Certified Open Source Software.
 OSI Certified is a certification mark of the Open Source Initiative.

 The license (Mozilla version 1.0) can be read at the MMBase site.
 See http://www.MMBase.org/license

 */
package com.finalist.cmsc.module.luceusmodule;

import net.sf.mmapps.modules.cloudprovider.CloudProviderFactory;

import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.mmbase.core.event.Event;
import org.mmbase.core.event.NodeEvent;
import org.mmbase.core.event.NodeEventListener;
import org.mmbase.module.core.MMBase;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * Handles events on content linked to a portlet
 */
public class NodeParameterEventListener implements NodeEventListener {

   protected final static String TYPE_PORTLET_PARAMETER = "nodeparameter";

   protected static final String FIELD_KEY = "key";

   protected static final String FIELD_VALUE = "value";

   protected static final String KEY_CONTENT_CHANNEL = "contentchannel";

   protected static final String KEY_CONTENT_ELEMENT = "contentelement";

   private LuceusModule module;

   private static Logger log = Logging.getLoggerInstance(NodeParameterEventListener.class.getName());


   public NodeParameterEventListener(LuceusModule module) {
      this.module = module;
      MMBase.getMMBase().addNodeRelatedEventsListener(TYPE_PORTLET_PARAMETER, this);
      log.info("registered listener for: " + TYPE_PORTLET_PARAMETER);
   }


   public void notify(NodeEvent event) {
      log.debug("NodeParameterEventListener NodeEvent: " + event.getNodeNumber());
      switch (event.getType()) {
         case Event.TYPE_CHANGE:
            if (event.getNewValue(FIELD_VALUE) != null) {
               Cloud cloud = CloudProviderFactory.getCloudProvider().getCloud();
               Node node = cloud.getNode(event.getNodeNumber());
               String updateKey = node.getStringValue(FIELD_KEY);

               int updateOldNumber = ((Integer) event.getOldValue(FIELD_VALUE)).intValue();
               doUpdateChannel(updateKey, updateOldNumber);
               
               int updateNewNumber = ((Integer) event.getNewValue(FIELD_VALUE)).intValue();
               doUpdate(updateKey, updateNewNumber);
            }
            break;
         case Event.TYPE_DELETE:
            if (event.getOldValue(FIELD_VALUE) != null) {
               String deleteKey = (String) event.getOldValue(FIELD_KEY);
               int deleteNumber = ((Integer) event.getOldValue(FIELD_VALUE)).intValue();
               doUpdate(deleteKey, deleteNumber);
            }
            break;
         case Event.TYPE_NEW:
            if (event.getNewValue(FIELD_VALUE) != null) {
               String newKey = (String) event.getNewValue(FIELD_KEY);
               int newNumber = ((Integer) event.getNewValue(FIELD_VALUE)).intValue();
               doUpdate(newKey, newNumber);
            }
            break;
      }
   }

   private void doUpdate(String key, int nodeNumber) {
      doUpdateChannel(key, nodeNumber);
      doUpdateContent(key, nodeNumber);
   }

    private void doUpdateChannel(String key, int nodeNumber) {
        if (KEY_CONTENT_CHANNEL.equals(key)) {
            module.updateContentChannelIndex(nodeNumber);
        }
    }

    private void doUpdateContent(String key, int nodeNumber) {
        if (KEY_CONTENT_ELEMENT.equals(key)) {
            module.updateContentIndex(nodeNumber);
        }
    }
}
