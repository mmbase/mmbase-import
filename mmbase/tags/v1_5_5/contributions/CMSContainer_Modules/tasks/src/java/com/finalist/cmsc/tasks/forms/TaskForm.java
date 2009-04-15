/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package com.finalist.cmsc.tasks.forms;

import java.util.Date;

import com.finalist.cmsc.struts.MMBaseForm;

@SuppressWarnings("serial")
public class TaskForm extends MMBaseForm {

   private String title;
   private String description;
   private Date deadline;
   private String nodetype;
   private int user;


   public String getNodetype() {
      return nodetype;
   }


   public void setNodetype(String nodetype) {
      this.nodetype = nodetype;
   }


   public Date getDeadline() {
      return deadline;
   }


   public void setDeadline(Date deadline) {
      this.deadline = deadline;
   }


   public String getDeadlineStr() {
      return convertToString(deadline);
   }


   public void setDeadlineStr(String deadline) {
      this.deadline = convertToDate(deadline);
   }


   public String getDescription() {
      return description;
   }


   public void setDescription(String description) {
      this.description = description;
   }


   public String getTitle() {
      return title;
   }


   public void setTitle(String title) {
      this.title = title;
   }


   public int getUser() {
      return user;
   }


   public void setUser(int user) {
      this.user = user;
   }

}
