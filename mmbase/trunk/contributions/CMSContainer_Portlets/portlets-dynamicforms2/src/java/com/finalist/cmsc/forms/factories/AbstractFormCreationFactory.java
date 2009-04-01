/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package com.finalist.cmsc.forms.factories;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.digester.AbstractObjectCreationFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.Attributes;

public abstract class AbstractFormCreationFactory extends AbstractObjectCreationFactory {
   
   private static final Log log = LogFactory.getLog(AbstractFormCreationFactory.class);
   
   String name;
   
   AbstractFormCreationFactory(String name) {
      this.name = name;
   }
   
   @Override
   public Object createObject(Attributes attributes) throws Exception {
      String clazz = attributes.getValue("class");
      return createObject(name, clazz);
   }

   protected Object createObject(String name, String clazz) {
      String fullclazz = null;
      InputStream in = this.getClass().getResourceAsStream(name + ".properties");
      if (in != null) {
         try {
            Properties mapping = new Properties();
            mapping.load(in);
            fullclazz = mapping.getProperty(clazz);
         }
         catch (IOException e) {
            log.error("" + e.getMessage(), e);
         }
      }
      if (fullclazz == null) {
         fullclazz = clazz;
      }
      
      if (fullclazz != null && fullclazz.length() > 0) {
         try {
            Class<?> fullClass = Class.forName(fullclazz);
            Object o = fullClass.newInstance();
            return o;
         }
         catch (ClassNotFoundException e) {
            log.error("" + e.getMessage(), e);
         }
         catch (InstantiationException e) {
            log.error("" + e.getMessage(), e);
         }
         catch (IllegalAccessException e) {
            log.error("" + e.getMessage(), e);
         }
      }
      throw new IllegalArgumentException("failed to create a " + name+ " with class " + clazz);
   }

}
