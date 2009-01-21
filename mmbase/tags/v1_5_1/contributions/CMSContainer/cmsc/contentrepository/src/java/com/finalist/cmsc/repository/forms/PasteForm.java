/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package com.finalist.cmsc.repository.forms;

import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;

import com.finalist.cmsc.repository.RepositoryUtil;
import com.finalist.cmsc.security.*;
import com.finalist.cmsc.struts.TreePasteForm;

@SuppressWarnings("serial")
public class PasteForm extends TreePasteForm {

   @Override
   protected boolean isAllowed(Cloud cloud, Node channel) {
      UserRole role = RepositoryUtil.getRole(cloud, channel, false);
      return SecurityUtil.isChiefEditor(role);
   }

}
