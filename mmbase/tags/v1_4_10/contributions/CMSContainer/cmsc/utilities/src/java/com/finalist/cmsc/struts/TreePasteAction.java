/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package com.finalist.cmsc.struts;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.*;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;

import com.finalist.cmsc.struts.MMBaseAction;

public abstract class TreePasteAction extends MMBaseAction {

   @Override
   public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
         HttpServletResponse response, Cloud cloud) throws Exception {

      TreePasteForm pasteForm = (TreePasteForm) form;

      Node sourceChannel = cloud.getNode(pasteForm.getSourcePasteChannel());
      Node destChannel = cloud.getNode(pasteForm.getDestPasteChannel());

      if (pasteForm.isMoveAction()) {
         move(sourceChannel, destChannel);
      }
      else if (pasteForm.isCopyAction()) {
         copy(sourceChannel, destChannel);
      }

      return mapping.findForward(SUCCESS);

   }


   protected abstract void copy(Node sourceChannel, Node destChannel);


   protected abstract void move(Node sourceChannel, Node destChannel);

}
