/*
 * 
 * This software is OSI Certified Open Source Software. OSI Certified is a certification mark of the Open Source
 * Initiative.
 * 
 * The license (Mozilla version 1.0) can be read at the MMBase site. See http://www.MMBase.org/license
 * 
 */
package com.finalist.cmsc.services.community.preferences;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Transactional;

import com.finalist.cmsc.services.HibernateService;
import com.finalist.cmsc.services.community.domain.PreferenceVO;
import com.finalist.cmsc.services.community.security.Authentication;
import com.finalist.cmsc.services.community.security.AuthenticationService;

/**
 * @author Remco Bos
 */
public class PreferenceHibernateService extends HibernateService implements PreferenceService {

   private AuthenticationService authenticationService;

   /** {@inheritDoc} */
   @Transactional(readOnly = true)
   @SuppressWarnings("unchecked")
   public Map<Long, Map<String, String>> getPreferencesByModule(String module) {
      Criteria criteria = getSession().createCriteria(Preference.class);
      criteria.add(Restrictions.eq("module", module));
      List preferenceList = criteria.list();
      Map<Long, Map<String, String>> userPreferenceMap = new HashMap<Long, Map<String, String>>();
      for (Iterator iter = preferenceList.iterator(); iter.hasNext();) {
         Preference p = (Preference) iter.next();
         Map<String, String> preferenceMap = userPreferenceMap.get(p.getAuthenticationId());
         if (preferenceMap == null) {
            preferenceMap = new HashMap<String, String>();
            userPreferenceMap.put(p.getAuthenticationId(), preferenceMap);
         }
         preferenceMap.put(p.getKey(), p.getValue());
      }
      return userPreferenceMap;
   }

   /** {@inheritDoc} */
   @Transactional(readOnly = true)
   @SuppressWarnings("unchecked")
   public Map<String, Map<String, String>> getPreferencesByUserId(String userId) {
      Long authenticationId = authenticationService.getAuthenticationIdForUserId(userId);
      Criteria criteria = getSession().createCriteria(Preference.class);
      criteria.add(Restrictions.eq("authenticationId", authenticationId));
      List preferenceList = criteria.list();
      Map<String, Map<String, String>> modulePreferenceMap = new HashMap<String, Map<String, String>>();
      for (Iterator iter = preferenceList.iterator(); iter.hasNext();) {
         Preference p = (Preference) iter.next();
         Map<String, String> preferenceMap = modulePreferenceMap.get(p.getModule());
         if (preferenceMap == null) {
            preferenceMap = new HashMap<String, String>();
            modulePreferenceMap.put(p.getModule(), preferenceMap);
         }
         preferenceMap.put(p.getKey(), p.getValue());
      }
      return modulePreferenceMap;
   }

   /** {@inheritDoc} */
   @Transactional(readOnly = true)
   public List<Preference> getListPreferencesByUserId(String userId) {
      Long authenticationId = authenticationService.getAuthenticationIdForUserId(userId);
      Criteria criteria = getSession().createCriteria(Preference.class);
      criteria.add(Restrictions.eq("authenticationId", authenticationId));
      List List = criteria.list();
      List<Preference> preferenceList = new ArrayList<Preference>();
      for (Iterator iter = List.iterator(); iter.hasNext();) {
         Preference p = new Preference();
         p = (Preference) iter.next();
         preferenceList.add(p);
      }
      return preferenceList;
   }

   /** {@inheritDoc} */
   @Transactional(readOnly = true)
   public Map<String, String> getPreferences(String module, String userId) {
      Long authenticationId = authenticationService.getAuthenticationIdForUserId(userId);
      Criteria criteria = getSession().createCriteria(Preference.class);
      criteria.add(Restrictions.eq("module", module));
      criteria.add(Restrictions.eq("authenticationId", authenticationId));
      return gePreferencesMap(criteria);
   }

   /** {@inheritDoc} */
   @SuppressWarnings("unchecked")
   @Transactional(readOnly = true)
   public List<String> getPreferenceValues(String module, String userId, String key) {
      Long authenticationId = authenticationService.getAuthenticationIdForUserId(userId);
      Criteria criteria = getSession().createCriteria(Preference.class);
      criteria.add(Restrictions.eq("module", module));
      criteria.add(Restrictions.eq("authenticationId", authenticationId));
      criteria.add(Restrictions.eq("key", key));

      List<String> result = new ArrayList<String>();
      for (Iterator iter = criteria.list().iterator(); iter.hasNext();) {
         Preference p = (Preference) iter.next();
         result.add(p.getValue());
      }
      return result;
   }

