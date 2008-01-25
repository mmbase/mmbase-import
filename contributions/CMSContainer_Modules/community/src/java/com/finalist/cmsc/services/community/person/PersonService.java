/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package com.finalist.cmsc.services.community.person;

import java.io.Serializable;
import java.util.Map;

/**
 * This service encapsulates the management of people and groups.
 * 
 * People and groups may be managed entirely in the repository or entirely in
 * some other implementation such as LDAP or via NTLM. Some properties may in
 * the repository and some in another store. Individual properties may or may
 * not be mutable.
 */
public interface PersonService {

    public Person getPerson(String userName);
    
    public boolean personExists(String userName);

    public void setPersonProperties(String userName, Map<String, Serializable> properties);

    public Person createPerson(Map<String, Serializable> properties);

    public void deletePerson(String userName);

}
