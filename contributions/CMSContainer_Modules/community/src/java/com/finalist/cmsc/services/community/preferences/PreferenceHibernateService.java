/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package com.finalist.cmsc.services.community.preferences;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Iterator;
import java.io.Serializable;

import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.Criteria;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Required;

import com.finalist.cmsc.services.HibernateService;
import com.finalist.cmsc.services.community.security.Authentication;
import com.finalist.cmsc.services.community.security.AuthenticationService;

/**
 * @author Remco Bos
 */
public class PreferenceHibernateService extends HibernateService implements PreferenceService {

    private AuthenticationService authenticationService;

    /** {@inheritDoc} */
    @Transactional(readOnly = true)
    public Map<String, Serializable> getPreferences(String module, String userName, String key, String value) {
        Long userId = null;
        if (!isEmpty(userName)) {
            Authentication authentication = authenticationService.findAuthentication(userName);
            if (authentication != null) {
                userId = authentication.getId();
            }
        }
        return findPreferences(module, userId, key, value);
    }

    /** {@inheritDoc} */
    @Transactional
    public Map<String, Serializable> createPreferences(String module, String userName, String key, String value) {
        Map<String, Serializable> result = null;
        Authentication authentication = authenticationService.findAuthentication(userName);
        if (authentication != null) {
            Preference preference = new Preference();
            preference.setModule(module);
            preference.setUserId(authentication.getId());
            preference.setKey(key);
            preference.setValue(value);
            getSession().save(preference);
            result = findPreferences(module, authentication.getId(), key, value);
        }
        return result;
    }

    private Map<String, Serializable> findPreferences(String module, Long userId, String key, String value) {
        Criteria criteria = getSession().createCriteria(Preference.class);
        if (!isEmpty(module)) {
            criteria.add(Restrictions.eq("module", module));
        }
        if (userId != null) {
            criteria.add(Restrictions.eq("userId", userId));
        }
        if (!isEmpty(key)) {
            criteria.add(Restrictions.eq("key", key));
        }
        if (!isEmpty(value)) {
            criteria.add(Restrictions.eq("value", value));
        }
        List preferenceList = criteria.list();
        if (preferenceList.size() == 0) return null;

        Map preferenceMap = new HashMap<String, Serializable>();
        for (Iterator iter = preferenceList.iterator(); iter.hasNext();) {
            Preference p = (Preference) iter.next();
            preferenceMap.put(p.getKey(), p.getValue());
        }
        return preferenceMap;
    }

    private boolean isEmpty(String stringValue) {
        return stringValue == null || stringValue.equals("");
    }

    @Required
    public void setAuthenticationService(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }
}
