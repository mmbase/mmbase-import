/*

 This software is OSI Certified Open Source Software.
 OSI Certified is a certification mark of the Open Source Initiative.

 The license (Mozilla version 1.0) can be read at the MMBase site.
 See http://www.MMBase.org/license

 */
package com.finalist.cmsc.services.contentrepository;

import java.util.List;

import com.finalist.cmsc.beans.NodetypeBean;
import com.finalist.cmsc.beans.om.ContentChannel;
import com.finalist.cmsc.beans.om.ContentElement;
import com.finalist.cmsc.services.ServiceManager;

/**
 * @author Wouter Heijke
 */
public class ContentRepository {
   private final static ContentRepositoryService cService = (ContentRepositoryService) ServiceManager
         .getService(ContentRepositoryService.class);


   public static List<ContentElement> getContentElements(ContentChannel channel) {
      return cService.getContentElements(channel);
   }


   public static List<ContentElement> getContentElements(String channel) {
      return cService.getContentElements(channel);
   }


   public static int countContentElements(String channel, List<String> contenttypes, String orderby, String direction,
         boolean useLifecycle, String archive, int offset, int maxNumbers, int year, int month, int day, int maxDays) {
      return cService.countContentElements(channel, contenttypes, orderby, direction, useLifecycle, archive, offset,
            maxNumbers, year, month, day, maxDays);
   }
   public static int countContentElements(String channel, List<String> contenttypes, String orderby, String direction,
         boolean useLifecycle, String archive, int offset, int maxNumbers, int year, int month, int day) {
      return cService.countContentElements(channel, contenttypes, orderby, direction, useLifecycle, archive, offset,
            maxNumbers, year, month, day);
   }

   public static List<ContentElement> getContentElements(String channel, List<String> contenttypes, String orderby,
         String direction, boolean useLifecycle, String archive, int offset, int maxNumbers, int year, int month,
         int day) {
      return cService.getContentElements(channel, contenttypes, orderby, direction, useLifecycle, archive, offset,
            maxNumbers, year, month, day);
   }


   public static List<ContentChannel> getContentChannels(ContentChannel channel) {
      return cService.getContentChannels(channel);
   }


   public static List<ContentChannel> getContentChannels(String channel) {
      return cService.getContentChannels(channel);
   }


   public static List<NodetypeBean> getContentTypes() {
      return cService.getContentTypes();
   }


   public static boolean mayEdit(String number) {
      // TODO Auto-generated method stub
      return cService.mayEdit(number);
   }


   public static ContentElement getContentElement(String elementId) {
      return cService.getContentElement(elementId);
   }


   public static List<ContentElement> getContentElements(String channel, List<String> contenttypes, String orderby,
         String direction, boolean useLifecycle, String archive, int offset, int maxNumbers, int year, int month,
         int day , int maxDays) {
      return cService.getContentElements(channel, contenttypes, orderby, direction, useLifecycle, archive, offset,
            maxNumbers, year, month, day, maxDays);
   }



}
