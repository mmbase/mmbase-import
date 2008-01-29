/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package com.finalist.cmsc.services.community.preferences;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Transactional;

import com.finalist.cmsc.services.HibernateService;
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
            Map<String, String> preferenceMap = (Map<String, String>)userPreferenceMap.get(p.getAuthenticationId());
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
            Map<String, String> preferenceMap = (Map<String, String>)modulePreferenceMap.get(p.getModule());
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
        criteria.add(Restrictions.eq("value", oldvalue));
        List preferences = criteria.list();
        if (preferences.size() == 1) {
        	Preference p = (Preference)preferences.get(0);
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
        	Preference p = (Preference)preferences.get(0);
        	getSession().delete(p);
        }
	}
    
    @Required
    public void setAuthenticationService(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }
}
