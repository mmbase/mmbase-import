package com.finalist.cmsc.beans.om;

import net.sf.mmapps.commons.beans.NodeBean;

public class WorkflowItem extends NodeBean {

   private static final long serialVersionUID = 3636369822534243850L;

   private int type;

   private String remark;

   private int status;

   public User user;

   public ContentElement contentelement;


   public String getRemark() {
      return remark;
   }


   public void setRemark(String remark) {
      this.remark = remark;
   }


   public int getStatus() {
      return status;
   }


   public void setStatus(int status) {
      this.status = status;
   }

}
