/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package com.finalist.cmsc.forms;

import java.io.IOException;
import java.util.*;

import javax.portlet.PortletException;


public class Localization {

   private static ThreadLocalStack<Locale> locales = new ThreadLocalStack<Locale>();
   private static ThreadLocalStack<ResourceBundle> bundles = new ThreadLocalStack<ResourceBundle>();
   
   public static <T> T localize(Locale locale, ResourceBundle bundle, Action<T> action) throws PortletException, IOException {
      try {
         if (locale != null) {
            locales.push(locale);
         }
         if (bundle != null) {
            bundles.push(bundle);
         }
      
         return action.run();
      }
      finally {
         if (locale != null) {
            locales.pop();
         }
         if (bundle != null) {
            bundles.pop();
         }
      }
   }

   public static Locale getLocale() {
      return locales.peek();
   }
   
   public static ResourceBundle getResourceBundle() {
      return bundles.peek();
   }
   
   
   public interface Action<T> {
      T run() throws PortletException, IOException;
   }
   
   static class ThreadLocalStack<T> {

      ThreadLocal<ArrayList<T>> local;

      ThreadLocalStack() {
         local = new ArrayListLocal<T>();
      }

      int size() {
         ArrayList<T> stack = local.get();
         return stack.size();
      }

      void push(T iten) {
         ArrayList<T> stack = local.get();
         stack.add(iten);
      }

      T pop() {
         ArrayList<T> stack = local.get();
         T context = null;
         int lastIndex = stack.size() - 1;
         if (lastIndex >= 0) context = stack.remove(lastIndex);
         return context;
      }

      T peek() {
         ArrayList<T> stack = local.get();
         T context = null;
         int lastIndex = stack.size() - 1;
         if (lastIndex >= 0) context = stack.get(lastIndex);
         return context;
      }

      void clear() {
         ArrayList<T> stack = local.get();
         stack.clear();
      }
   }
   
   static class ArrayListLocal<T> extends ThreadLocal<ArrayList<T>> {

      protected ArrayList<T> initialValue() {
         return new ArrayList<T>();
      }
   }
}
