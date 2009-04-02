package com.finalist.cmsc.community.forms;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class ParameterMapper {

   private static Log log = LogFactory.getLog(ParameterMapper.class);

   private Object source;
   private Map < String , String > parameters = new HashMap < String , String > ();

   public void setSource(Object source) {
      this.source = source;
   }

   public static ParameterMapper wrap(Object obj) {
      ParameterMapper mapper = new ParameterMapper();
      mapper.setSource(obj);
      return mapper;
   }

   public ParameterMapper map(String name, String destination) {
      try {

         String value = (String) PropertyUtils.getProperty(source, destination);
         if (StringUtils.isNotBlank(value)) {
            parameters.put(name, value);
         }

      } catch (IllegalAccessException e) {
         log.debug(e);
      } catch (InvocationTargetException e) {
         log.debug(e);
      } catch (NoSuchMethodException e) {
         log.debug(e);
      }

      return this;
   }

   public Map < String , String > getMap() {
      return parameters;
   }
}
