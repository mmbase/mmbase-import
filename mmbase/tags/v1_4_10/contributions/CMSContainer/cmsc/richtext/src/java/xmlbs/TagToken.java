/*
 * xmlbs
 *
 * Copyright (C) 2002  R.W. van 't Veer
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston,
 * MA 02111-1307, USA.
 */

package xmlbs;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Tag token. Represents an XML element.
 * 
 * @see <A href="http://www.w3.org/TR/REC-xml#sec-logical-struct">XML: Logical
 *      Structures</A>
 * @author R.W. van 't Veer
 */
public class TagToken implements Token {
   /** tag name */
   private String tagName;
   /** map of tag attributes */
   private Map<String, String> attrs = new HashMap<String, String>();
   /** document structure this tag lives in */
   private DocumentStructure ds = null;

   /** type of tag */
   private int type;
   /** open tag type constant */
   public static final int OPEN = 0;
   /** close tag type constant */
   public static final int CLOSE = 1;
   /** empty tag type constant */
   public static final int EMPTY = 2;

   /** regular expression to match close tags */
   private static Pattern closeRe;
   /** regular expression to match empty tags */
   private static Pattern emptyRe;
   /** regular expression to match tag name */
   private static Pattern nameRe;
   /** regular expression to match attribute name */
   private static Pattern attRe;
   /** regular expression to match single-quoted attribute value */
   private static Pattern valRe1;
   /** regular expression to match double-quoted attribute value */
   private static Pattern valRe2;
   /** regular expression to match unquoted attribute value */
   private static Pattern valRe3;

   static {
      closeRe = Pattern.compile("^\\s*/");
      emptyRe = Pattern.compile("/\\s*$");
      nameRe = Pattern.compile("([a-zA-Z_:][a-zA-Z0-9._:-]*)\\s*");
      attRe = Pattern.compile("\\s([a-zA-Z_:][a-zA-Z0-9]*)\\s*=");
      valRe1 = Pattern.compile("^\\s*=\\s*'([^']*)'");
      valRe2 = Pattern.compile("^\\s*=\\s*\"([^\"]*)\"");
      valRe3 = Pattern.compile("^\\s*=\\s*([^\\s]*)");
   }


   /**
    * @param raw
    *           tag text without &lt; and &gt;
    * @param ds
    *           Document Structure
    */
   public TagToken(String raw, DocumentStructure ds) {
      this.ds = ds;

      // determine tag type
      {
         if (closeRe.matcher(raw).find()) {
            type = CLOSE;
         }
         else if (emptyRe.matcher(raw).find()) {
            type = EMPTY;
         }
         else {
            type = OPEN;
         }
      }

      // determine tag name
      {
         Matcher nameMatcher = nameRe.matcher(raw);
         if (nameMatcher.find()) {
            tagName = nameMatcher.group(1);
         }
         else {
            tagName = "";
         }
         if (ds.getIgnoreCase()) {
            String t = ds.getTagName(tagName);
            tagName = t == null ? tagName : t;
         }
      }

      // collect attributes
      {
         int pos = 0;
         Matcher attMatcher = attRe.matcher(raw);

         while (attMatcher.find(pos)) {
            String attr = attMatcher.group(1);
            pos = attMatcher.end(1);
            String valStr = raw.substring(pos);

            String val = null;
            Matcher valMatcher = null;

            // Pattern valRe = null;
            Matcher val1Matcher = valRe1.matcher(valStr);
            if (val1Matcher.find()) {
               valMatcher = val1Matcher;
            }
            else {
               Matcher val2Matcher = valRe2.matcher(valStr);
               if (val2Matcher.find()) {
                  valMatcher = val2Matcher;
               }
               else {
                  Matcher val3Matcher = valRe3.matcher(valStr);
                  if (val3Matcher.find()) {
                     valMatcher = val3Matcher;
                  }
                  else {
                     break;
                  }
               }
            }
            val = valMatcher.group(1); // TODO handle entity and char refs
            pos += valMatcher.end(1);

            if (ds.getIgnoreCase()) {
               String t = ds.getTagAttribute(tagName, attr);
               attr = t == null ? attr : t;
            }
            attrs.put(attr, val);
         }
      }
   }


   /**
    * @param tagName
    *           tag name
    * @param attrs
    *           map of attributes
    * @param type
    *           type of tag
    * @see #OPEN
    * @see #CLOSE
    * @see #EMPTY
    */
   public TagToken(String tagName, Map<String, String> attrs, int type) {
      this.tagName = tagName;
      this.attrs = attrs;
      this.type = type;
   }


   /**
    * @return empty version of this tag
    */
   public TagToken emptyTag() {
      TagToken tok = new TagToken(tagName, attrs, EMPTY);
      return tok;
   }


   /**
    * @return closing version of this tag
    */
   public TagToken closeTag() {
      TagToken tok = new TagToken(tagName, attrs, CLOSE);
      return tok;
   }


   /**
    * @return tag name
    */
   public String getName() {
      return tagName;
   }


   /**
    * @return map of tag attributes
    */
   public Map<String, String> getAttributes() {
      return attrs;
   }


   /**
    * @return true if this is a open tag
    * @see #OPEN
    */
   public boolean isOpenTag() {
      return type == OPEN;
   }


   /**
    * @return true if this is a close tag
    * @see #CLOSE
    */
   public boolean isCloseTag() {
      return type == CLOSE;
   }


   /**
    * @return true if this is a empty tag
    * @see #EMPTY
    */
   public boolean isEmptyTag() {
      return type == EMPTY;
   }


   /**
    * @param tag
    *           token to compare to
    * @return true if name of given tag matches
    */
   public boolean isSameTag(TagToken tag) {
      return tagName.equals(tag.getName());
   }


   /**
    * @return proper string representation of this tag
    */
   @Override
   public String toString() {
      StringBuffer sb = new StringBuffer();

      sb.append('<');

      if (type == CLOSE) {
         sb.append('/');
         sb.append(tagName);
         sb.append('>');

         return sb.toString();
      }

      // else OPEN or EMPTY
      sb.append(tagName);

      if (attrs != null) {
         List<String> l = new ArrayList<String>(attrs.keySet());
         Collections.sort(l);
         Iterator<String> it = l.iterator();
         while (it.hasNext()) {
            String attr = it.next();
            String val = attrs.get(attr);

            sb.append(' ');
            sb.append(attr);
            sb.append('=');
            sb.append('"');
            sb.append(TextToken.fixText(val, ds));
            sb.append('"');
         }
      }

      if (type == EMPTY) {
         sb.append('/');
      }
      sb.append('>');

      return sb.toString();
   }
}
