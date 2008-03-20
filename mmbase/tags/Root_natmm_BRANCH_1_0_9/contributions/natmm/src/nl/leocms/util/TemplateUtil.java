/*
 * Version: MPL 1.1/GPL 2.0/LGPL 2.1
 *
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Original Code is LeoCMS.
 *
 * The Initial Developer of the Original Code is
 * 'De Gemeente Leeuwarden' (The dutch municipality Leeuwarden).
 *
 * See license.txt in the root of the LeoCMS directory for the full license.
 */
package nl.leocms.util;

import java.util.StringTokenizer;

import org.apache.commons.lang.StringUtils;

/**
 * @author Gerard van de Weerd
 * Nov 5, 2003
 * 
 * This class contains utilities for templates.
 * 
 */
public class TemplateUtil {
   
   /**
    * Creates a link sql-statement for the given allowed content types. E.g.
    * allowedContentTypes = "artikel, evenement"
    * likeField = "type" 
    * -> result ->
    * type LIKE "%artikel%" OR type LIKE "%evenement%"
    *    
    * @param allowedContentTypes Allowed content types comma separated (spaces are ignored and the
    * characters are set to lowercase).
    * @param likeField the like field in the sql statement 
    * @return
    * @throws IllegalArgumentException one of the allowed content types is not supported 
    */
   public static String createAllowedContentTypesLikeSqlStatement(String allowedContentTypes, String likeField) throws IllegalArgumentException {
      StringTokenizer tokenizer = new StringTokenizer(allowedContentTypes, ",");
      String currentType;
      StringBuffer err = new StringBuffer("The following content types in " + allowedContentTypes + " are invalid. ");
      StringBuffer linkStatement = new StringBuffer("");
      boolean error = false;
      boolean firstLike = true;
      while (tokenizer.hasMoreElements()) {
         currentType = StringUtils.strip(tokenizer.nextToken()).toLowerCase();
         //System.out.println("testing: " + currentType);
         if (!ContentTypeHelper.isContentType(currentType)) {
            error = true;
            err.append("'" + currentType + "' ");
         }
         else {
            if (!firstLike) {
               linkStatement.append(" OR ");
            }
            else {
               firstLike = !firstLike;
            }
            linkStatement.append(likeField + " LIKE " + "'%" + currentType + "%'");
         }
      }      
      if (error) {
         throw new IllegalArgumentException(err.toString());
      }
      return linkStatement.toString();
   }
}
