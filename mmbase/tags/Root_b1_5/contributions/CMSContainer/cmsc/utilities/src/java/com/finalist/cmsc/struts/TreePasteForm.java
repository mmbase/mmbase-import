/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package com.finalist.cmsc.struts;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.*;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;

import com.finalist.cmsc.struts.MMBaseAction;

public abstract class TreePasteForm extends ActionForm {

   private String action;
   private String sourcePasteChannel;
   private String destPasteChannel;


   public String getAction() {
      return action;
   }


   public void setAction(String action) {
      this.action = action;
   }


   public String getDestPasteChannel() {
      return destPasteChannel;
   }


   public void setDestPasteChannel(String destPasteChannel) {
      this.destPasteChannel = destPasteChannel;
   }


   public String getSourcePasteChannel() {
      return sourcePasteChannel;
   }


   public void setSourcePasteChannel(String sourcePasteChannel) {
      this.sourcePasteChannel = sourcePasteChannel;
   }


   /**
    * Validation of form. Check if user is allowed to move or copy the channel
    * 
    * @param mappings
    *           action mappings
    * @param request
    *           http request
    * @return all errors
    */
   @Override
   public ActionErrors validate(ActionMapping mappings, HttpServletRequest request) {
      ActionErrors errors = new ActionErrors();

      Cloud cloud = MMBaseAction.getCloudFromSession(request);

      // has the user sufficient rights?
      Node destChannel = cloud.getNode(this.destPasteChannel);
      boolean isAllowed = isAllowed(cloud, destChannel);
      if (!isAllowed) {
         errors.add("destPasteChannel", new ActionMessage("navigation.paste.insufficient_rights"));
      }
      else {
         if (isMoveAction()) {
            Node sourceChannel = cloud.getNode(this.sourcePasteChannel);
            boolean isAllowed2 = isAllowed(cloud, sourceChannel);
            if (!isAllowed2) {
               errors.add("sourcePasteChannel", new ActionMessage("navigation.paste.insufficient_rights"));
            }
         }
      }

      return errors;
   }


   public boolean isMoveAction() {
      return "move".equals(action);
   }


   public boolean isCopyAction() {
      return "copy".equals(action);
   }


   protected abstract boolean isAllowed(Cloud cloud, Node channel);

}
