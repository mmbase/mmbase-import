/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package com.finalist.cmsc.forms.formprocessors;

import net.sf.mmapps.modules.cloudprovider.CloudProvider;
import net.sf.mmapps.modules.cloudprovider.CloudProviderFactory;

import org.mmbase.bridge.*;

import com.finalist.cmsc.forms.value.ValueObject;
import com.finalist.cmsc.mmbase.RelationUtil;

public class DynamicFormProcessor extends FormProcessor {

   private NodeManager savedFieldMgr = null;
   private Node savedResponse = null;
      
   public String processForm(ValueObject valueObject) {
      Cloud cloud = getCloudForAnonymousUpdate();
      savedFieldMgr = cloud.getNodeManager("savedfieldvalue");

      NodeManager savedFormMgr = cloud.getNodeManager("savedform");
      savedResponse = savedFormMgr.createNode();
      savedResponse.commit();

      processObject(valueObject);
      
      return null;
   }
   
   @Override
   protected void processField(String path, String value) {
      Node savedFieldValue = savedFieldMgr.createNode();
      savedFieldValue.setStringValue("field", path);
      savedFieldValue.setValue("value", value);
      savedFieldValue.commit();

      RelationUtil.createRelation(savedResponse, savedFieldValue, "posrel");
   }

   protected Cloud getCloudForAnonymousUpdate() {
      CloudProvider cloudProvider = CloudProviderFactory.getCloudProvider();
      Cloud cloud = cloudProvider.getCloud();
      return cloud;
   }
}
