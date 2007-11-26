package com.finalist.cmsc.taglib.image;

import java.util.ArrayList;

import javax.servlet.jsp.tagext.SimpleTagSupport;

public class ImageTextTag extends SimpleTagSupport {

   public static final String TEXT_TAGS = "IMAGE_TEXT_TAGS";

   private String text;
   private int x;
   private int y;
   private String effect;
   private String font;
   private int pointSize;
   private String color;


   @Override
   public void doTag() {
      ArrayList<ImageTextTag> textTags = (ArrayList<ImageTextTag>) getJspContext().getAttribute(TEXT_TAGS);
      if (textTags == null) {
         textTags = new ArrayList<ImageTextTag>();
         getJspContext().setAttribute(TEXT_TAGS, textTags);
      }
      textTags.add(this);
   }


   public void addToTemplate(StringBuffer template, boolean asis) {

      // escape the text for imagemagick
      String text = this.text.replace("\'", "\'\'");

      if (font != null) {
         template.append("+font(mm:" + font + ")");
      }
      if (pointSize > 0) {
         template.append("+pointsize(" + pointSize + ")");
      }

      // the effects are a little bit ticky, but can be enhanced in the future
      if (effect != null) {
         if (effect.startsWith("outline")) {
            outline(template, text, x, y, effect.substring(effect.indexOf("(") + 1, effect.indexOf(")")));
         }
         else {
            template.append("+" + effect);
         }
      }
      if (color != null) {
         template.append("+fill(" + color + ")");
      }
      if (text != null && !text.equals("")) {
         template.append("+text(" + (x <= 0 ? 0 : x) + "," + (y <= 0 ? 0 : y) + ",'" + text + "')");
      }

      if (asis) {
         if (template.length() > 0) {
            template.append("+f(asis)");
         }
         else {
            template.append("f(asis)");
         }
      }
   }


   private void outline(StringBuffer template, String text, int x, int y, String outlineColor) {
      if (color != null) {
         template.append("+fill(" + outlineColor + ")");
      }

      if (text != null && !text.equals("")) {
         template.append("+text(" + (x - 1) + "," + (y) + ",'" + text + "')");
         template.append("+text(" + (x + 1) + "," + (y) + ",'" + text + "')");
         template.append("+text(" + (x) + "," + (y - 1) + ",'" + text + "')");
         template.append("+text(" + (x) + "," + (y + 1) + ",'" + text + "')");
      }
   }


   public String getColor() {
      return color;
   }


   public void setColor(String color) {
      this.color = color;
   }


   public String getEffect() {
      return effect;
   }


   public void setEffect(String effect) {
      this.effect = effect;
   }


   public String getFont() {
      return font;
   }


   public void setFont(String font) {
      this.font = font;
   }


   public int getPointSize() {
      return pointSize;
   }


   public void setPointSize(int pointSize) {
      this.pointSize = pointSize;
   }


   public String getText() {
      return text;
   }


   public void setText(String text) {
      this.text = text;
   }


   public int getX() {
      return x;
   }


   public void setX(int x) {
      this.x = x;
   }


   public int getY() {
      return y;
   }


   public void setY(int y) {
      this.y = y;
   }
}