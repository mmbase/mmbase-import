/*
 * WIAB - Web-in-a-Box
 *
 * The contents of this file are subject to the Mozilla Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * The Original Code is Web-In-A-Box.
 */
package com.finalist.cmsc.richtext;

/**
 * Class for storing constants for richtext handling classes.
 */
public class RichText {

   public final static String RICHTEXT_ROOT_OPEN =
      "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
      "<richtext>";

   public final static String RICHTEXT_ROOT_CLOSE = "</richtext>";

   public final static String LINK_TAGNAME = "a";
   public final static String IMG_TAGNAME = "img";
   
   public final static String DESTINATION_ATTR = "destination";
   public final static String RELATIONID_ATTR = "relationID";

   public final static String SRC_ATTR = "src";
   public final static String HREF_ATTR = "href";

   public final static String SIZE_ATTR = "imgsize";
   public final static String LEGEND = "legend";

   public static final String TITLE_ATTR = "title";
   public static final String DESCRIPTION_ATTR = "description";
   public static final String ALT_ATTR = "alt";
   public static final String WIDTH_ATTR = "width";
   public static final String HEIGHT_ATTR = "height";

   // mmbase stuff
   public final static String RICHTEXT_TYPE = "cmscrichtext";
   public final static String INLINEREL_NM = "inlinerel";
   public final static String IMAGEINLINEREL_NM = "imageinlinerel";
   public static final String REFERID_FIELD = "referid";

}
