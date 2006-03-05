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

import org.apache.log4j.Category;

import java.text.DateFormatSymbols;
import java.util.Locale;

/**
 * @author Jeoffrey Bakker
 * @version $Revision: 1.1 $, $Date: 2006-03-05 21:43:59 $
 */
public class LocaleUtil {
   static Category log = Category.getInstance(LocaleUtil.class);


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

}

/**
 * $Log: not supported by cvs2svn $
 * Revision 1.1  2003/12/15 15:16:30  jeoffrey
 * fix for jira bug LEEUW-194
 *
 */