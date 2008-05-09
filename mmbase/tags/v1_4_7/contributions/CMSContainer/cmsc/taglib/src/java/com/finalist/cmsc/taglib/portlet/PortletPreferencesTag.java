/*

 This software is OSI Certified Open Source Software.
 OSI Certified is a certification mark of the Open Source Initiative.

 The license (Mozilla version 1.0) can be read at the MMBase site.
 See http://www.MMBase.org/license

 */
package com.finalist.cmsc.taglib.portlet;

import java.io.IOException;
import java.util.Enumeration;

import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.apache.pluto.Constants;

/**
 * Tag to get a name/value from the PortletPreferences for the active portlet
 * 
 * @author Wouter Heijke
 * @version $Revision: 1.5 $
 */
public class PortletPreferencesTag extends SimpleTagSupport {

   private String name;
   private String value;
   private String var;


   @Override
   public void doTag() throws JspException, IOException {
      PageContext ctx = (PageContext) getJspContext();
      HttpServletRequest request = (HttpServletRequest) ctx.getRequest();
      PortletRequest renderRequest = (PortletRequest) request.getAttribute(Constants.PORTLET_REQUEST);

      if (renderRequest != null) {
         PortletPreferences preferences = renderRequest.getPreferences();

         if (name != null) {
            value = preferences.getValue(name, value);

            // handle result
            if (var != null) {
               // put in variable
               if (value != null) {
                  request.setAttribute(var, value);
               }
               else {
                  request.removeAttribute(var);
               }
            }
            else {
               // write
               ctx.getOut().print(String.valueOf(value));
            }
         }
         else {
            if (var != null) {
               // Set a map of all preferences to a variable with the name in
               // var
               request.setAttribute(var, preferences.getMap());
            }
            else {
               Enumeration<String> p = preferences.getNames();
               while (p.hasMoreElements()) {
                  String pref = p.nextElement();
                  String[] values = preferences.getValues(pref, null);
                  if (values != null) {
                     if (values.length > 1) {
                        request.setAttribute(pref, values);
                     }
                     else {
                        String value = values[0];
                        request.setAttribute(pref, value);
                     }
                  }
               }
            }
         }
      }
      else {
         throw new JspException("Couldn't find a Portlet");
      }
   }


   public void setName(String name) {
      this.name = name;
   }


   public void setValue(String value) {
      this.value = value;
   }


   public void setVar(String var) {
      this.var = var;
   }
}
