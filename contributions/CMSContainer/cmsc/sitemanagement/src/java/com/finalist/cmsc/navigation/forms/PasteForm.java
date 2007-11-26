/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package com.finalist.cmsc.navigation.forms;

import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;

import com.finalist.cmsc.navigation.NavigationUtil;
import com.finalist.cmsc.security.*;
import com.finalist.cmsc.struts.TreePasteForm;

public class PasteForm extends TreePasteForm {

   @Override
   protected boolean isAllowed(Cloud cloud, Node page) {
      UserRole role = NavigationUtil.getRole(cloud, page, false);
      return SecurityUtil.isChiefEditor(role);
   }

}
