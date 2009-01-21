/*
 * 
 * This software is OSI Certified Open Source Software. OSI Certified is a certification mark of the Open Source
 * Initiative.
 * 
 * The license (Mozilla version 1.0) can be read at the MMBase site. See http://www.MMBase.org/license
 */
package com.finalist.cmsc.services.workflow;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.struts.util.LabelValueBean;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeList;

import com.finalist.cmsc.services.ServiceUtil;

public class WorkflowStatusInfo {

   private static String CONTENTELEMENT = "contentelement";
   private static String ASSETELEMENT = "assetelement";
   
   private List<LabelValueBean> contentChildTypes ;
   private List<LabelValueBean> assetChildTypes ;

   private int allcontentDraft;
   private int allcontentFinished;
   private int allcontentApproved;
   private int allcontentPublished;

   private int contentDraft;
   private int contentFinished;
   private int contentApproved;
   private int contentPublished;

   private Map<String, Integer> contentChildrenDraft = new HashMap<String, Integer>();
   private Map<String, Integer> contentChildrenFinished = new HashMap<String, Integer>();
   private Map<String, Integer> contentChildrenApproved = new HashMap<String, Integer>();
   private Map<String, Integer> contentChildrenPublished = new HashMap<String, Integer>();

   private int assetDraft;
   private int assetFinished;
   private int assetApproved;
   private int assetPublished;

   private Map<String, Integer> assetChildrenDraft = new HashMap<String, Integer>();
   private Map<String, Integer> assetChildrenFinished = new HashMap<String, Integer>();
   private Map<String, Integer> assetChildrenApproved = new HashMap<String, Integer>();
   private Map<String, Integer> assetChildrenPublished = new HashMap<String, Integer>();

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

   public WorkflowStatusInfo(Cloud cloud,NodeList statusList) {
      
      contentChildTypes = ServiceUtil.getDirectChildTypes(cloud,CONTENTELEMENT);
      assetChildTypes = ServiceUtil.getDirectChildTypes(cloud,ASSETELEMENT);
      // initialization
      for (LabelValueBean childType : contentChildTypes) {
         contentChildrenDraft.put(childType.getValue(), 0);
         contentChildrenFinished.put(childType.getValue(), 0);
         contentChildrenApproved.put(childType.getValue(), 0);
         contentChildrenPublished.put(childType.getValue(), 0);
      }
      for (LabelValueBean childType : assetChildTypes) {
         assetChildrenDraft.put(childType.getValue(), 0);
         assetChildrenFinished.put(childType.getValue(), 0);
         assetChildrenApproved.put(childType.getValue(), 0);
         assetChildrenPublished.put(childType.getValue(), 0);
      }

      for (Iterator<Node> iter = statusList.iterator(); iter.hasNext();) {
         Node node = iter.next();
         String type = node.getStringValue("type");
         String nodetype = node.getStringValue("nodetype");
         String status = node.getStringValue("status");
         int count = node.getIntValue("number");
         if ("content".equals(type)) {
            if (Workflow.STATUS_DRAFT.equals(status)) {
               contentDraft += count;
               setNodetypeStatus(cloud,contentChildTypes, contentChildrenDraft, nodetype, count);
            }
            if (Workflow.STATUS_FINISHED.equals(status)) {
               contentFinished += count;
               setNodetypeStatus(cloud,contentChildTypes, contentChildrenFinished, nodetype, count);
            }
            if (Workflow.STATUS_APPROVED.equals(status)) {
               if (Workflow.isAcceptedStepEnabled()) {
                  contentApproved += count;
                  setNodetypeStatus(cloud,contentChildTypes, contentChildrenApproved, nodetype, count);
               } else {
                  contentFinished += count;
                  setNodetypeStatus(cloud,contentChildTypes, contentChildrenFinished, nodetype, count);
               }
            }
            if (Workflow.STATUS_PUBLISHED.equals(status)) {
               contentPublished += count;
               setNodetypeStatus(cloud,contentChildTypes, contentChildrenPublished, nodetype, count);
            }
         }
         if ("asset".equals(type)) {
            if (Workflow.STATUS_DRAFT.equals(status)) {
               assetDraft += count;
               setNodetypeStatus(cloud,assetChildTypes, assetChildrenDraft, nodetype, count);
            }
            if (Workflow.STATUS_FINISHED.equals(status)) {
               assetFinished += count;
               setNodetypeStatus(cloud,assetChildTypes, assetChildrenFinished, nodetype, count);
            }
            if (Workflow.STATUS_APPROVED.equals(status)) {
               if (Workflow.isAcceptedStepEnabled()) {
                  assetApproved += count;
                  setNodetypeStatus(cloud,assetChildTypes, assetChildrenApproved, nodetype, count);
               } else {
                  assetFinished += count;
                  setNodetypeStatus(cloud,assetChildTypes, assetChildrenFinished, nodetype, count);
               }
            }
            if (Workflow.STATUS_PUBLISHED.equals(status)) {
               assetPublished = count;
               setNodetypeStatus(cloud,assetChildTypes, assetChildrenPublished, nodetype, count);
            }
         }
         if ("link".equals(type)) {
            if (Workflow.STATUS_DRAFT.equals(status)) linkDraft = count;
            if (Workflow.STATUS_FINISHED.equals(status)) linkFinished += count;
            if (Workflow.STATUS_APPROVED.equals(status)) {
               if (Workflow.isAcceptedStepEnabled()) {
                  linkApproved = count;
               } else {
                  linkFinished += count;
               }

            }
            if (Workflow.STATUS_PUBLISHED.equals(status)) linkPublished = count;
         }
         if ("page".equals(type)) {
            if (Workflow.STATUS_DRAFT.equals(status)) pageDraft = count;
            if (Workflow.STATUS_FINISHED.equals(status)) pageFinished += count;
            if (Workflow.STATUS_APPROVED.equals(status)) {
               if (Workflow.isAcceptedStepEnabled()) {
                  pageApproved = count;
               } else {
                  pageFinished += count;
               }

            }
            if (Workflow.STATUS_PUBLISHED.equals(status)) pagePublished = count;
         }
      }
      allcontentDraft = contentDraft + assetDraft;
      allcontentFinished = contentFinished + assetFinished;
      allcontentApproved = contentApproved + assetApproved;
      allcontentPublished = contentPublished + assetPublished;
   }

