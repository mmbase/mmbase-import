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

import java.io.*;
import java.util.*;

/**
 * Document structure configurable using a property file. A property key is a
 * tag name, when it starts with a <tt>_</tt> character a includable set,
 * <tt>&#64;ROOT</tt> when it denotes the document root and <tt>&amp;</tt>
 * for a list of all known entities. Property values give a list of tags which
 * can be parents of the given key, when a value starts with <tt>$</tt> it
 * denotes a attribute name and a value starting with <tt>_</tt> references an
 * other property.
 * <p>
 * The following example has a <tt>table</tt> element as possible root tag and
 * a structure similar to tables in html:
 * 
 * <pre>
 * &#064;ROOT: table
 * table: tr $width $height
 * tr: td th
 * td: _cell
 * th: _cell
 * _cell: #TEXT table $colspan $rowspan 
 * &amp;: nbsp
 * </pre>
 * 
 * @author R.W. van 't Veer
 */
public class PropertiesDocumentStructure implements DocumentStructure {
   /** set to keep tag names */
   private Set<String> tagNames = new HashSet<String>();
   /** set to keep entity names */
   private Set<String> entityNames = new HashSet<String>();
   /** map to keep tag attributes */
   private Map<String, List<String>> tagAttributes = new HashMap<String, List<String>>();
   /** map to keep lists of possible tag parents */
   private Map<String, List<String>> tagHierarchy = new HashMap<String, List<String>>();
   /** ignore case flag */
   private boolean icase = false;


   /**
    * @param prop
    *           properties map describing possible parent tags and attributes
    */
   public PropertiesDocumentStructure(Properties prop) {
      setup(prop);
   }


   /**
    * @param resource
    *           location of the properties file loadable as resource
    * @throws IOException
    *            when resource loading fails
    * @throws NullPointerException
    *            when resource does not exist
    */
   public PropertiesDocumentStructure(String resource) throws IOException {
      Properties prop = new Properties();
      prop.load(ClassLoader.getSystemResourceAsStream(resource));
      setup(prop);
   }


   /**
    * Initialize syntax maps from properties.
    * 
    * @param prop
    *           properties to initialize from
    */
   private void setup(Properties prop) {
      // collect tag names
      {
         Set<Object> keys = prop.keySet();
         for (Iterator<Object> iter = keys.iterator(); iter.hasNext();) {
            String key = (String) iter.next();
            if (!key.startsWith("_")) {
               tagNames.add(key);
            }
         }
      }

      // create "master" map by resolving all includes
      Map<String, List<String>> master = new HashMap<String, List<String>>();
      for (Iterator<String> it = tagNames.iterator(); it.hasNext();) {
         String key = it.next();
         master.put(key, include(prop, key));
      }

      // collect entity names
      {
         entityNames.add("amp");
         entityNames.add("gt");
         entityNames.add("lt");

         List<String> l = master.get("&");
         if (l != null) {
            entityNames.addAll(l);
            master.remove("&");
            tagNames.remove("&");
         }
      }

      // create hierarchy map from master
      for (Iterator<String> it = tagNames.iterator(); it.hasNext();) {
         String key = it.next();
         List<String> l = new ArrayList<String>(master.get(key));
         // remove attribute info
         for (Iterator<String> it0 = l.iterator(); it0.hasNext();) {
            String key0 = it0.next();
            if (key0.startsWith("$")) {
               it0.remove();
            }
         }
         tagHierarchy.put(key, l);
      }

      // create attribute map from master
      for (Iterator<String> it = tagNames.iterator(); it.hasNext();) {
         String key = it.next();
         List<String> in = new ArrayList<String>(master.get(key));
         List<String> l = new ArrayList<String>();
         // copy attribute only info to list
         for (Iterator<String> it0 = in.iterator(); it0.hasNext();) {
            String key0 = it0.next();
            if (key0.startsWith("$")) {
               l.add(key0.substring(1));
            }
         }
         tagAttributes.put(key, l);
      }
   }


   /**
    * @param prop
    *           properties to read from
    * @param key
    *           to read
    * @return fully dereferenced list
    */
   private List<String> include(Properties prop, String key) {
      List<String> l = new ArrayList<String>();
      StringTokenizer st = new StringTokenizer(prop.getProperty(key));
      while (st.hasMoreTokens()) {
         String v = st.nextToken();
         if (v.startsWith("_")) {
            l.addAll(include(prop, v));
         }
         else {
            l.add(v);
         }
      }
      return l;
   }


