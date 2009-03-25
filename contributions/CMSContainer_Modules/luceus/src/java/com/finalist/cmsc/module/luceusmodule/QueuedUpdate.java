/*

 This software is OSI Certified Open Source Software.
 OSI Certified is a certification mark of the Open Source Initiative.

 The license (Mozilla version 1.0) can be read at the MMBase site.
 See http://www.MMBase.org/license

 */
package com.finalist.cmsc.module.luceusmodule;

/**
 * Queue command object
 * 
 * @author Freek Punt
 * @version $Revision $
 */
public class QueuedUpdate {

   public final static int METHOD_UPDATE_CONTENT_INDEX = 1;

   public final static int METHOD_UPDATE_CONTENT_CHANNEL_INDEX = 2;

   public final static int METHOD_UPDATE_PAGE_INDEX = 3;

   public final static int METHOD_UPDATE_SECONDARYCONTENT_INDEX = 4;

   // public final static int METHOD_UPDATE_RELATED_CONTENT = 5;

   public static final int METHOD_DELETE_CONTENT_INDEX = 6;

   public static final int METHOD_DELETE_PAGE_INDEX = 7;

   public static final int METHOD_DELETE_PAGECONTENT_INDEX = 8;

   public static final int METHOD_DELETE_CHANNELCONTENT_INDEX = 9;

   public final static int METHOD_CREATE_CONTENT_INDEX = 11;

   public final static int METHOD_UPDATE_CUSTOMOBJECT_INDEX = 12;   
   
   public final static int METHOD_DELETE_CUSTOMOBJECT_INDEX = 13;
   
   public final static int METHOD_CREATE_CUSTOMOBJECT_INDEX = 14;
   
   public static final int METHOD_ERASE_INDEX = 666;

   private int method;

   private int nodeNumber = 0;

   private int relatedNumber = 0;
   
   private String nodemanager = null;


   public QueuedUpdate(int method) {
      this.method = method;
   }
   
   public QueuedUpdate(int method, String nodemanager) {
      this.method = method;
      this.nodemanager = nodemanager;
   }

   public QueuedUpdate(int method, int nodeNumber) {
      this.method = method;
      this.nodeNumber = nodeNumber;
   }


   public QueuedUpdate(int method, int mainNumber, int secondaryNumber) {
      this.method = method;
      this.nodeNumber = mainNumber;
      this.relatedNumber = secondaryNumber;
   }


   public int getMethod() {
      return method;
   }


   public int getNodeNumber() {
      return nodeNumber;
   }


   public int getRelatedNodeNumber() {
      return relatedNumber;
   }


   public boolean equals(Object obj) {
      if (!(obj instanceof QueuedUpdate)) {
         return false;
      }
      QueuedUpdate qu = (QueuedUpdate) obj;

      return (method == qu.method) && (nodeNumber == qu.nodeNumber) && (relatedNumber == qu.relatedNumber);
   }


   public int hashCode() {
      int code = method * nodeNumber;
      if (relatedNumber > 0) {
         code *= relatedNumber;
      }
      return code;
   }

   public void setNodemanager(String nodemanager) {
      this.nodemanager = nodemanager;
   }

   public String getNodemanager() {
      return nodemanager;
   }

}
