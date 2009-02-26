/*

 This software is OSI Certified Open Source Software.
 OSI Certified is a certification mark of the Open Source Initiative.

 The license (Mozilla version 1.0) can be read at the MMBase site.
 See http://www.MMBase.org/license

 */
package com.finalist.pluto.portalImpl.om.common.impl;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Iterator;

import org.apache.pluto.om.common.Parameter;
import org.apache.pluto.om.common.ParameterSet;
import org.apache.pluto.om.common.ParameterSetCtrl;
import org.apache.pluto.util.StringUtils;

public class ParameterSetImpl extends HashSet implements ParameterSet, ParameterSetCtrl, Serializable {

   // ParameterSet implementation.
   public Parameter get(String name) {
      Iterator iterator = this.iterator();
      while (iterator.hasNext()) {
         Parameter parameter = (Parameter) iterator.next();
         if (parameter.getName().equals(name)) {
            return parameter;
         }
      }
      return null;
   }


   // ParameterSetCtrl implementation.
   public Parameter add(String name, String value) {
      ParameterImpl parameter = new ParameterImpl();
      parameter.setName(name);
      parameter.setValue(value);

      super.add(parameter);

      return parameter;
   }


   public Parameter remove(String name) {
      Iterator iterator = this.iterator();
      while (iterator.hasNext()) {
         Parameter parameter = (Parameter) iterator.next();
         if (parameter.getName().equals(name)) {
            super.remove(parameter);
            return parameter;
         }
      }
      return null;
   }


   public void remove(Parameter parameter) {
      super.remove(parameter);
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
         buffer.append(((ParameterImpl) iterator.next()).toString(indent + 2));
      }
      return buffer.toString();
   }

}
