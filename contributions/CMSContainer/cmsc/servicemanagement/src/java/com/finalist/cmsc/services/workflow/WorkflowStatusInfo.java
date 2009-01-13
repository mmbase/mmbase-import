/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package com.finalist.cmsc.services.workflow;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sf.mmapps.modules.cloudprovider.CloudProviderFactory;

import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeList;
import org.mmbase.bridge.Query;

public class WorkflowStatusInfo {

   private int allcontentDraft;
   private int allcontentFinished;
   private int allcontentApproved;
   private int allcontentPublished;
   
   private int contentDraft;
   private int contentFinished;
   private int contentApproved;
   private int contentPublished;

   private int contentArticleDraft;
   private int contentArticleFinished;
   private int contentArticleApproved;
   private int contentArticlePublished;

   private int contentBannersDraft;
   private int contentBannersFinished;
   private int contentBannersApproved;
   private int contentBannersPublished;

   private int contentLinkDraft;
   private int contentLinkFinished;
   private int contentLinkApproved;
   private int contentLinkPublished;

   private int contentFaqitemDraft;
   private int contentFaqitemFinished;
   private int contentFaqitemApproved;
   private int contentFaqitemPublished;
   
   private int assetDraft;
   private int assetFinished;
   private int assetApproved;
   private int assetPublished;
   
   private int assetImagesDraft;
   private int assetImagesFinished;
   private int assetImagesApproved;
   private int assetImagesPublished;

   private int assetAttachmentsDraft;
   private int assetAttachmentsFinished;
   private int assetAttachmentsApproved;
   private int assetAttachmentsPublished;

