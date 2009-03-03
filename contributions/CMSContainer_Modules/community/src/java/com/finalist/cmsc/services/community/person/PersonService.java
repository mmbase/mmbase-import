/*
 * 
 * This software is OSI Certified Open Source Software. OSI Certified is a certification mark of the Open Source
 * Initiative.
 * 
 * The license (Mozilla version 1.0) can be read at the MMBase site. See http://www.MMBase.org/license
 * 
 */
package com.finalist.cmsc.services.community.person;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import com.finalist.cmsc.services.community.domain.PersonExportImportVO;
import com.finalist.cmsc.services.community.security.Authority;

/**
 * This service encapsulates the management of people and groups.
 * 
 * People and groups may be managed entirely in the repository or entirely in some other implementation such as LDAP or
 * via NTLM. Some properties may in the repository and some in another store. Individual properties may or may not be
 * mutable.
 * 
 * @author Remco Bos
 */
public interface PersonService {

   Person getPersonByUserId(String userId);

   /**
    * Get a list of matching persons that match the given example. The fields that are set on the example Person are the
    * criteria for the search.
    * 
    * @param example
    *           the example person
    * @return a list of persons that match the given example.
    */
   List < Person > getPersons(Person example);

   /**
    * Get a list of matching persons that like the given example. The fields that are set on the example Person are the
    * criteria for the search.
    * 
    * @param example
    *           the example person
    * @return a list of persons that like the given example.
    */
   List < Person > getLikePersons(Person example);

   Person getPersonByAuthenticationId(Long authenticationId);

   Person getPersonByEmail(String email);

   Person createPerson(String firstName, String infix, String lastName, Long authenticationId,String active,Date registerDate);

   /*
    * Save or update the person to the database
    */
   void updatePerson(Person person);

   List < Person > getAllPersons();

   public List < Person > getAllPeople();

   public int countAllPersons();

   boolean deletePersonByAuthenticationId(Long userId);

   public void batchClean();

   public void deleteRelationRecord(Long id);

   public void creatRelationRecord(PersonExportImportVO xperson);

   public List < PersonExportImportVO > getPersonExportImportVO();

   public List < Person > getAssociatedPersons(Map conditions);

   public int getAssociatedPersonsNum(Map < String , String > map);

   public List < Person > getPersonsByAuthenticationIds(Set < Integer > authenticationIds, String name, String email);

   public void addRelationRecord(String level, PersonExportImportVO importPerson);

   void changeStateByAuthenticationId(Long authenticationId, String active);

   public List<PersonExportImportVO> getPersonExportImportVO(String group);

   public List<Authority> getAllAuthorities();
   
   public void importDataFromFileRecord(String level, PersonExportImportVO importPerson);
}
