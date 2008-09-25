/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package com.finalist.cmsc.repository.forms;

import org.mmbase.bridge.Node;

import com.finalist.cmsc.repository.RepositoryUtil;
import com.finalist.cmsc.security.SecurityUtil;
import com.finalist.cmsc.struts.TreePasteAction;

public class PasteAction extends TreePasteAction {

   @Override
   protected void copy(Node sourceChannel, Node destChannel) {
      RepositoryUtil.copyChannel(sourceChannel, destChannel);
   }


   @Override
   protected void move(Node sourceChannel, Node destChannel) {
      RepositoryUtil.moveChannel(sourceChannel, destChannel);
      SecurityUtil.clearUserRoles(sourceChannel.getCloud());
   }

}
