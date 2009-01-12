package com.finalist.captcha;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.finalist.cmsc.mmbase.PropertiesUtil;
import com.octo.captcha.engine.image.ListImageCaptchaEngine;
import com.octo.captcha.service.captchastore.FastHashMapCaptchaStore;
import com.octo.captcha.service.image.DefaultManageableImageCaptchaService;
import com.octo.captcha.service.image.ImageCaptchaService;

public class CaptchaServiceSingleton {

   private static Log log = LogFactory.getLog(CaptchaServiceSingleton.class);	
   private static ImageCaptchaService instance = initializeService();


   public static ImageCaptchaService getInstance() {
      return instance;
   }


   private static ImageCaptchaService initializeService() {
	  String engineClass = PropertiesUtil.getProperty("guestbook.captcha.engineClass");
	  ListImageCaptchaEngine engine = null; 
      
      if(engineClass == null || engineClass.length() == 0) {
    	  engine = new NumberListImageCaptchaEngine();
      }
      else {
    	  try {
			engine = (ListImageCaptchaEngine) Class.forName(engineClass).newInstance();
		  } catch (Exception e) {
	        log.error("Could not instantiate engineClass for guestbook (see admin/properties)", e);
		  }
      }
      return new DefaultManageableImageCaptchaService(new FastHashMapCaptchaStore(), engine, 180, 100000, 75000);
   }
}