   private void setNodetypeStatus(Cloud cloud,List<LabelValueBean> childTypes, Map<String, Integer> childrenStatus, String nodetype,
         int count) {
      for (LabelValueBean childType : childTypes) {
         String childTypeName = childType.getValue();
         if (nodetype.equals(childTypeName)||ServiceUtil.getAllChildTypes(cloud,childTypeName).contains(nodetype)) {
            int temp = 0;
            if (childrenStatus.containsKey(childTypeName)) {
               temp = childrenStatus.get(childTypeName);
            }
            childrenStatus.put(childTypeName, temp + count);
            break;
         }
      }
   }

   public int getAllcontentDraft() {
      return allcontentDraft;
   }

   public int getAllcontentFinished() {
      return allcontentFinished;
   }

   public int getAllcontentApproved() {
      return allcontentApproved;
   }

   public int getAllcontentPublished() {
      return allcontentPublished;
   }

   public int getContentDraft() {
      return contentDraft;
   }

   public int getContentFinished() {
      return contentFinished;
   }

   public int getContentApproved() {
      return contentApproved;
   }

   public int getContentPublished() {
      return contentPublished;
   }

   public Map<String, Integer> getContentChildrenDraft() {
      return contentChildrenDraft;
   }

   public Map<String, Integer> getContentChildrenFinished() {
      return contentChildrenFinished;
   }

   public Map<String, Integer> getContentChildrenApproved() {
      return contentChildrenApproved;
   }

   public Map<String, Integer> getContentChildrenPublished() {
      return contentChildrenPublished;
   }

   public int getAssetDraft() {
      return assetDraft;
   }

   public int getAssetFinished() {
      return assetFinished;
   }

   public int getAssetApproved() {
      return assetApproved;
   }

   public int getAssetPublished() {
      return assetPublished;
   }

   public Map<String, Integer> getAssetChildrenDraft() {
      return assetChildrenDraft;
   }

   public Map<String, Integer> getAssetChildrenFinished() {
      return assetChildrenFinished;
   }

   public Map<String, Integer> getAssetChildrenApproved() {
      return assetChildrenApproved;
   }

   public Map<String, Integer> getAssetChildrenPublished() {
      return assetChildrenPublished;
   }

   public int getAssetUrlsDraft() {
      return assetUrlsDraft;
   }

   public int getAssetUrlsFinished() {
      return assetUrlsFinished;
   }

   public int getAssetUrlsApproved() {
      return assetUrlsApproved;
   }

   public int getAssetUrlsPublished() {
      return assetUrlsPublished;
   }

   public int getLinkDraft() {
      return linkDraft;
   }

   public int getLinkFinished() {
      return linkFinished;
   }

   public int getLinkApproved() {
      return linkApproved;
   }

