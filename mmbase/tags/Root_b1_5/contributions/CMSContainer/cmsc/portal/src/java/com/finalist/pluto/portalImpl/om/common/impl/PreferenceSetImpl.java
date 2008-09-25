/*

 This software is OSI Certified Open Source Software.
 OSI Certified is a certification mark of the Open Source Initiative.

 The license (Mozilla version 1.0) can be read at the MMBase site.
 See http://www.MMBase.org/license

 */
package com.finalist.pluto.portalImpl.om.common.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import javax.portlet.PreferencesValidator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pluto.om.common.Preference;
import org.apache.pluto.om.common.PreferenceSet;
import org.apache.pluto.om.common.PreferenceSetCtrl;
import org.apache.pluto.util.StringUtils;

/**
 * Set preferences for portlets
 */
public class PreferenceSetImpl extends HashSet<Preference> implements PreferenceSet, PreferenceSetCtrl, Serializable {
   private static Log log = LogFactory.getLog(PreferenceSetImpl.class);


   public Preference get(String name) {
      Iterator iterator = this.iterator();
      while (iterator.hasNext()) {
         Preference preference = (Preference) iterator.next();
         if (preference.getName().equals(name)) {
            return preference;
         }
      }
      return null;
   }


   public PreferencesValidator getPreferencesValidator() {
      log.warn("Portlet class loader not yet available to load preferences validator.");
      return null;
   }


   public Preference add(String name, List values) {
      PreferenceImpl preference = new PreferenceImpl();
      preference.setName(name);
      preference.setValues(values);
      super.add(preference);
      return preference;
   }


   public Preference add(String name, Object value) {
      ArrayList<Object> values = new ArrayList<Object>();
      values.add(value);
      return this.add(name, values);
   }


   public Preference add(String name, int value) {
      return this.add(name, String.valueOf(value));
   }


   public Preference remove(String name) {
      Iterator iterator = this.iterator();
      while (iterator.hasNext()) {
         Preference preference = (Preference) iterator.next();
         if (preference.getName().equals(name)) {
            super.remove(preference);
            return preference;
         }
      }
      return null;
   }


   public void remove(Preference preference) {
      super.remove(preference);
   }


   public String toString() {
      return toString(0);
   }


   public String toString(int indent) {
      StringBuffer buffer = new StringBuffer(50);
      StringUtils.newLine(buffer, indent);
      buffer.append(getClass().toString());
      buffer.append(": ");
      Iterator iterator = this.iterator();
      while (iterator.hasNext()) {
         buffer.append(((PreferenceImpl) iterator.next()).toString(indent + 2));
      }
      return buffer.toString();
   }
}