   /**
    * Set ignore case flag for matching tagnames, attributes and entities.
    * 
    * @param icase
    *           true where character case should be ignored
    */
   public void setIgnoreCase(boolean icase) {
      this.icase = icase;
   }


   /**
    * Get ignore case flag.
    * 
    * @return true where character case should be ignored
    */
   public boolean getIgnoreCase() {
      return icase;
   }


   /**
    * Get tag name. Ignoring character case if needed. Add icase-tag-name-set
    * for speed.
    * 
    * @param name
    *           tag name to lookup
    * @return tag name in proper case
    */
   public String getTagName(String name) {
      if (!icase) {
         return name;
      }

      String in = name.toLowerCase();
      for (Iterator<String> it = tagNames.iterator(); it.hasNext();) {
         String n = it.next();
         if (n.toLowerCase().equals(in)) {
            return n;
         }
      }
      return name;
   }


   /**
    * Get entity reference. Ignoring character case if needed.
    * 
    * @param name
    *           entity reference name
    * @return entity reference name in proper case or <tt>null</tt> if no such
    *         entity exists
    */
   public String getEntityRef(String name) {
      if (!icase) {
         return entityNames.contains(name) ? name : null;
      }

      // try exact match first
      if (entityNames.contains(name)) {
         return name;
      }

      // find a lower case match
      String in = name.toLowerCase();
      for (Iterator<String> it = entityNames.iterator(); it.hasNext();) {
         String n = it.next();
         if (n.toLowerCase().equals(in)) {
            return n;
         }
      }
      return null;
   }


   /**
    * Get attribute name. Ignoring character case if needed. Add
    * icase-attribute-name-set for speed.
    * 
    * @param name
    *           tag name to lookup
    * @param attr
    *           attribute name to lookup
    * @return attribute name in proper case or the value of <tt>attr</tt> when
    *         attribute is not known
    */
   public String getTagAttribute(String name, String attr) {
      if (!icase) {
         return attr;
      }
      List<String> names = tagAttributes.get(name);
      if (names == null) {
         return attr;
      }
      String in = attr.toLowerCase();
      for (Iterator<String> it = names.iterator(); it.hasNext();) {
         String n = it.next();
         if (n.toLowerCase().equals(in)) {
            return n;
         }
      }
      return attr;
   }


   /**
    * @param tag
    *           a tag token
    * @return true if tag is known
    */
   public boolean isKnownTag(TagToken tag) {
      return tagNames.contains(tag.getName());
   }


   /**
    * @param tag
    *           retain known attributes in this tag
    */
   public void retainKnownAttributes(TagToken tag) {
      List<String> names = tagAttributes.get(tag.getName());
      Iterator<Map.Entry<String, String>> it = tag.getAttributes().entrySet().iterator();
      while (it.hasNext()) {
         Map.Entry<String, String> en = it.next();
         if (!names.contains(en.getKey())) {
            it.remove();
         }
      }
   }


   /**
    * @param parent
    *           top tag
    * @param child
    *           possible child token
    * @return true if parent can contain child
    */
   public boolean canContain(TagToken parent, Token child) {
      if (child instanceof TextToken) {
         return canContainText(parent);
      }
      else {
         if (child instanceof TagToken) {
            TagToken childtag = (TagToken) child;
            return canContainTag(parent, childtag.getName());
         }
      }
      return false;
   }


   private String getTagName(TagToken parent) {
      return parent == null ? "@ROOT" : parent.getName();
   }


   public boolean canContainTag(TagToken tag, String name) {
      String parentName = getTagName(tag);
      List<String> hier = tagHierarchy.get(parentName);
      return hier.contains(name);
   }


   public boolean canContainText(TagToken tag) {
      String parentName = getTagName(tag);
      List<String> hier = tagHierarchy.get(parentName);
      return hier.contains("#TEXT");
   }


   /**
    * @return debug info
    */
   @Override
   public String toString() {
      return "names=" + tagNames + "\n" + "attributes=" + tagAttributes + "\n" + "hierarchy=" + tagHierarchy;
   }
}
