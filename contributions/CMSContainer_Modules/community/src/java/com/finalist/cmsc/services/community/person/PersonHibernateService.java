/*
 * 
 * This software is OSI Certified Open Source Software. OSI Certified is a certification mark of the Open Source
 * Initiative.
 * 
 * The license (Mozilla version 1.0) can be read at the MMBase site. See http://www.MMBase.org/license
 * 
 */
package com.finalist.cmsc.services.community.person;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Example;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.finalist.cmsc.paging.PagingStatusHolder;
import com.finalist.cmsc.paging.PagingUtils;
import com.finalist.cmsc.services.HibernateService;
import com.finalist.cmsc.services.community.domain.PersonExportImportVO;
import com.finalist.cmsc.services.community.preferences.Preference;
import com.finalist.cmsc.services.community.preferences.PreferenceService;
import com.finalist.cmsc.services.community.security.Authentication;
import com.finalist.cmsc.services.community.security.AuthenticationService;
import com.finalist.cmsc.services.community.security.Authority;

/**
 * @author Remco Bos
 */
public class PersonHibernateService extends HibernateService implements PersonService {

   private AuthenticationService authenticationService;
   private PreferenceService preferenceService;

   @Required
   public void setPreferenceService(PreferenceService preferenceService) {
      this.preferenceService = preferenceService;
   }

   /**
    * {@inheritDoc}
    */
   @Transactional(readOnly = true)
   public Person getPersonByUserId(String userId) {
      if (StringUtils.isBlank(userId)) {
         throw new IllegalArgumentException("UserId is not filled in. ");
      }
      return findPersonByUserId(userId);
   }

   /**
    * {@inheritDoc}
    */
   @Transactional
   @SuppressWarnings("unchecked")
   public List < Person > getPersons(Person example) {
      if (example == null) {
         return Collections.emptyList();
      }

      List < Person > personList = getSession().createCriteria(Person.class).add(Example.create(example)).list();
      return personList;

   }

   /**
    * {@inheritDoc}
    */
   @Transactional
   public List < Person > getLikePersons(Person example) {
      if (example == null) {
         return Collections.emptyList();
      }

      Example userExample = Example.create(example).enableLike(MatchMode.ANYWHERE).excludeZeroes().ignoreCase();
      List < Person > personList = getSession().createCriteria(Person.class).add(userExample).list();
      return personList;
   }

   /**
    * {@inheritDoc}
    */
   @Transactional
   public Person createPerson(String firstName, String infix, String lastName, Long authenticationId,String active,Date registerDate) {
      if (firstName == null) {
         throw new IllegalArgumentException("Firstname is null. ");
      }
      if (lastName == null) {
         throw new IllegalArgumentException("Lastname is null. ");
      }
      if (authenticationId == null) {
         throw new IllegalArgumentException("authenticationId is not filled in. ");
      }
      // Create a new person and store it
      Person person = new Person();
      person.setFirstName(firstName);
      person.setInfix(infix);
      person.setLastName(lastName);
      person.setAuthenticationId(authenticationId); // used to find account
      person.setActive(active);
      person.setRegisterDate(registerDate);
      getSession().save(person);
      return person;
   }

   /**
    * {@inheritDoc}
    */
   @Transactional
   public void updatePerson(Person person) {
      getSession().saveOrUpdate(person);
      getSession().flush();
   }

   @Transactional
   public List < Person > getAllPeople() {
      return getAssociatedPersons(null);
   }

   @Transactional
   public List < Person > getAllPersons() {
      Criteria criteria = getSession().createCriteria(Person.class);
      return criteria.list();
   }

   @Transactional(readOnly = true)
   public int countAllPersons() {
      Criteria criteria = getSession().createCriteria(Person.class);
      return criteria.list().size();
   }

