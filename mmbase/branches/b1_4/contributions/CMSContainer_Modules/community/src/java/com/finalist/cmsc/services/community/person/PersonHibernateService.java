/*

 This software is OSI Certified Open Source Software.
 OSI Certified is a certification mark of the Open Source Initiative.

 The license (Mozilla version 1.0) can be read at the MMBase site.
 See http://www.MMBase.org/license

 */
package com.finalist.cmsc.services.community.person;

import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Example;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Transactional;

import com.finalist.cmsc.services.HibernateService;
import com.finalist.cmsc.services.community.security.AuthenticationService;

/**
 * @author Remco Bos
 */
public class PersonHibernateService extends HibernateService implements PersonService {

   private AuthenticationService authenticationService;

   /** {@inheritDoc} */
   @Transactional(readOnly = true)
   public Person getPersonByUserId(String userId) {
      if (StringUtils.isBlank(userId)) {
         throw new IllegalArgumentException("UserId is not filled in. ");
      }
      return findPersonByUserId(userId);
   }

   /** {@inheritDoc} */
   @Transactional
   @SuppressWarnings("unchecked")
   public List<Person> getPersons(Person example) {
      if (example == null) {
         return Collections.emptyList();
      }

      List personList = getSession().createCriteria(Person.class).add(Example.create(example)).list();
      return personList;

   }

   /** {@inheritDoc} */
   @Transactional
   public Person createPerson(String firstName, String infix, String lastName, Long authenticationId) {
      if (firstName == null) {
         throw new IllegalArgumentException("Firstname is null. ");
      }
      if (lastName == null) {
         throw new IllegalArgumentException("Lastname is null. ");
      }
      if (authenticationId == null) {
         throw new IllegalArgumentException("authenticationId is not filled in. ");
      }

      if (authenticationId != null) {
         Person person = new Person();
         person.setFirstName(firstName);
         person.setInfix(infix);
         person.setLastName(lastName);
         person.setAuthenticationId(authenticationId); // used to find account
         getSession().save(person);
         return person;
      }
      return null;
   }

   /** {@inheritDoc} */
   @Transactional
   public void updatePerson(Person person) {
      getSession().saveOrUpdate(person);
      getSession().flush();
   }

   /** {@inheritDoc} */
   @Transactional
   public boolean deletePersonByAuthenticationId(Long authenticationId) {
      if (authenticationId != null) {
         Person person = getPersonByAuthenticationId(authenticationId);
         if (person != null) {
            getSession().delete(person);
            return true;
         }
      }
      return false;
   }

   private Person findPersonByUserId(String userId) {
      Long authenticationId = authenticationService.getAuthenticationIdForUserId(userId);
      return getPersonByAuthenticationId(authenticationId);
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

   /** {@inheritDoc} */
   @Transactional(readOnly = true)
   public Person getPersonByAuthenticationId(Long authenticationId) {
      Person person = null;
      if (authenticationId != null) {
         Criteria criteria = getSession().createCriteria(Person.class).add(Restrictions.eq("authenticationId", authenticationId));
         person = findPersonByCriteria(criteria);
      }
      return person;
   }

}
