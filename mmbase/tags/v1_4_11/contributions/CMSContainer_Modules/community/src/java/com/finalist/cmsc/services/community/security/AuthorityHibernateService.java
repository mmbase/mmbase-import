/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package com.finalist.cmsc.services.community.security;


import org.springframework.transaction.annotation.Transactional;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;

import com.finalist.cmsc.services.HibernateService;


/**
 * @author Remco Bos
 */
public class AuthorityHibernateService extends HibernateService implements AuthorityService {

    /** {@inheritDoc} */
    @Transactional
    public Authority createAuthority(String parentName, String name) {
        Authority authority = new Authority();
        authority.setName(name);
        getSession().save(authority);
        return authority;
    }

    /** {@inheritDoc} */
    @Transactional
    public void deleteAuthority(String name) {
        Authority authority = findAuthorityByName(name);
        getSession().delete(authority);
    }

    /** {@inheritDoc} */
    @Transactional(readOnly = true)
    public boolean authorityExists(String name) {
        Authority authority = findAuthorityByName(name);
        return authority != null;
    }

    /** {@inheritDoc} */
    @Transactional(readOnly = true)
    public Authority findAuthorityByName(String name) {
        Criteria criteria = getSession()
                .createCriteria(Authority.class)
                .add(Restrictions.eq("name", name));
        return findAuthorityByCriteria(criteria);
    }

    /** {@inheritDoc} */
    @Transactional(readOnly = true)
    public Set<String> getAuthorityNames() {
        Criteria criteria = getSession().createCriteria(Authority.class);
        return findAuthorityNamesByCriteria(criteria);
    }

    /** {@inheritDoc} */
    @Transactional(readOnly = true)
    public Set<String> getAuthorityNamesForUser(String userId) {
        Criteria criteria = getSession()
                .createCriteria(Authority.class)
                .createCriteria("authentications")
                .add(Restrictions.eq("userId", userId));
        return findAuthorityNamesByCriteria(criteria);
    }

    @SuppressWarnings("unchecked")
	private Authority findAuthorityByCriteria(Criteria criteria) {
        List authorities = criteria.list();
        return authorities.size() == 1 ? (Authority) authorities.get(0) : null;
    }

    @SuppressWarnings("unchecked")
    private Set<String> findAuthorityNamesByCriteria(Criteria criteria) {
        List authorityList = criteria.list();
        Set<String> result = new HashSet<String>();
        for (Iterator iter = authorityList.iterator(); iter.hasNext();) {
            Authority authority = (Authority) iter.next();
            result.add(authority.getName());
        }
        return result;
    }

}
