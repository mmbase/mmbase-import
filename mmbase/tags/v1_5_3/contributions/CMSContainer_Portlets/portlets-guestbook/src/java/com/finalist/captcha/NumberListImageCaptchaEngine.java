package com.finalist.captcha;

import java.awt.Color;

import com.octo.captcha.component.image.backgroundgenerator.BackgroundGenerator;
import com.octo.captcha.component.image.backgroundgenerator.GradientBackgroundGenerator;
import com.octo.captcha.component.image.fontgenerator.FontGenerator;
import com.octo.captcha.component.image.fontgenerator.TwistedAndShearedRandomFontGenerator;
import com.octo.captcha.component.image.textpaster.RandomTextPaster;
import com.octo.captcha.component.image.textpaster.TextPaster;
import com.octo.captcha.component.image.wordtoimage.ComposedWordToImage;
import com.octo.captcha.component.image.wordtoimage.WordToImage;
import com.octo.captcha.component.word.wordgenerator.RandomWordGenerator;
import com.octo.captcha.component.word.wordgenerator.WordGenerator;
import com.octo.captcha.engine.image.ListImageCaptchaEngine;
import com.octo.captcha.image.gimpy.GimpyFactory;

public class NumberListImageCaptchaEngine extends ListImageCaptchaEngine {

   protected void buildInitialFactories() {
      WordGenerator wordGenerator = new RandomWordGenerator("0123456789");
      TextPaster textPaster = new RandomTextPaster(Integer.valueOf(5), Integer.valueOf(6), Color.BLACK);
      BackgroundGenerator backgroundGenerator = new GradientBackgroundGenerator(Integer.valueOf(180), Integer.valueOf(40),
            Color.GRAY, Color.WHITE);
      FontGenerator fontGenerator = new TwistedAndShearedRandomFontGenerator(Integer.valueOf(16), Integer.valueOf(22));
      WordToImage wordToImage = new ComposedWordToImage(fontGenerator, backgroundGenerator, textPaster);
      this.addFactory(new GimpyFactory(wordGenerator, wordToImage));
   }
}
