/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package com.finalist.cmsc.services.community.person;

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
import com.finalist.cmsc.services.community.preferences.PreferenceService;
import com.finalist.cmsc.services.community.preferences.Preference;

/**
 * @author Remco Bos
 */
public class PersonHibernateService extends HibernateService implements PersonService {

    /** {@inheritDoc} */
    @Transactional(readOnly = true)
    public Person getPerson(String userName) {
        return findPersonByUserName(userName);
    }

    /** {@inheritDoc} */
    @Transactional(readOnly = true)
    public boolean personExists(String userName) {
        return findPersonByUserName(userName) != null;
    }

    /** {@inheritDoc} */
    @Transactional
    public void setPersonProperties(String userName, Map<String, Serializable> properties) {
        Person person = findPersonByUserName(userName);
        person.setProperties(properties);
    }

    /** {@inheritDoc} */
    @Transactional
    public Person createPerson(Map<String, Serializable> properties) {
        Person person = new Person();
        person.setProperties(properties);
        getSession().save(person);
        return person;
    }

    /** {@inheritDoc} */
    @Transactional
    public void deletePerson(String userName) {
        Person person = findPersonByUserName(userName);
        getSession().delete(person);
    }

    private Person findPersonByUserName(String userName) {
        Criteria criteria = getSession()
                .createCriteria(Authentication.class)
                .add(Restrictions.eq("userName", userName));
        return findPersonByCriteria(criteria);
    }

    private Person findPersonByCriteria(Criteria criteria) {
        List personList = criteria.list();
        return personList.size() == 1 ? (Person) personList.get(0) : null;
    }
}
