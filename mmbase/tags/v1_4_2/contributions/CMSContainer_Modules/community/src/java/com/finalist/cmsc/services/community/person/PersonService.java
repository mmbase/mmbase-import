/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package com.finalist.cmsc.services.community.person;

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
    
    void createPerson(String firstName, String infix, String lastName, String userId);

    void deletePersonByUserId(String userId);

}
