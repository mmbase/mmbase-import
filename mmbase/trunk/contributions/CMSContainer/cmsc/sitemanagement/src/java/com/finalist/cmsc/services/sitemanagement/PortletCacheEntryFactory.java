/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package com.finalist.cmsc.services.sitemanagement;

import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeIterator;
import org.mmbase.bridge.NodeList;
import org.mmbase.core.event.NodeEvent;
import org.mmbase.core.event.RelationEvent;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import com.finalist.cmsc.beans.MMBaseNodeMapper;
import com.finalist.cmsc.beans.om.NodeParameter;
import com.finalist.cmsc.beans.om.Portlet;
import com.finalist.cmsc.beans.om.PortletParameter;
import com.finalist.cmsc.navigation.PortletUtil;

public class PortletCacheEntryFactory extends MMBaseCacheEntryFactory {

   /** MMbase logging system */
   private static final Logger log = Logging.getLoggerInstance(PortletCacheEntryFactory.class.getName());


   public PortletCacheEntryFactory() {
      super(PortletUtil.PORTLET);
      registerListener(PortletUtil.PORTLETPARAMETER);
      registerListener(PortletUtil.NODEPARAMETER);
   }


   @Override
   protected Object loadEntry(Object key) {
      return loadPortlet((Integer) key);
   }


   private Portlet loadPortlet(Integer key) {
      Node portletNode = getNode(key);
      if (portletNode == null || !PortletUtil.isPortlet(portletNode)) {
         return null;
      }

      Portlet portlet = MMBaseNodeMapper.copyNode(portletNode, Portlet.class);

      Node definition = PortletUtil.getDefinition(portletNode);
      if (definition == null) {
         log.warn("Portlet " + portletNode.getNumber() + " does not have a definition attached");
         return null;
      }
      portlet.setDefinition(definition.getNumber());

      loadViews(portletNode, portlet);
      loadPortletParameters(portletNode, portlet);
      loadNodeParameters(portletNode, portlet);
      return portlet;
   }


   private void loadViews(Node portletNode, Portlet portlet) {
      NodeList vlist = PortletUtil.getPortletViews(portletNode);
      for (NodeIterator iter = vlist.nodeIterator(); iter.hasNext();) {
         Node viewNode = iter.nextNode();
         portlet.addView(viewNode.getNumber());
      }
   }


   private void loadPortletParameters(Node portletNode, Portlet portlet) {
      String previousKey = null;
      PortletParameter previousParam = null;
      NodeList plist = PortletUtil.getPortletParameters(portletNode);
      // list is sorted based on key field
      for (NodeIterator iter = plist.nodeIterator(); iter.hasNext();) {
         Node paramNode = iter.nextNode();
         String paramterKey = paramNode.getStringValue(PortletUtil.KEY_FIELD);
         if (previousKey == null || !previousKey.equals(paramterKey)) {
            PortletParameter param = MMBaseNodeMapper.copyNode(paramNode, PortletParameter.class);
            portlet.addPortletparameter(param);

            previousKey = paramterKey;
            previousParam = param;
         }
         else {
            String value = paramNode.getStringValue(PortletUtil.VALUE_FIELD);
            ;
            previousParam.addValue(value);
         }
      }
   }


   private void loadNodeParameters(Node portletNode, Portlet portlet) {
      String previousNodeKey = null;
      NodeParameter previousNodeParam = null;
      NodeList pnodeslist = PortletUtil.getNodeParameters(portletNode);
      for (NodeIterator iter = pnodeslist.nodeIterator(); iter.hasNext();) {
         Node paramNode = iter.nextNode();
         String paramterKey = paramNode.getStringValue(PortletUtil.KEY_FIELD);
         if (previousNodeKey == null || !previousNodeKey.equals(paramterKey)) {
            int qNodeNumber = paramNode.getIntValue(PortletUtil.VALUE_FIELD);
            if (paramNode.getCloud().hasNode(qNodeNumber)) {
               NodeParameter param = MMBaseNodeMapper.copyNode(paramNode, NodeParameter.class);
               portlet.addPortletparameter(param);

               previousNodeKey = paramterKey;
               previousNodeParam = param;
            }
         }
         else {
            String qNodeNumber = paramNode.getStringValue(PortletUtil.VALUE_FIELD);
            ;
            if (paramNode.getCloud().hasNode(qNodeNumber)) {
               previousNodeParam.addValue(qNodeNumber);
            }
         }
      }
   }


   /**
    * @see net.sf.mmapps.commons.portalImpl.services.sitemanagement.MMBaseCacheEntryFactory#getKey(org.mmbase.core.event.NodeEvent)
    */
   @Override
   protected Integer getKey(NodeEvent event) {
      int nodeNumber = event.getNodeNumber();
      if (isNodeEvent(event, PortletUtil.PORTLET)) {
         return Integer.valueOf(nodeNumber);
      }
      if (isNodeEvent(event, PortletUtil.PORTLETPARAMETER) || isNodeEvent(event, PortletUtil.NODEPARAMETER)) {

         if (event.getType() != NodeEvent.TYPE_DELETE) {
            Node parameter = getNode(nodeNumber);
            if (parameter != null) {
               Node portlet = PortletUtil.getPortletForParameter(parameter);
               if (portlet != null) {
                  return Integer.valueOf(portlet.getNumber());
               }
            }
         }
      }
      return null;
   }


   @Override
   protected Integer getKey(RelationEvent event) {
      int nodeNumber = event.getRelationSourceNumber();
      if (isRelationEvent(event, PortletUtil.PORTLET)) {
         return Integer.valueOf(nodeNumber);
      }
      if (isRelationEvent(event, PortletUtil.PORTLETPARAMETER) || isRelationEvent(event, PortletUtil.NODEPARAMETER)) {

         Node parameter = getNode(nodeNumber);
         if (parameter != null) {
            Node portlet = PortletUtil.getPortletForParameter(parameter);
            if (portlet != null) {
               return Integer.valueOf(portlet.getNumber());
            }
         }
      }
      return null;
   }


   /**
    * @see net.sf.mmapps.commons.portalImpl.services.sitemanagement.MMBaseCacheEntryFactory#isNodeEvent(org.mmbase.core.event.NodeEvent)
    */
   @Override
   protected boolean isNodeEvent(NodeEvent event) {
      return isNodeEvent(event, PortletUtil.PORTLET) || isNodeEvent(event, PortletUtil.PORTLETPARAMETER)
            || isNodeEvent(event, PortletUtil.NODEPARAMETER);
   }


   /**
    * @see net.sf.mmapps.commons.portalImpl.services.sitemanagement.MMBaseCacheEntryFactory#isRelationEvent(org.mmbase.core.event.RelationEvent)
    */
   @Override
   protected boolean isRelationEvent(RelationEvent event) {
      return isRelationEvent(event, PortletUtil.PORTLET) || isRelationEvent(event, PortletUtil.PORTLETPARAMETER)
            || isRelationEvent(event, PortletUtil.NODEPARAMETER);
   }
}
