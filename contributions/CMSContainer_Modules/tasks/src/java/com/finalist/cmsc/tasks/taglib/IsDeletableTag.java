package com.finalist.cmsc.tasks.taglib;

import javax.servlet.jsp.JspTagException;

import org.mmbase.bridge.jsp.taglib.CloudReferrerTag;
import org.mmbase.bridge.jsp.taglib.Condition;
import org.mmbase.bridge.jsp.taglib.util.Attribute;

import com.finalist.cmsc.tasks.TasksUtil;

/**
 * A very simple tag to check whether or not a task is deletable.
 *
 * @author Marco
 *
 */
@SuppressWarnings("serial")
public class IsDeletableTag extends CloudReferrerTag implements Condition {

   protected Attribute number = Attribute.NULL;
   protected Attribute inverse  = Attribute.NULL;

   public void setNumber(String s) throws JspTagException {
      number = getAttribute(s);
   }

   public void setInverse(String b) throws JspTagException {
       inverse = getAttribute(b);
   }
   protected boolean getInverse() throws JspTagException {
       return inverse.getBoolean(this, false);
   }

   @Override
   public int doStartTag() throws JspTagException {
       boolean result = true;
       String numberString = number.getString(this);
       if (numberString.length() > 0) {
           result = TasksUtil.isDeleteable(getCloudVar().getNode(numberString), getCloudVar());
       }

       if (result != getInverse()) {
           return EVAL_BODY;
       } else {
           return SKIP_BODY;
       }
   }

}
