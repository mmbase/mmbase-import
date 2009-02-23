/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package com.finalist.cmsc.repository.forms;

import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeList;

import com.finalist.cmsc.repository.RepositoryUtil;
import com.finalist.cmsc.security.SecurityUtil;
import com.finalist.cmsc.struts.TreePasteAction;

public class PasteAction extends TreePasteAction {

   private static final String NAME_FIELD = "name";
   private static final String CHANNELSUBFIX = "-Copy";
   @Override
   protected void copy(Node sourceChannel, Node destChannel) {
      Node newChannel = RepositoryUtil.copyChannel(sourceChannel, destChannel);
      renameChannel(newChannel,destChannel);

   }


   @Override
   protected void move(Node sourceChannel, Node destChannel) {
      RepositoryUtil.moveChannel(sourceChannel, destChannel);
      SecurityUtil.clearUserRoles(sourceChannel.getCloud());
   }

   private void renameChannel(Node newChannel, Node destChannel) {
      if (newChannel != null) {
         NodeList children = RepositoryUtil.getOrderedChildren(destChannel);
         outer:            
         while(true) {
            String channelName = newChannel.getStringValue(NAME_FIELD);
            for (int i = 0 ; i < children.size(); i++) {
               Node childChannel = children.getNode(i);
               if(channelName.equals(childChannel.getStringValue(NAME_FIELD)) && newChannel.getNumber() != childChannel.getNumber()) {
                  if(channelName.indexOf(CHANNELSUBFIX) > -1) {
                     if(channelName.endsWith(CHANNELSUBFIX)) {
                        newChannel.setStringValue(NAME_FIELD, channelName+"-2");
                     }
                     else {
                        int subfix = Integer.parseInt(channelName.substring(channelName.lastIndexOf("-")+1));
                        subfix++;
                        newChannel.setStringValue(NAME_FIELD, channelName.substring(0,channelName.lastIndexOf("-"))+"-"+subfix);
                     }
                  }
                  else {
                     newChannel.setStringValue(NAME_FIELD, channelName+CHANNELSUBFIX); 
                  }
                  newChannel.commit(); 
                  continue outer;
               }
               if(i == (children.size()-1)) {
                  break outer;
               }
            }
         }
      }
   }
}
