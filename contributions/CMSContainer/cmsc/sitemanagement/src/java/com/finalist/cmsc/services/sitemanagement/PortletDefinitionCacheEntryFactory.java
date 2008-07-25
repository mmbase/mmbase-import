/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package com.finalist.cmsc.services.sitemanagement;

import java.io.Serializable;
import java.util.List;

import org.mmbase.bridge.*;

import com.finalist.cmsc.beans.MMBaseNodeMapper;
import com.finalist.cmsc.beans.om.PortletDefinition;
import com.finalist.cmsc.navigation.PortletUtil;

public class PortletDefinitionCacheEntryFactory extends MMBaseCacheEntryFactory {

   public PortletDefinitionCacheEntryFactory() {
      super(PortletUtil.PORTLETDEFINITION);
   }


   @Override
   protected Serializable loadEntry(Serializable key) throws Exception {
      Node definitionNode = getNode(key);
      if (definitionNode == null || !PortletUtil.isDefinition(definitionNode)) {
         return null;
      }
      PortletDefinition definition = MMBaseNodeMapper.copyNode(definitionNode, PortletDefinition.class);

      NodeList nodelist = PortletUtil.getAllowedViews(definitionNode);
      NodeIterator r = nodelist.nodeIterator();
      while (r.hasNext()) {
         Node viewNode = r.nextNode();
         definition.addView(viewNode.getNumber());
      }
      Node rank = PortletUtil.getRank(definitionNode);
      if (rank != null) {
         definition.setRank(rank.getIntValue("rank"));
      }
      List<String> types = PortletUtil.getAllowedTypes(definitionNode);
      for (String type : types) {
         definition.addContenttype(type);
      }
      return definition;
   }

}
