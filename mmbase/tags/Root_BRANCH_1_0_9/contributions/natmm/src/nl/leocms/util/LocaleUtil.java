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

import java.text.DateFormatSymbols;
import java.util.Locale;

import org.mmbase.bridge.*;
import org.mmbase.util.logging.Logging;
import org.mmbase.util.logging.Logger;

/**
 * @author Jeoffrey Bakker
 * @version $Revision: 1.3 $, $Date: 2006-08-11 09:24:16 $
 */
public class LocaleUtil {

   private static final Logger log = Logging.getLoggerInstance(LocaleUtil.class);

   public static DateFormatSymbols getDateFormatSymbols(Locale locale) {
      DateFormatSymbols dateFormatSymbols = null;
      if (locale.getLanguage().equals("eng")) {
         dateFormatSymbols = new DateFormatSymbols(Locale.ENGLISH);
      }
      else if (locale.getLanguage().equals("fra")) {
         String months[] = new String[]{"jannewaris", "febrewaris", "maart", "april", "maaie", "juni", "juli", "augustus", "septimber", "oktober", "novimber", "desimber"};
         dateFormatSymbols = new DateFormatSymbols(new Locale("nl", "NL"));
         dateFormatSymbols.setMonths(months);
      }
      else {
         dateFormatSymbols = new DateFormatSymbols(locale);
      }
      return dateFormatSymbols;
   }

    public static String getLangFieldName(String field, String language) {
       String fieldname = field;
       if (!"nl".equals(language)) {
          fieldname += "_" + language;
       }
       return fieldname;
    }
    
    public static String getField(Node node, String field, String language) {
       return getField(node, field, language, "");
    }
    
    public static String getField(Node node, String field, String language, String postString) {
       String value = node.getStringValue(getLangFieldName(field,language));
       if(!value.equals("")) {
        value += postString;
       }
       return value;
    }
   
}

/**
 * $Log: not supported by cvs2svn $
 * Revision 1.2  2006/03/08 22:23:51  henk
 * Changed log4j into MMBase logging
 *
 * Revision 1.1  2006/03/05 21:43:59  henk
 * First version of the NatMM contribution.
 *
 * Revision 1.1  2003/12/15 15:16:30  jeoffrey
 * fix for jira bug LEEUW-194
 *
 */