   public int getLinkPublished() {
      return linkPublished;
   }

   public int getPageDraft() {
      return pageDraft;
   }

   public int getPageFinished() {
      return pageFinished;
   }

   public int getPageApproved() {
      return pageApproved;
   }

   public int getPagePublished() {
      return pagePublished;
   }

   public List<LabelValueBean> getContentChildTypes() {
      return contentChildTypes;
   }

   public List<LabelValueBean> getAssetChildTypes() {
      return assetChildTypes;
   }

   public void setContentChildTypes(List<LabelValueBean> contentChildTypes) {
      this.contentChildTypes = contentChildTypes;
   }

   public void setAssetChildTypes(List<LabelValueBean> assetChildTypes) {
      this.assetChildTypes = assetChildTypes;
   }

   public void setAllcontentDraft(int allcontentDraft) {
      this.allcontentDraft = allcontentDraft;
   }

   public void setAllcontentFinished(int allcontentFinished) {
      this.allcontentFinished = allcontentFinished;
   }

   public void setAllcontentApproved(int allcontentApproved) {
      this.allcontentApproved = allcontentApproved;
   }

   public void setAllcontentPublished(int allcontentPublished) {
      this.allcontentPublished = allcontentPublished;
   }

   public void setContentDraft(int contentDraft) {
      this.contentDraft = contentDraft;
   }

   public void setContentFinished(int contentFinished) {
      this.contentFinished = contentFinished;
   }

   public void setContentApproved(int contentApproved) {
      this.contentApproved = contentApproved;
   }

   public void setContentPublished(int contentPublished) {
      this.contentPublished = contentPublished;
   }

   public void setContentChildrenDraft(Map<String, Integer> contentChildrenDraft) {
      this.contentChildrenDraft = contentChildrenDraft;
   }

   public void setContentChildrenFinished(Map<String, Integer> contentChildrenFinished) {
      this.contentChildrenFinished = contentChildrenFinished;
   }

   public void setContentChildrenApproved(Map<String, Integer> contentChildrenApproved) {
      this.contentChildrenApproved = contentChildrenApproved;
   }

   public void setContentChildrenPublished(Map<String, Integer> contentChildrenPublished) {
      this.contentChildrenPublished = contentChildrenPublished;
   }

   public void setAssetDraft(int assetDraft) {
      this.assetDraft = assetDraft;
   }

   public void setAssetFinished(int assetFinished) {
      this.assetFinished = assetFinished;
   }

   public void setAssetApproved(int assetApproved) {
      this.assetApproved = assetApproved;
   }

   public void setAssetPublished(int assetPublished) {
      this.assetPublished = assetPublished;
   }

   public void setAssetChildrenDraft(Map<String, Integer> assetChildrenDraft) {
      this.assetChildrenDraft = assetChildrenDraft;
   }

   public void setAssetChildrenFinished(Map<String, Integer> assetChildrenFinished) {
      this.assetChildrenFinished = assetChildrenFinished;
   }

   public void setAssetChildrenApproved(Map<String, Integer> assetChildrenApproved) {
      this.assetChildrenApproved = assetChildrenApproved;
   }

   public void setAssetChildrenPublished(Map<String, Integer> assetChildrenPublished) {
      this.assetChildrenPublished = assetChildrenPublished;
   }

   public void setAssetUrlsDraft(int assetUrlsDraft) {
      this.assetUrlsDraft = assetUrlsDraft;
   }

   public void setAssetUrlsFinished(int assetUrlsFinished) {
      this.assetUrlsFinished = assetUrlsFinished;
   }

   public void setAssetUrlsApproved(int assetUrlsApproved) {
      this.assetUrlsApproved = assetUrlsApproved;
   }

   public void setAssetUrlsPublished(int assetUrlsPublished) {
      this.assetUrlsPublished = assetUrlsPublished;
   }

   public void setLinkDraft(int linkDraft) {
      this.linkDraft = linkDraft;
   }

   public void setLinkFinished(int linkFinished) {
      this.linkFinished = linkFinished;
   }

   public void setLinkApproved(int linkApproved) {
      this.linkApproved = linkApproved;
   }

   public void setLinkPublished(int linkPublished) {
      this.linkPublished = linkPublished;
   }

   public void setPageDraft(int pageDraft) {
      this.pageDraft = pageDraft;
   }

   public void setPageFinished(int pageFinished) {
      this.pageFinished = pageFinished;
   }

   public void setPageApproved(int pageApproved) {
      this.pageApproved = pageApproved;
   }

   public void setPagePublished(int pagePublished) {
      this.pagePublished = pagePublished;
   }

}
