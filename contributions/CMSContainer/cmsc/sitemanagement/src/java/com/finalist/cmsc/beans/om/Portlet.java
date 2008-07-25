package com.finalist.cmsc.beans.om;

import java.util.ArrayList;
import java.util.List;

import com.finalist.cmsc.beans.NodeBean;

/**
 * @author Wouter Heijke
 */
@SuppressWarnings("serial")
public class Portlet extends NodeBean implements Comparable<Portlet> {

   private String title;
   private int definition;
   private int expirationcache = -1; // when field is not present or virtual then this is the default value

   private List<Object> portletparameters = new ArrayList<Object>();
   private List<Integer> views = new ArrayList<Integer>();


   public int getDefinition() {
      return definition;
   }


   public void setDefinition(int definition) {
      this.definition = definition;
   }


   public String getTitle() {
      return title;
   }


   public void setTitle(String title) {
      this.title = title;
   }


   public int getView() {
      if (views.isEmpty()) {
         return -1;
      }
      return views.get(0);
   }


   public void addView(int view) {
      this.views.add(Integer.valueOf(view));
   }


   public List<Object> getPortletparameters() {
      return portletparameters;
   }


   public void addPortletparameter(PortletParameter parameter) {
      this.portletparameters.add(parameter);
   }


   public void addPortletparameter(NodeParameter parameter) {
      this.portletparameters.add(parameter);
   }


   public String getParameterValue(String key) {
      for (Object param : portletparameters) {
         if (param instanceof NodeParameter) {
            NodeParameter nodeparam = (NodeParameter) param;
            if (key.equals(nodeparam.getKey())) {
               return nodeparam.getValueAsString();
            }
         }
         if (param instanceof PortletParameter) {
            PortletParameter portletparam = (PortletParameter) param;
            if (key.equals(portletparam.getKey())) {
               return portletparam.getValue();
            }
         }
      }
      return null;
   }


   public int compareTo(Portlet o) {
      return title.compareTo(o.title);
   }



   public int getExpirationcache() {
      return expirationcache;
   }



   public void setExpirationcache(int expirationcache) {
      this.expirationcache = expirationcache;
   }
}