   @SuppressWarnings("unchecked")
   private Map<String, String> gePreferencesMap(Criteria criteria) {
      List userPreferenceList = criteria.list();
      Map<String, String> preferenceMap = new HashMap<String, String>();
      for (Iterator iter = userPreferenceList.iterator(); iter.hasNext();) {
         Preference p = (Preference) iter.next();
         preferenceMap.put(p.getKey(), p.getValue());
      }
      return preferenceMap;
   }

   /** {@inheritDoc} */
   @Transactional
   public void createPreference(String module, String userId, String key, String value) {
      Long authenticationId = authenticationService.getAuthenticationIdForUserId(userId);
      Preference preference = new Preference();
      preference.setModule(module);
      preference.setAuthenticationId(authenticationId);
      preference.setKey(key);
      preference.setValue(value);
      getSession().save(preference);
   }

   /** {@inheritDoc} */
   @Transactional
   @SuppressWarnings("unchecked")
   public void updatePreference(String module, String userId, String key, String oldvalue, String newValue) {
      Long authenticationId = authenticationService.getAuthenticationIdForUserId(userId);
      Criteria criteria = getSession().createCriteria(Preference.class);
      criteria.add(Restrictions.eq("module", module));
      criteria.add(Restrictions.eq("authenticationId", authenticationId));
      criteria.add(Restrictions.eq("key", key));
      if (oldvalue != null) {
         criteria.add(Restrictions.eq("value", oldvalue));
      }
      List preferences = criteria.list();
      if (preferences.size() == 1) {
         Preference p = (Preference) preferences.get(0);
         p.setValue(newValue);
      }
   }

   /** {@inheritDoc} */
   @Transactional
   @SuppressWarnings("unchecked")
   public void deletePreference(String module, String userId, String key, String value) {
      Long authenticationId = authenticationService.getAuthenticationIdForUserId(userId);
      Criteria criteria = getSession().createCriteria(Preference.class);
      criteria.add(Restrictions.eq("module", module));
      criteria.add(Restrictions.eq("authenticationId", authenticationId));
      criteria.add(Restrictions.eq("key", key));
      criteria.add(Restrictions.eq("value", value));
      List preferences = criteria.list();
      if (preferences.size() == 1) {
         Preference p = (Preference) preferences.get(0);
         getSession().delete(p);
      }
   }

   @Required
   public void setAuthenticationService(AuthenticationService authenticationService) {
      this.authenticationService = authenticationService;
   }

   @SuppressWarnings("unchecked")
   @Transactional(readOnly = true)
   public List<PreferenceVO> getPreferences(PreferenceVO preference, int offset, int pageSize, String orderBy,
         String direction) {

      List<PreferenceVO> preferences = new ArrayList<PreferenceVO>();
      StringBuffer queryString = new StringBuffer(
            "select new com.finalist.cmsc.services.community.domain.PreferenceVO(p.id,a.userId,p.authenticationId,p.module,p.key,p.value) from Preference as p , Authentication as a where a.id=p.authenticationId ");
      if (preference != null) {
         if (StringUtils.isNotBlank(preference.getUserId())) {
            queryString.append("and a.userId like '%" + preference.getUserId() + "%' ");
         }
         if (StringUtils.isNotBlank(preference.getModule())) {
            queryString.append("and p.module like '%" + preference.getModule() + "%' ");
         }
         if (StringUtils.isNotBlank(preference.getKey())) {
            queryString.append("and p.key like '%" + preference.getKey() + "%' ");
         }
         if (StringUtils.isNotBlank(preference.getValue())) {
            queryString.append("and p.value like '%" + preference.getValue() + "%' ");
         }
      }
      if (StringUtils.isNotEmpty(orderBy)) {
         if (StringUtils.isEmpty(direction) || direction.equalsIgnoreCase("down") || direction.equalsIgnoreCase("desc")) {
            if (StringUtils.equalsIgnoreCase(orderBy, "userId")) {
               queryString.append("order by a.userId desc");
            } else {
               queryString.append("order by p." + orderBy + " desc");
            }
         } else {
            if (StringUtils.equalsIgnoreCase(orderBy, "userId")) {
               queryString.append("order by a.userId asc");
            } else {
               queryString.append("order by p." + orderBy + " asc");
            }
         }
      } else {
         queryString.append("order by p.id desc");
      }
      Query query = getSession().createQuery(queryString.toString());
      query.setFirstResult(offset).setMaxResults(pageSize);
      preferences = query.list();

      return preferences;
   }

