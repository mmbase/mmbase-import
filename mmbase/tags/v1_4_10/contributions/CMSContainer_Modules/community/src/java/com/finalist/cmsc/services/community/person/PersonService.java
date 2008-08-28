/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package com.finalist.cmsc.services.community.person;

import java.util.List;

/**
 * This service encapsulates the management of people and groups.
 *
 * People and groups may be managed entirely in the repository or entirely in
 * some other implementation such as LDAP or via NTLM. Some properties may in
 * the repository and some in another store. Individual properties may or may
 * not be mutable.
 *
 * @author Remco Bos
 */
public interface PersonService {

   Person getPersonByUserId(String userId);

   /**
    * Get a list of matching persons that match the given example. The fields
    * that are set on the example Person are the criteria for the search.
    *
    * @param example
    *           the example person
    * @return a list of persons that match the given example.
    */
   List<Person> getPersons(Person example);

   Person getPersonByAuthenticationId(Long authenticationId);

   Person createPerson(String firstName, String infix, String lastName, Long authenticationId);

   /*
    * Save or update the person to the database
    */
   void updatePerson(Person person);

   boolean deletePersonByAuthenticationId(Long userId);

}
