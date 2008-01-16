package com.finalist.cmsc.services.community.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.*;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import com.finalist.cmsc.services.community.data.ModulePref;

/**
 * ModulePrefDAOImpl, this is a hibernate DAO/transaction class.
 * This class is the implementation of the DAO interface
 * it contains the methods for transactions to the database
 * 
 * @author menno menninga
 */
public class ModulePrefDAOImpl extends GenericDAO<ModulePref> implements ModulePrefDAO{

   
   public ModulePrefDAOImpl() {
      super(ModulePref.class);
   }

   @SuppressWarnings("unchecked")
   public Map<String, Map<String,List<String>>> getPreferences(String module, String userId, String key, String value) {
      Map<String, List<String>> valueMap = new HashMap<String, List<String>>();
      Map<String, Map<String,List<String>>> resultMap = new HashMap<String, Map<String,List<String>>>();
      List<String> valueList = new ArrayList<String>();
      DetachedCriteria criteria = DetachedCriteria.forClass(getPersistentClass());  
      if(userId != "" && key != ""){
         if(value == ""){  
            criteria.add(Restrictions.eq("userId", userId));
            criteria.add(Restrictions.eq("moduleKey", key));
            List<String> resultList = getHibernateTemplate().findByCriteria(criteria);
            Iterator it = resultList.iterator();
            valueMap = null;
            while(it.hasNext()){
               ModulePref modulePref = (ModulePref)it.next();
               if(valueMap != null && valueMap.containsKey(modulePref.getModuleKey())){
                  valueList = valueMap.get(modulePref.getModuleKey());
                  valueList.add(modulePref.getModuleValue());
               }
               else{
                  valueList.add(modulePref.getModuleValue());
                  valueMap.put(modulePref.getModuleKey(), valueList);
                  resultMap.put(userId, valueMap);
               }
            }
            return resultMap;
         }
         else if(value != ""){
            criteria.add(Restrictions.eq("moduleValue", value));
            List<String> resultList = getHibernateTemplate().findByCriteria(criteria);
            Iterator it = resultList.iterator();
            while(it.hasNext()){
               ModulePref modulePref = (ModulePref)it.next();
               if(valueMap != null && valueMap.containsKey(modulePref.getModuleKey())){
                  valueList = valueMap.get(modulePref.getModuleKey());
                  valueList.add(modulePref.getModuleValue());
               }
               else{
                  valueList.add(modulePref.getModuleValue());
                  valueMap.put(modulePref.getModuleKey(), valueList);
                  resultMap.put(userId, valueMap);
               }
            }
         }
         return resultMap;
      }
      else if(userId != ""){ 
         criteria.add(Restrictions.eq("userId", userId));
         List<String> resultList = getHibernateTemplate().findByCriteria(criteria);
         Iterator it = resultList.iterator();
         while(it.hasNext()){
            ModulePref modulePref = (ModulePref)it.next();
            if(valueMap != null && valueMap.containsKey(modulePref.getModuleKey())){
               valueList = valueMap.get(modulePref.getModuleKey());
               valueList.add(modulePref.getModuleValue());
            }
            else{
               String tempKey = modulePref.getModuleKey();
               String tempValue = modulePref.getModuleValue();
               valueList.add(modulePref.getModuleValue());
               valueMap.put(modulePref.getModuleKey(), valueList);
               resultMap.put(userId, valueMap);
            }
         }
         return resultMap;
      }
      else if(key != ""){
         criteria.add(Restrictions.eq("moduleKey", key));
         List<String> resultList = getHibernateTemplate().findByCriteria(criteria);
         Iterator it = resultList.iterator();
         while(it.hasNext()){
            ModulePref modulePref = (ModulePref)it.next();
            if(valueMap != null && valueMap.containsKey(modulePref.getUserId())){
               valueList = valueMap.get(modulePref.getUserId());
               valueList.add(modulePref.getModuleValue());
            }
            else{
               valueList.add(modulePref.getModuleValue());
               valueMap.put(modulePref.getUserId(), valueList);
               resultMap.put(key, valueMap);
            }
         }
         return resultMap;
      }
      return (null);
   }
}
