/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package com.finalist.cmsc.taglib;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import net.sf.mmapps.commons.bridge.NodeFieldComparator;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mmbase.bridge.Node;

public class SortTag extends SimpleTagSupport {

   static Log log = LogFactory.getLog(SortTag.class);

   private String var;

   private String orderby;

   private String direction;


   @Override
   public void doTag() {
      PageContext ctx = (PageContext) getJspContext();
      HttpServletRequest request = (HttpServletRequest) ctx.getRequest();

      Object o = request.getAttribute(var);
      if (o != null && o instanceof List) {
         List list = (List) o;
         if (!list.isEmpty()) {
            Object first = list.get(0);
            boolean ascending = !"DOWN".equalsIgnoreCase(direction);
            Comparator comparator;
            if (first instanceof Node) {
               comparator = new NodeFieldComparator(orderby, ascending);
            }
            else {
               comparator = new PropertyComparator(orderby, ascending);
            }
            Collections.sort(list, comparator);
         }
      }
   }


   public void setVar(String var) {
      this.var = var;
   }


   public String getVar() {
      return var;
   }


   public String getOrderby() {
      return orderby;
   }


   public void setOrderby(String orderby) {
      this.orderby = orderby;
   }


   public String getDirection() {
      return direction;
   }


   public void setDirection(String direction) {
      this.direction = direction;
   }

   class PropertyComparator implements Comparator<Object> {

      private String propertyName;
      protected boolean ascending = true;


      public PropertyComparator(String property, boolean ascending) {
         this.propertyName = property;
         this.ascending = ascending;
      }


      public int compare(Object o1, Object o2) {
         if (!PropertyUtils.isReadable(o1, propertyName)) {
            throw new IllegalArgumentException("Property " + propertyName + " is not found in comparable object " + o1);
         }
         if (!PropertyUtils.isReadable(o2, propertyName)) {
            throw new IllegalArgumentException("Property " + propertyName + " is not found in comparable object " + o2);
         }
         int result = -1;
         try {
            Object value1 = PropertyUtils.getProperty(o1, propertyName);
            Object value2 = PropertyUtils.getProperty(o2, propertyName);
            if (value1 != null && value2 != null) {
               if (value1 instanceof String && value2 instanceof String) {
                  result = ((String) value1).compareToIgnoreCase((String) value2);
               }
               else if (value1 instanceof Comparable && value2 instanceof Comparable) {
                  result = ((Comparable) value1).compareTo(value2);
               }
            }
         }
         catch (IllegalAccessException e) {
            log.warn("" + e.getMessage(), e);
         }
         catch (InvocationTargetException e) {
            log.warn("" + e.getMessage(), e);
         }
         catch (NoSuchMethodException e) {
            log.warn("" + e.getMessage(), e);
         }

         if (ascending) {
            return result;
         }
         else {
            return -result;
         }
      }
   }

}
