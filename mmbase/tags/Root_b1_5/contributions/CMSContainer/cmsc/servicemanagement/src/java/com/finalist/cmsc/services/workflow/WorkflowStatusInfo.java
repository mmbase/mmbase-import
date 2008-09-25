/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package com.finalist.cmsc.services.workflow;

import java.util.Iterator;

import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeList;

public class WorkflowStatusInfo {

   private int contentDraft;
   private int contentFinished;
   private int contentApproved;
   private int contentPublished;

   private int linkDraft;
   private int linkFinished;
   private int linkApproved;
   private int linkPublished;

   private int pageDraft;
   private int pageFinished;
   private int pageApproved;
   private int pagePublished;


   public WorkflowStatusInfo(NodeList statusList) {
      for (Iterator<Node> iter = statusList.iterator(); iter.hasNext();) {
         Node node = iter.next();
         String type = node.getStringValue("type");
         String status = node.getStringValue("status");
         int count = node.getIntValue("number");
         if ("content".equals(type)) {
            if (Workflow.STATUS_DRAFT.equals(status))
               contentDraft = count;
            if (Workflow.STATUS_FINISHED.equals(status))
               contentFinished += count;
            if (Workflow.STATUS_APPROVED.equals(status)) {
               if (Workflow.isAcceptedStepEnabled()) {
                  contentApproved = count;
               }
               else {
                  contentFinished += count;
               }
            }
            if (Workflow.STATUS_PUBLISHED.equals(status))
               contentPublished = count;
         }
         if ("link".equals(type)) {
            if (Workflow.STATUS_DRAFT.equals(status))
               linkDraft = count;
            if (Workflow.STATUS_FINISHED.equals(status))
               linkFinished += count;
            if (Workflow.STATUS_APPROVED.equals(status)) {
               if (Workflow.isAcceptedStepEnabled()) {
                  linkApproved = count;
               }
               else {
                  linkFinished += count;
               }

            }
            if (Workflow.STATUS_PUBLISHED.equals(status))
               linkPublished = count;
         }
         if ("page".equals(type)) {
            if (Workflow.STATUS_DRAFT.equals(status))
               pageDraft = count;
            if (Workflow.STATUS_FINISHED.equals(status))
               pageFinished += count;
            if (Workflow.STATUS_APPROVED.equals(status)) {
               if (Workflow.isAcceptedStepEnabled()) {
                  pageApproved = count;
               }
               else {
                  pageFinished += count;
               }

            }
            if (Workflow.STATUS_PUBLISHED.equals(status))
               pagePublished = count;
         }
      }
   }


   public int getContentApproved() {
      return contentApproved;
   }


   public void setContentApproved(int contentApproved) {
      this.contentApproved = contentApproved;
   }


   public int getContentDraft() {
      return contentDraft;
   }


   public void setContentDraft(int contentDraft) {
      this.contentDraft = contentDraft;
   }


   public int getContentFinished() {
      return contentFinished;
   }


   public void setContentFinished(int contentFinished) {
      this.contentFinished = contentFinished;
   }


   public int getContentPublished() {
      return contentPublished;
   }


   public void setContentPublished(int contentPublished) {
      this.contentPublished = contentPublished;
   }


   public int getLinkApproved() {
      return linkApproved;
   }


   public void setLinkApproved(int linkApproved) {
      this.linkApproved = linkApproved;
   }


   public int getLinkDraft() {
      return linkDraft;
   }


   public void setLinkDraft(int linkDraft) {
      this.linkDraft = linkDraft;
   }


   public int getLinkFinished() {
      return linkFinished;
   }


   public void setLinkFinished(int linkFinished) {
      this.linkFinished = linkFinished;
   }


   public int getLinkPublished() {
      return linkPublished;
   }


   public void setLinkPublished(int linkPublished) {
      this.linkPublished = linkPublished;
   }


   public int getPageApproved() {
      return pageApproved;
   }


   public void setPageApproved(int pageApproved) {
      this.pageApproved = pageApproved;
   }


   public int getPageDraft() {
      return pageDraft;
   }


   public void setPageDraft(int pageDraft) {
      this.pageDraft = pageDraft;
   }


   public int getPageFinished() {
      return pageFinished;
   }


   public void setPageFinished(int pageFinished) {
      this.pageFinished = pageFinished;
   }


   public int getPagePublished() {
      return pagePublished;
   }


   public void setPagePublished(int pagePublished) {
      this.pagePublished = pagePublished;
   }
}
