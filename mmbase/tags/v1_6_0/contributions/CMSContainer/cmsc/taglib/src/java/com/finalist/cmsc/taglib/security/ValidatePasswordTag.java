package com.finalist.cmsc.taglib.security;

import javax.servlet.jsp.PageContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mmbase.util.Encode;

import com.finalist.cmsc.taglib.CmscTag;

/**
 * Tag that compares a given password with the passwordfield. When no
 * passwordfield is given fieldname "password" is assumed.
 * 
 * @author Bas Piepers
 */
public class ValidatePasswordTag extends CmscTag {
   private static Log log = LogFactory.getLog(ValidatePasswordTag.class);

   protected String md5Password;
   protected String givenPassword;
   protected String result;


   @Override
   public void doTag() {
      PageContext ctx = (PageContext) getJspContext();
      Encode encoder = new org.mmbase.util.Encode("MD5");
      String encodedPassword = encoder.encode(getGivenPassword());
      log.info("The given password matches the md5 password: " + encodedPassword.equals(getMd5Password()));
      ctx.setAttribute(result, encodedPassword.equals(getMd5Password()));

   }


   public String getGivenPassword() {
      return givenPassword;
   }


   public void setGivenPassword(String givenPassword) {
      this.givenPassword = givenPassword;
   }


   public String getResult() {
      return result;
   }


   public void setResult(String result) {
      this.result = result;
   }


   public String getMd5Password() {
      return md5Password;
   }


   public void setMd5Password(String md5Password) {
      this.md5Password = md5Password;
   }
}