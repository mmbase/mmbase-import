/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package com.finalist.cmsc.portlets.i18n;

import java.util.*;

import javax.portlet.PortletException;
import javax.portlet.RenderRequest;

import net.sf.mmapps.modules.cloudprovider.CloudProvider;
import net.sf.mmapps.modules.cloudprovider.CloudProviderFactory;

import org.mmbase.bridge.*;
import org.mmbase.bridge.util.Queries;
import org.mmbase.bridge.util.SearchUtil;
import org.mmbase.storage.search.FieldValueConstraint;

import com.finalist.cmsc.beans.MMBaseNodeMapper;
import com.finalist.cmsc.beans.om.ContentElement;
import com.finalist.cmsc.portlets.ContentChannelPortlet;
import com.finalist.cmsc.repository.RepositoryUtil;

public class I18NChannelPortlet extends ContentChannelPortlet {

   private CloudProvider cloudProvider;


   @Override
   public void init() throws PortletException {
      this.cloudProvider = CloudProviderFactory.getCloudProvider();
      super.init();
   }


   @Override
   protected int countContentElements(RenderRequest req, List<String> contenttypes, String channel, int offset,
         String orderby, String direction, String archive, int maxNumber, int year, int month, int day,
         boolean useLifecycle) {

      Node chan = getCloud().getNode(channel);

      NodeQuery query = RepositoryUtil.createLinkedContentQuery(chan, contenttypes, orderby, direction, useLifecycle,
            archive, offset, maxNumber, year, month, day);
      addLanguageConstraint(req, query);

      return Queries.count(query);
   }


   @Override
   protected List<ContentElement> getContentElements(RenderRequest req, List<String> contenttypes, String channel,
         int offset, String orderby, String direction, String archive, int maxNumber, int year, int month, int day,
         boolean useLifecycle) {

      List<ContentElement> elements = new ArrayList<ContentElement>();

      Node chan = getCloud().getNode(channel);

      NodeQuery query = RepositoryUtil.createLinkedContentQuery(chan, contenttypes, orderby, direction, useLifecycle,
            archive, offset, maxNumber, year, month, day);

      addLanguageConstraint(req, query);

      NodeList l = query.getNodeManager().getList(query);
      for (int i = 0; i < l.size(); i++) {
         Node currentNode = l.getNode(i);
         ContentElement e = (ContentElement) MMBaseNodeMapper.copyNode(currentNode, ContentElement.class);
         elements.add(e);
      }

      return elements;
   }


   private void addLanguageConstraint(RenderRequest req, NodeQuery query) {
      List<Locale> locales = getLocales(req);
      if (!locales.isEmpty()) {
         Field keyField = query.getNodeManager().getField("language");
         FieldValueConstraint constraint = SearchUtil.createEqualConstraint(query, keyField, locales.get(0)
               .getLanguage());
         SearchUtil.addConstraint(query, constraint);
      }
   }


   protected Cloud getCloud() {
      Cloud cloud = cloudProvider.getAnonymousCloud();
      return cloud;
   }
}