   private int assetUrlsDraft;
   private int assetUrlsFinished;
   private int assetUrlsApproved;
   private int assetUrlsPublished;
   
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
         String nodetype = node.getStringValue("nodetype");
         String status = node.getStringValue("status");
         int count = node.getIntValue("number");
         if ("content".equals(type)) {
            if (Workflow.STATUS_DRAFT.equals(status)){
               contentDraft += count;
               setNodetypeDraft(nodetype, count);
            }
            if (Workflow.STATUS_FINISHED.equals(status)){
               contentFinished += count;
               setNodetypeFinished(nodetype, count);
            }
            if (Workflow.STATUS_APPROVED.equals(status)) {
               if (Workflow.isAcceptedStepEnabled()) {
                  contentApproved += count;
               }
               else {
                  contentFinished += count;
               }
               setNodetypeApproved(nodetype, count);
            }
            if (Workflow.STATUS_PUBLISHED.equals(status)){
               contentPublished += count;
               setNodetypePublished(nodetype, count);
            }
         }
         if ("asset".equals(type)) {
            if (Workflow.STATUS_DRAFT.equals(status)){
               assetDraft += count;
               setNodetypeDraft(nodetype, count);
            }
            if (Workflow.STATUS_FINISHED.equals(status)){
               assetFinished += count;
               setNodetypeFinished(nodetype, count);
            }
            if (Workflow.STATUS_APPROVED.equals(status)) {
               if (Workflow.isAcceptedStepEnabled()) {
                  assetApproved += count;
               }
               else {
                  assetFinished += count;
               }
               setNodetypeApproved(nodetype, count);
            }
            if (Workflow.STATUS_PUBLISHED.equals(status)){
               assetPublished = count;
               setNodetypePublished(nodetype, count);
            }
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
      allcontentDraft = contentDraft + assetDraft;
      allcontentFinished = contentFinished + assetFinished;
      allcontentApproved = contentApproved + assetApproved;
      allcontentPublished = contentPublished + assetPublished;
   }

   
   private void setNodetypeDraft(String nodetype, int count) {
      if(getChildTypes("article").contains(nodetype))
         contentArticleDraft += count;
      if(getChildTypes("banners").contains(nodetype))
         contentBannersDraft += count;
      if(getChildTypes("link").contains(nodetype))
         contentLinkDraft += count;
      if(getChildTypes("faqitem").contains(nodetype))
         contentFaqitemDraft += count;
      if(getChildTypes("images").contains(nodetype))
         assetImagesDraft += count;
      if(getChildTypes("attachments").contains(nodetype))
         assetAttachmentsDraft += count;
      if(getChildTypes("urls").contains(nodetype))
         assetUrlsDraft += count;
   }

   
   private void setNodetypeFinished(String nodetype, int count) {
      if(getChildTypes("article").contains(nodetype))
         contentArticleFinished += count;
      if(getChildTypes("banners").contains(nodetype))
         contentBannersFinished += count;
      if(getChildTypes("link").contains(nodetype))
         contentLinkFinished += count;
      if(getChildTypes("faqitem").contains(nodetype))
         contentFaqitemFinished += count;
      if(getChildTypes("images").contains(nodetype))
         assetImagesFinished += count;
      if(getChildTypes("attachments").contains(nodetype))
         assetAttachmentsFinished += count;
      if(getChildTypes("urls").contains(nodetype))
         assetUrlsFinished += count;
   }
   
   
   private void setNodetypeApproved(String nodetype, int count) {
      if (Workflow.isAcceptedStepEnabled()) {
         if (getChildTypes("article").contains(nodetype)) contentArticleApproved += count;
         if (getChildTypes("banners").contains(nodetype)) contentBannersApproved += count;
         if (getChildTypes("link").contains(nodetype)) contentLinkApproved += count;
         if (getChildTypes("faqitem").contains(nodetype)) contentFaqitemApproved += count;
         if (getChildTypes("images").contains(nodetype)) assetImagesApproved += count;
         if (getChildTypes("attachments").contains(nodetype)) assetAttachmentsApproved += count;
         if (getChildTypes("urls").contains(nodetype)) assetUrlsApproved += count;
      } else {
         if (getChildTypes("article").contains(nodetype)) contentArticleFinished += count;
         if (getChildTypes("banners").contains(nodetype)) contentBannersFinished += count;
         if (getChildTypes("link").contains(nodetype)) contentLinkFinished += count;
         if (getChildTypes("faqitem").contains(nodetype)) contentFaqitemFinished += count;
         if (getChildTypes("images").contains(nodetype)) assetImagesFinished += count;
         if (getChildTypes("attachments").contains(nodetype)) assetAttachmentsFinished += count;
         if (getChildTypes("urls").contains(nodetype)) assetUrlsFinished += count;
      }
   }

   
   private void setNodetypePublished(String nodetype, int count) {
      if(getChildTypes("article").contains(nodetype))
         contentArticlePublished += count;
      if(getChildTypes("banners").contains(nodetype))
         contentBannersPublished += count;
      if(getChildTypes("link").contains(nodetype))
         contentLinkFinished += count;
      if(getChildTypes("faqitem").contains(nodetype))
         contentFaqitemPublished += count;
      if(getChildTypes("images").contains(nodetype))
         assetImagesPublished += count;
      if(getChildTypes("attachments").contains(nodetype))
         assetAttachmentsPublished += count;
      if(getChildTypes("urls").contains(nodetype))
         assetUrlsPublished += count;
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


   public int getAssetDraft() {
      return assetDraft;
   }


   public void setAssetDraft(int assetDraft) {
      this.assetDraft = assetDraft;
   }


   public int getAssetFinished() {
      return assetFinished;
   }


   public void setAssetFinished(int assetFinished) {
      this.assetFinished = assetFinished;
   }


   public int getAssetApproved() {
      return assetApproved;
   }


   public void setAssetApproved(int assetApproved) {
      this.assetApproved = assetApproved;
   }


   public int getAssetPublished() {
      return assetPublished;
   }


   public void setAssetPublished(int assetPublished) {
      this.assetPublished = assetPublished;
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
   
   public int getContentArticleDraft() {
      return contentArticleDraft;
   }


   public void setContentArticleDraft(int contentArticleDraft) {
      this.contentArticleDraft = contentArticleDraft;
   }


   public int getContentArticleFinished() {
      return contentArticleFinished;
   }


   public void setContentArticleFinished(int contentArticleFinished) {
      this.contentArticleFinished = contentArticleFinished;
   }


   public int getContentArticleApproved() {
      return contentArticleApproved;
   }


   public void setContentArticleApproved(int contentArticleApproved) {
      this.contentArticleApproved = contentArticleApproved;
   }


   public int getContentArticlePublished() {
      return contentArticlePublished;
   }


   public void setContentArticlePublished(int contentArticlePublished) {
      this.contentArticlePublished = contentArticlePublished;
   }


   public int getContentBannersDraft() {
      return contentBannersDraft;
   }


   public void setContentBannersDraft(int contentBannersDraft) {
      this.contentBannersDraft = contentBannersDraft;
   }


   public int getContentBannersFinished() {
      return contentBannersFinished;
   }


   public void setContentBannersFinished(int contentBannersFinished) {
      this.contentBannersFinished = contentBannersFinished;
   }


   public int getContentBannersApproved() {
      return contentBannersApproved;
   }


   public void setContentBannersApproved(int contentBannersApproved) {
      this.contentBannersApproved = contentBannersApproved;
   }


   public int getContentBannersPublished() {
      return contentBannersPublished;
   }


   public void setContentBannersPublished(int contentBannersPublished) {
      this.contentBannersPublished = contentBannersPublished;
   }


   public int getContentLinkDraft() {
      return contentLinkDraft;
   }


   public void setContentLinkDraft(int contentLinkDraft) {
      this.contentLinkDraft = contentLinkDraft;
   }


   public int getContentLinkFinished() {
      return contentLinkFinished;
   }


   public void setContentLinkFinished(int contentLinkFinished) {
      this.contentLinkFinished = contentLinkFinished;
   }


   public int getContentLinkApproved() {
      return contentLinkApproved;
   }


   public void setContentLinkApproved(int contentLinkApproved) {
      this.contentLinkApproved = contentLinkApproved;
   }


   public int getContentLinkPublished() {
      return contentLinkPublished;
   }


   public void setContentLinkPublished(int contentLinkPublished) {
      this.contentLinkPublished = contentLinkPublished;
   }


   public int getContentFaqitemDraft() {
      return contentFaqitemDraft;
   }


   public void setContentFaqitemDraft(int contentFaqitemDraft) {
      this.contentFaqitemDraft = contentFaqitemDraft;
   }


   public int getContentFaqitemFinished() {
      return contentFaqitemFinished;
   }


   public void setContentFaqitemFinished(int contentFaqitemFinished) {
      this.contentFaqitemFinished = contentFaqitemFinished;
   }


   public int getContentFaqitemApproved() {
      return contentFaqitemApproved;
   }


   public void setContentFaqitemApproved(int contentFaqitemApproved) {
      this.contentFaqitemApproved = contentFaqitemApproved;
   }


   public int getContentFaqitemPublished() {
      return contentFaqitemPublished;
   }


   public void setContentFaqitemPublished(int contentFaqitemPublished) {
      this.contentFaqitemPublished = contentFaqitemPublished;
   }


   public int getAssetImagesDraft() {
      return assetImagesDraft;
   }


   public void setAssetImagesDraft(int assetImagesDraft) {
      this.assetImagesDraft = assetImagesDraft;
   }


   public int getAssetImagesFinished() {
      return assetImagesFinished;
   }


   public void setAssetImagesFinished(int assetImagesFinished) {
      this.assetImagesFinished = assetImagesFinished;
   }


   public int getAssetImagesApproved() {
      return assetImagesApproved;
   }


   public void setAssetImagesApproved(int assetImagesApproved) {
      this.assetImagesApproved = assetImagesApproved;
   }


   public int getAssetImagesPublished() {
      return assetImagesPublished;
   }


   public void setAssetImagesPublished(int assetImagesPublished) {
      this.assetImagesPublished = assetImagesPublished;
   }


   public int getAssetAttachmentsDraft() {
      return assetAttachmentsDraft;
   }


   public void setAssetAttachmentsDraft(int assetAttachmentsDraft) {
      this.assetAttachmentsDraft = assetAttachmentsDraft;
   }


   public int getAssetAttachmentsFinished() {
      return assetAttachmentsFinished;
   }


   public void setAssetAttachmentsFinished(int assetAttachmentsFinished) {
      this.assetAttachmentsFinished = assetAttachmentsFinished;
   }


   public int getAssetAttachmentsApproved() {
      return assetAttachmentsApproved;
   }


   public void setAssetAttachmentsApproved(int assetAttachmentsApproved) {
      this.assetAttachmentsApproved = assetAttachmentsApproved;
   }


   public int getAssetAttachmentsPublished() {
      return assetAttachmentsPublished;
   }


   public void setAssetAttachmentsPublished(int assetAttachmentsPublished) {
      this.assetAttachmentsPublished = assetAttachmentsPublished;
   }


   public int getAssetUrlsDraft() {
      return assetUrlsDraft;
   }


   public void setAssetUrlsDraft(int assetUrlsDraft) {
      this.assetUrlsDraft = assetUrlsDraft;
   }


   public int getAssetUrlsFinished() {
      return assetUrlsFinished;
   }


   public void setAssetUrlsFinished(int assetUrlsFinished) {
      this.assetUrlsFinished = assetUrlsFinished;
   }


   public int getAssetUrlsApproved() {
      return assetUrlsApproved;
   }


   public void setAssetUrlsApproved(int assetUrlsApproved) {
      this.assetUrlsApproved = assetUrlsApproved;
   }


   public int getAssetUrlsPublished() {
      return assetUrlsPublished;
   }


   public void setAssetUrlsPublished(int assetUrlsPublished) {
      this.assetUrlsPublished = assetUrlsPublished;
   }
   
   public int getAllcontentDraft() {
      return allcontentDraft;
   }


   public void setAllcontentDraft(int allcontentDraft) {
      this.allcontentDraft = allcontentDraft;
   }


   public int getAllcontentFinished() {
      return allcontentFinished;
   }


   public void setAllcontentFinished(int allcontentFinished) {
      this.allcontentFinished = allcontentFinished;
   }


   public int getAllcontentApproved() {
      return allcontentApproved;
   }


   public void setAllcontentApproved(int allcontentApproved) {
      this.allcontentApproved = allcontentApproved;
   }


   public int getAllcontentPublished() {
      return allcontentPublished;
   }


   public void setAllcontentPublished(int allcontentPublished) {
      this.allcontentPublished = allcontentPublished;
   }

   private static List<String> getChildTypes(String parent) {
      Query typesQuery = CloudProviderFactory.getCloudProvider().getCloud().getNodeManager(parent).createQuery();
      NodeList types = typesQuery.getList();
      List<String> typeNames = new ArrayList<String>(); 
      for (int i = 0; i < types.size(); i++) {
         typeNames.add(((Node)types.get(i)).getNodeManager().getName());
      }
      return typeNames;
   }
}
