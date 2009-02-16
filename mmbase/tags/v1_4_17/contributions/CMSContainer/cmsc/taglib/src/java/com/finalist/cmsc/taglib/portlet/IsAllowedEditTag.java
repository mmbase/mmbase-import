package com.finalist.cmsc.taglib.portlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.tagext.TagSupport;

import org.mmbase.bridge.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import com.finalist.cmsc.repository.RepositoryUtil;
import com.finalist.cmsc.security.Role;
import com.finalist.cmsc.security.UserRole;

import net.sf.mmapps.commons.bridge.CloudUtil;

@SuppressWarnings("serial")
public class IsAllowedEditTag extends TagSupport {

   private static Logger log = Logging.getLoggerInstance(IsAllowedEditTag.class.getName());

   private int channelNumber;
   private boolean inverse;


   public void setChannelNumber(int channelNumber) {
      this.channelNumber = channelNumber;
   }


   public void setInverse(boolean inverse) {
      this.inverse = inverse;
   }


   @Override
   public int doStartTag() {
      UserRole role = null;

      Cloud cloud = CloudUtil.getCloudFromSession((HttpServletRequest) pageContext.getRequest());
      if (cloud != null) {
         role = RepositoryUtil.getRole(cloud, channelNumber);
         log.debug(role.getRole().getName());
      }

      if (role != null && role.getRole() != Role.NONE) {
         return inverse ? SKIP_BODY : EVAL_BODY_INCLUDE;
      }
      else {
         return inverse ? EVAL_BODY_INCLUDE : SKIP_BODY;
      }
   }

}