   /**
    * {@inheritDoc}
    */
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
      List < Person > personList = criteria.list();
      return personList.size() > 0 ? personList.get(0) : null;
   }

   @Required
   public void setAuthenticationService(AuthenticationService authenticationService) {
      this.authenticationService = authenticationService;
   }

   /**
    * {@inheritDoc}
    */
   @Transactional(readOnly = true)
   public Person getPersonByAuthenticationId(Long authenticationId) {
      Person person = null;
      if (authenticationId != null) {
         Criteria criteria = getSession().createCriteria(Person.class).add(
               Restrictions.eq("authenticationId", authenticationId));
         person = findPersonByCriteria(criteria);
      }
      return person;
   }

   @Transactional(readOnly = true)
   public Person getPersonByEmail(String email) {
      Person person = null;
      if (email != null) {
         Criteria criteria = getSession().createCriteria(Person.class).add(Restrictions.eq("email", email));
         person = findPersonByCriteria(criteria);
      }
      return person;
   }

   @Transactional
   public void batchClean() {
      List < Person > persons = getAllPersons();
      for (Person tempPerson : persons) {
         if (null != tempPerson) {
            long authId = tempPerson.getAuthenticationId();
            deleteRelationRecord(authId);
         }
      }
   }

   @Transactional(propagation = Propagation.REQUIRED)
   public void deleteRelationRecord(Long id) {
      preferenceService.batchCleanByAuthenticationId(id);
      deletePersonByAuthenticationId(id);
      authenticationService.deleteAuthentication(id);
   }

   @Transactional(propagation = Propagation.REQUIRED)
   @SuppressWarnings("unchecked")
   public void creatRelationRecord(PersonExportImportVO xperson) {
      Authentication authentication = xperson.getAuthentication();
      if(authenticationService.authenticationExists(authentication.getUserId()) && xperson.getAuthorityId()>0 ){
         Authority authority = this.getAuthorityById(xperson.getAuthorityId());
         if(null!=authority){
            authentication = authenticationService.getAuthenticationById(authenticationService.getAuthenticationIdForUserId(authentication.getUserId()));
            authentication.getAuthorities().add(authority);
            getSession().saveOrUpdate(authentication);
         }
      }
      else if(!authenticationService.authenticationExists(authentication.getUserId())){
         authentication = authenticationService.createAuthentication(authentication);
         if(xperson.getAuthorityId()>0 ){
            Authority authority = this.getAuthorityById(xperson.getAuthorityId());
            if (null!=authentication.getAuthorities()) {
               authentication.getAuthorities().add(authority);
            }
         }
         Person person = new Person();
         converPersonPropertis(xperson, person);
         person.setAuthenticationId(authentication.getId());
         getSession().saveOrUpdate(authentication);
         updatePerson(person);
         String userId = xperson.getAuthentication().getUserId();
         List < Preference > preferences = xperson.getPreferences();
         if (preferences.size() > 0) {
            for (Preference preference : preferences) {
               preferenceService.createPreference(preference, userId);
            }
         }
      }
   }

   @Transactional(readOnly = true)
   public List < PersonExportImportVO > getPersonExportImportVO() {
      List < PersonExportImportVO > XPersons = new ArrayList < PersonExportImportVO >();
      List < Person > persons = getAllPersons();
      if (null == persons) {
         return null;
      }
      for (Person tempPerson : persons) {
         PersonExportImportVO o = transformToPersonExportImportVO(tempPerson);
         XPersons.add(o);
      }
      return XPersons;
   }
   @Transactional(readOnly = true)
   public List < PersonExportImportVO > getPersonExportImportVO(String group) {
      List < PersonExportImportVO > XPersons = new ArrayList < PersonExportImportVO >();
      List < Person > persons = getPersonsByGroup(group);
      if (null == persons) {
         return null;
      }
      for (Person tempPerson : persons) {
         PersonExportImportVO o = transformToPersonExportImportVO(tempPerson);
         XPersons.add(o);
      }
      return XPersons;
   }

   private void converPersonPropertis(Person t, Person o) {
      o.setFirstName(t.getFirstName());
      o.setInfix(t.getInfix());
      o.setNickname(t.getNickname());
      o.setLastName(t.getLastName());
      o.setEmail(t.getEmail());
      o.setUri(t.getUri());
      o.setActive(t.getActive());
      o.setRegisterDate(t.getRegisterDate());
   }

   private PersonExportImportVO transformToPersonExportImportVO(Person tempPerson) {
      PersonExportImportVO o = new PersonExportImportVO();
      converPersonPropertis(tempPerson, o);
      Long authenticationId = tempPerson.getAuthenticationId();
      Authentication authentication;
      authentication = authenticationService.getAuthenticationById(authenticationId);
      authentication.setAuthorities(null);
      String userId = authentication.getUserId();
      List < Preference > preferences = preferenceService.getListPreferencesByUserId(userId);
      converPersonPropertis(tempPerson, o);
      o.setAuthentication(authentication);
      o.setPreferences(preferences);
      return o;
   }
   private List<Person> getPersonsByGroup(String groupId){
      String sql = "select distinct person from Person person , Authentication authentication " +
      		       "left join authentication.authorities authority " +
      		       "where person.authenticationId = authentication.id"+
      		       " and authority.id = "+Integer.parseInt(groupId);
      Query q = getSession().createQuery(sql);
      return q.list();
   }

   @Transactional(readOnly = true)
   public List < Person > getAssociatedPersons(Map conditions) {
      PagingStatusHolder holder = PagingUtils.getStatusHolder();
      StringBuffer strb = new StringBuffer();
      basicGetAssociatedPersons(conditions, strb);
      strb.append(holder.getSortToken());
      Query q = getSession().createQuery(strb.toString());
      q.setMaxResults(holder.getPageSize()).setFirstResult(holder.getOffset());
      return q.list();
   }

   private void basicGetAssociatedPersons(Map < String , String > conditions, StringBuffer strb) {

      strb.append("select distinct person from Person person , Authentication authentication ").append(
            "left join authentication.authorities authority ").append(
            "where person.authenticationId = authentication.id");

      if (conditions.size() < 1) {
         return;
      }

      if (conditions.containsKey("fullname")) {
         String[] names = conditions.get("fullname").toString().split(" ");
         if (names.length == 2) {
            strb.append(" and ((person.firstName like '%" + names[0] + "%' and person.lastName like '%" + names[1]
                  + "%')" + " or person.firstName like '%" + names[0] + " " + names[1] + "%'"
                  + "or person.lastName like '%" + names[0] + " " + names[1] + "%')");
         } else if (names.length == 1) {
            strb.append(" and (person.firstName like '%" + names[0] + "%' or person.lastName like '%" + names[0]
                  + "%')");
         }
      }

      strb.append(condition("and upper(authentication.userId) like '%@%'", "username", conditions));
      strb.append(condition("and upper(person.email) like '%@%'", "email", conditions));

      if (conditions.containsKey("group")) {
         if (!conditions.containsKey("strict")) {
            String[] groups = conditions.get("group").split(" ");
            if (groups.length < 1) {
               return;
            }
            strb.append(" and (");
            strb.append("upper(authority.name)like '%" + groups[0].toUpperCase() + "%'");
            for (int i = 1; i < groups.length; i++) {
               strb.append(" or upper(authority.name)like '%" + groups[i].toUpperCase() + "%'");
            }
            strb.append(")");
         } else if ("strict".equals(conditions.get("strict"))) {
            String groups = conditions.get("group").toString();
            strb.append(" and authority.name='" + groups + "'");
         } else {
            String exceptNames = conditions.get("strict").toString();
            strb.append(" and authentication.userId not in ('" + exceptNames + "')");
         }
      }

   }

   @Transactional(readOnly = true)
   public int getAssociatedPersonsNum(Map < String , String > conditions) {
      StringBuffer strb = new StringBuffer();
      basicGetAssociatedPersons(conditions, strb);
      Query q = getSession().createQuery(strb.toString());
      return q.list().size();
   }

   private static String condition(String query, String name, Map < String , String > conditions) {
      String condition = "";

      if (conditions.containsKey(name)) {
         if (query.contains("%")) {
            query = StringUtils.replace(query, "%", "%%");
         }
         query = StringUtils.replace(query, "@", "%s");

         condition = String.format(query, conditions.get(name));
      }

      return " " + condition;

   }

   @Transactional
   public List < Person > getPersonsByAuthenticationIds(Set < Integer > authenticationIds, String name, String email) {
      List < Person > persons = null;
      if (authenticationIds.size() > 0) {
         StringBuffer stb = new StringBuffer("select distinct person from Person person");
         stb.append(" where person.authenticationId in (");
         for (int authId : authenticationIds) {
            stb.append(authId + ",");
         }
         stb.delete(stb.length() - 1, stb.length());
         stb.append(")");
         if (StringUtils.isNotBlank(email)) {
            stb.append(" and person.email like '%" + email + "%'");
         }
         if (StringUtils.isNotBlank(name)) {
            String[] names = name.split(" ");
            if (names.length >= 2) {
               stb.append(" and (person.firstName like '%" + names[0] + "%' or person.firstName like '%" + name + "%')"
                     + " and (person.lastName like '%" + names[1] + "%' or person.lastName like '%" + name + "%')");
            } else if (names.length == 1) {
               stb.append(" and (person.firstName like '%" + names[0] + "%')");
            }
         }
         Query q = getSession().createQuery(stb.toString());
         persons = q.list();
      }
      return persons;
   }

   @Transactional(propagation = Propagation.REQUIRED)
   private void updateRelationRecord(Person oldDataPerson, PersonExportImportVO importPerson) {
      Session session = getSession();
      Authentication activedAuthentication = importPerson.getAuthentication();
      if(importPerson.getAuthorityId()>0){
         Authority authority = this.getAuthorityById(importPerson.getAuthorityId());
         activedAuthentication.getAuthorities().add(authority);
      }
      Authentication dbAuthentication = (Authentication) session.load(Authentication.class, oldDataPerson.getAuthenticationId());
      converPersonPropertis(importPerson, oldDataPerson);
      converAuthenticationPropertis(activedAuthentication, dbAuthentication);
      parsePreferences(importPerson, session, activedAuthentication, dbAuthentication);
   }
   private Authority getAuthorityById(Long authorityId){
      Criteria criteria = getSession().createCriteria(Authority.class).add(
            Restrictions.eq("id", authorityId));
      return (Authority)criteria.list().get(0);
   }
   
   private void parsePreferences(PersonExportImportVO importPerson, Session session,
         Authentication activedAuthentication, Authentication dbAuthentication) {
      List < Preference > importPreferences = importPerson.getPreferences();
      List < Preference > dbPreferences = preferenceService.getListPreferencesByUserId(activedAuthentication
            .getUserId());
      Map < Long , Preference > util = new HashMap();
      for (Preference iPreference : importPreferences) {
         iPreference.setAuthenticationId(dbAuthentication.getId());
         util.put(iPreference.getId(), iPreference);
      }
      for (Preference dbPreference : dbPreferences) {
         Preference activePreference = util.get(dbPreference.getId());
         if (null != activePreference) {
            converPreferencePropertis(activePreference, dbPreference);
            util.remove(dbPreference.getId());
         }
      }
      for (Map.Entry < Long , Preference > entry : util.entrySet()) {
         session.save(entry.getValue());
      }
   }

   private void converPreferencePropertis(Preference iPreference, Preference dbPreference) {
      dbPreference.setAuthenticationId(iPreference.getAuthenticationId());
      dbPreference.setKey(iPreference.getKey());
      dbPreference.setModule(iPreference.getModule());
      dbPreference.setValue(iPreference.getValue());
   }

   private void converAuthenticationPropertis(Authentication activedAuthentication, Authentication dbAuthentication) {
      dbAuthentication.setAuthorities(activedAuthentication.getAuthorities());
      dbAuthentication.setEnabled(activedAuthentication.isEnabled());
      dbAuthentication.setPassword(activedAuthentication.getPassword());
      dbAuthentication.setUserId(activedAuthentication.getUserId());
   }

   @Transactional
   public void addRelationRecord(String level, PersonExportImportVO importPerson) {
      Person p = getPersonByUserId(importPerson.getAuthentication().getUserId());
      if ("over".equals(level)) {
         updateRelationRecord(p, importPerson);
      }
      else{
         // add new users or put user to another group
         creatRelationRecord(importPerson);
      }
     
   }
   @Transactional
   public void changeStateByAuthenticationId(Long authenticationId, String active) {
      Person per=getPersonByAuthenticationId(authenticationId);
      per.setActive(active);
      updatePerson(per);      
   }
   @Transactional
   public List<Authority> getAllAuthorities() {
      Criteria criteria = getSession().createCriteria(Authority.class);
      criteria = criteria.setResultTransformer(criteria.DISTINCT_ROOT_ENTITY); 
      return criteria.list();
   }
   @Transactional
   public void importDataFromFileRecord(String level,
         PersonExportImportVO importPerson) {
      Person p = getPersonByUserId(importPerson.getAuthentication().getUserId());
      if ("over".equals(level)) {
         if(p != null) {
            authenticationService.updateAuthenticationPassword(importPerson.getAuthentication().getUserId(), importPerson.getAuthentication().getPassword());
            p.setEmail(importPerson.getEmail());
            p.setActive(importPerson.getActive());
            updatePerson(p);
         }
         else {
            Authentication authentication = authenticationService.createAuthentication(importPerson.getAuthentication());
            if (authentication.getId() != null) {
               if(importPerson.getAuthorityId()>0 ){
                  Authority authority = this.getAuthorityById(importPerson.getAuthorityId());
                  if (null!=authentication.getAuthorities()) {
                     authentication.getAuthorities().add(authority);
                  }
               }
               addPerson(importPerson, authentication);
            } 
         }         
      }
      else{
         // add new users or put user to another group
         createNewPerson(importPerson);
      }
      
   }
   @Transactional
   private void addPerson(PersonExportImportVO importPerson,
         Authentication authentication) {
      Person person = new Person();
      person.setEmail(importPerson.getEmail());
      person.setActive(importPerson.getActive());
      person.setAuthenticationId(authentication.getId());
      getSession().saveOrUpdate(authentication);
      updatePerson(person);
   }
   @Transactional(propagation = Propagation.REQUIRED)
   private void createNewPerson(PersonExportImportVO xperson) {

      Authentication authentication = xperson.getAuthentication();
      if(authenticationService.authenticationExists(authentication.getUserId()) && xperson.getAuthorityId()>0 ){
         Authority authority = this.getAuthorityById(xperson.getAuthorityId());
         if(null!=authority){
            authentication = authenticationService.getAuthenticationById(authenticationService.getAuthenticationIdForUserId(authentication.getUserId()));
            authentication.getAuthorities().add(authority);
            getSession().saveOrUpdate(authentication);
         }
      }
      else if(!authenticationService.authenticationExists(authentication.getUserId())){
         authentication = authenticationService.createAuthentication(authentication);
         if(xperson.getAuthorityId()>0 ){
            Authority authority = this.getAuthorityById(xperson.getAuthorityId());
            if (null!=authentication.getAuthorities()) {
               authentication.getAuthorities().add(authority);
            }
         }
         addPerson(xperson, authentication);
      }
   
   }
}
