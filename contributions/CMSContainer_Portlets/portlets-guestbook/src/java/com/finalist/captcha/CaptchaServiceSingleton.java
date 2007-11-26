package com.finalist.captcha;

import com.octo.captcha.service.captchastore.FastHashMapCaptchaStore;
import com.octo.captcha.service.image.DefaultManageableImageCaptchaService;
import com.octo.captcha.service.image.ImageCaptchaService;

public class CaptchaServiceSingleton {

   private static ImageCaptchaService instance = initializeService();


   public static ImageCaptchaService getInstance() {
      return instance;
   }


   private static ImageCaptchaService initializeService() {
      NumberListImageCaptchaEngine engine = new NumberListImageCaptchaEngine();
      return new DefaultManageableImageCaptchaService(new FastHashMapCaptchaStore(), engine, 180, 100000, 75000);
   }
}