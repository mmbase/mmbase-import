/*

 This software is OSI Certified Open Source Software.
 OSI Certified is a certification mark of the Open Source Initiative.

 The license (Mozilla version 1.0) can be read at the MMBase site.
 See http://www.MMBase.org/license

 */
package com.finalist.cmsc.services.community.person;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Transactional;

import com.finalist.cmsc.services.HibernateService;
import com.finalist.cmsc.services.community.security.AuthenticationService;

/**
 * @author Remco Bos
 */
public class PersonHibernateService extends HibernateService implements PersonService {

	private static Log log = LogFactory.getLog(PersonHibernateService.class);

	private AuthenticationService authenticationService;

	/** {@inheritDoc} */
	@Transactional(readOnly = true)
	public Person getPersonByUserId(String userId) {
		return findPersonByUserId(userId);
	}

	/** {@inheritDoc} */
	@Transactional
	public void createPerson(String firstName, String infix, String lastName, String userId) {
		Long authenticationId = authenticationService.getAuthenticationIdForUserId(userId);
		if (authenticationId != null) {
			Person person = new Person();
			person.setFirstName(firstName);
			person.setInfix(infix);
			person.setLastName(lastName);
			person.setAuthenticationId(authenticationId);
			getSession().save(person);
		}
	}

	/** {@inheritDoc} */
	@Transactional
	public void deletePersonByUserId(String userId) {
		if (userId != null) {
			Person person = findPersonByUserId(userId);
			if (person != null) {
				getSession().delete(person);
			}
		}
	}

	private Person findPersonByUserId(String userId) {
		Long authenticationId = authenticationService.getAuthenticationIdForUserId(userId);
		Person person = null;
		if (authenticationId != null) {
			Criteria criteria = getSession().createCriteria(Person.class).add(
					Restrictions.eq("authenticationId", authenticationId));
			person = findPersonByCriteria(criteria);
		}
		return person;
	}

	@SuppressWarnings("unchecked")
	private Person findPersonByCriteria(Criteria criteria) {
		List personList = criteria.list();
		return personList.size() == 1 ? (Person) personList.get(0) : null;
	}

	@Required
	public void setAuthenticationService(AuthenticationService authenticationService) {
		this.authenticationService = authenticationService;
	}
}
