package org.jahia.portlet.util;

import java.util.StringTokenizer;

/**
 * Util methods in order to manipulate String
 *
 * @author Khaled TLILI
 */
public class StringUtil {
   private static final String ENCODE_TOKEN_SEPARATOR = "x";


   /**
    * Replace special graph like "&lt;", "&gt;", ... by "<",">",...
    *
    * @param strg Description of Parameter
    * @return Description of the Returned Value
    */
   public static String replaceSpecialGraphics(String strg) {
      if (strg == null) {
         return null;
      }
      Object[][] specialGraphic = {
               {"&lt;", "<"},
               {"&gt;", ">"},
               {"&amp;", "&"},
               {"&quot;", "\""},
               {"nbsp;", ""},
               {"&reg;", ""},
               {"copy;", ""},
               {"&ensp;", ""},
               {"&emsp", ""}
      };

      for (int i = 0; i < specialGraphic.length; i++) {
         strg = strg.replaceAll((String) specialGraphic[i][0], (String) specialGraphic[i][1]);
      }
      return strg;
   }


   /**
    * Return a str if not null else default Value
    *
    * @param strg         Description of Parameter
    * @param defaultValue Description of Parameter
    * @return Description of the Returned Value
    */
   public static String notNullValue(String strg, String defaultValue) {
      if (strg == null) {
         if (defaultValue != null) {
            return defaultValue;
         } else {
            return "";
         }
      } else {
         return strg;
      }
   }

   /**
    *  Encrypte String
    *
    *@param  strg  Description of Parameter
    *@return Description of the Returned Value
    */
   /*public static String encrypte(String strg) {
       byte[] bytes = strg.getBytes();
       StringBuffer enct = new StringBuffer();
       for (int i = 0; i < bytes.length; i++) {
          enct.append("" + bytes[i]);
          enct.append(ENCODE_TOKEN_SEPARATOR);
       }
       return enct.toString();
    }*/

   /**
    *  Decrypte string that has been enncrspted whith encrypte(...) method
    *
    *@param  encryptedStrg  Encrypted strg
    *@return Decrypted strg
    */
   /*public static String decrypte(String encryptedStrg) {

       StringTokenizer strtk = new StringTokenizer(encryptedStrg.toString(), ENCODE_TOKEN_SEPARATOR);
       byte bytess[] = new byte[strtk.countTokens()];
       int index = 0;
       while (strtk.hasMoreElements()) {
          String ss = (String) strtk.nextToken();
          bytess[index] = Byte.parseByte(ss);
          index++;
       }
       String dec = new String(bytess);
       return dec;
    }*/

}