   @Transactional(readOnly = true)
   public List<PreferenceVO> getPreferences(int offset, int pageSize, String orderBy, String direction) {
      return getPreferences(null, offset, pageSize, orderBy, direction);
   }

   @Transactional
   public void createPreference(PreferenceVO preference) {
      createPreference(preference.getModule(), preference.getUserId(), preference.getKey(), preference.getValue());
   }

   @Transactional
   public void createPreference(Preference preference, String userId) {
      createPreference(preference.getModule(), userId, preference.getKey(), preference.getValue());
   }

   @Transactional
   public void updatePreference(PreferenceVO preferenceVO) {
      try {
         Preference preference = (Preference) getSession().load(Preference.class, Long.parseLong(preferenceVO.getId()));
         preference.setKey(preferenceVO.getKey());
         preference.setValue(preferenceVO.getValue());
         getSession().saveOrUpdate(preference);
      } catch (HibernateException e) {
         e.printStackTrace();
      }
   }

   @Transactional
   public void deletePreference(String number) {
      Preference preference = (Preference) getSession().get(Preference.class, Long.parseLong(number));
      getSession().delete(preference);
   }

   public void deletePreference(long number) {
      Preference preference = (Preference) getSession().get(Preference.class, number);
      getSession().delete(preference);
   }

   @Transactional(readOnly = true)
   public List<String> getAllUserIds() {
      List<Authentication> authentications = authenticationService.findAuthentications();
      List<String> userIds = new ArrayList<String>();
      for (Authentication authentication : authentications) {
         userIds.add(authentication.getUserId());
      }
      return userIds;
   }

   @Transactional(readOnly = true)
   public int getTotalCount(PreferenceVO preference) {
      Criteria criteria = getSession().createCriteria(Preference.class);
      if (preference != null) {

         if (StringUtils.isNotBlank(preference.getUserId())) {
            Criteria authentication = getSession().createCriteria(Authentication.class);
            authentication.add(Restrictions.ilike("userId", "%" + preference.getUserId() + "%"));
            List<Authentication> authentications = authentication.list();
            List<Long> authenticationIds = new ArrayList<Long>();
            for (Authentication authe : authentications) {
               authenticationIds.add(authe.getId());
            }
            if (authenticationIds.size() > 0) {
               criteria.add(Restrictions.in("authenticationId", authenticationIds));
            }
         }
         if (StringUtils.isNotBlank(preference.getModule())) {
            criteria.add(Restrictions.ilike("module", "%" + preference.getModule() + "%"));
         }
         if (StringUtils.isNotBlank(preference.getKey())) {
            criteria.add(Restrictions.ilike("key", "%" + preference.getKey() + "%"));
         }
         if (StringUtils.isNotBlank(preference.getValue())) {
            criteria.add(Restrictions.ilike("value", "%" + preference.getValue() + "%"));
         }
      }
      return criteria.list().size();
   }

    private void copyPropertiesToVO(List < PreferenceVO > dest, List < Preference > src) {
       if (src == null || src.size() == 0) {
          return;
       }
       for (Preference preference : src) {
          PreferenceVO preferenceVO = new PreferenceVO();
          preferenceVO.setId(String.valueOf(preference.getId()));
          preferenceVO.setKey(preference.getKey());
          preferenceVO.setValue(preference.getValue());
          preferenceVO.setModule(preference.getModule());
          preferenceVO.setUserId(getUserIdByAuthenticationId(preference.getAuthenticationId()));
          preferenceVO.setAuthenticationId(String.valueOf(preference.getAuthenticationId()));
          dest.add(preferenceVO);
       }
    }

   @Transactional
   private String getUserIdByAuthenticationId(Long authenticationId) {
      Authentication authentication = authenticationService.getAuthenticationById(authenticationId);
      return authentication.getUserId();
   }

   @Transactional
   public void batchCleanByAuthenticationId(long authenticationId) {
      String hqlDelete = "delete Preference where authenticationid =:authenticationId";
      getSession().createQuery(hqlDelete).setLong("authenticationId", authenticationId).executeUpdate();

   }
}
