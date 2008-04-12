package com.finalist.cmsc.directreaction.taglib;

import java.util.List;

import javax.servlet.jsp.tagext.SimpleTagSupport;

import com.finalist.cmsc.directreaction.util.*;

/**
 * Warning this tag is untested
 * 
 * @author freek
 */
public class GetTag extends SimpleTagSupport {

   private int number;
   private String var;


   public void doTag() {
      List<Reaction> reactions = ReactionUtil.getReactions(number);
      getJspContext().setAttribute(var, reactions);
   }


   public void setNumber(int number) {
      this.number = number;
   }


   public void setVar(String var) {
      this.var = var;
   }

}
