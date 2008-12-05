/*
 * 
 * This software is OSI Certified Open Source Software. OSI Certified is a certification mark of the Open Source
 * Initiative.
 * 
 * The license (Mozilla version 1.0) can be read at the MMBase site. See http://www.MMBase.org/license
 * 
 */
package com.finalist.cmsc.services.community.security;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.finalist.cmsc.paging.PagingStatusHolder;

/**
 * @author Remco Bos
 */
public interface AuthorityService {

   /**
    * @return set with authorities names
    */
   Set < String > getAuthorityNames();

   List < Authority > getAllAuthorities(PagingStatusHolder holder);

   /**
    * @param userName
    * @return set authorities
    */
   Set < String > getAuthorityNamesForUser(String userName);

   /**
    * Create a new Authority
    * 
    * @param parentName
    * @param authorityName
    */
   Authority createAuthority(String parentName, String authorityName);

   /**
    * Delete an Authority
    * 
    * @param authorityName
    */
   void deleteAuthority(String authorityName);

   /**
    * Does an authority exist?
    * 
    * @param authorityName
    * @return boolean true if the authority exists, otherwise false
    */
   boolean authorityExists(String authorityName);

   /**
    * Finds the authoritdy
    * 
    * @param authorityName
    * @return Authority with the requested name (if it exists)
    */
   Authority findAuthorityByName(String authorityName);

   List < Authority > findAssociatedAuthorityByName(String name, PagingStatusHolder holder);

   int countAllAuthorities();

   int countAssociatedAuthorities(String name);

   /**
    * @param map stored paramate
    * @param holder treat PagingStatus
    * @return  List stored authorities  
    */
   List < Authority > getAssociatedAuthorities(Map map, PagingStatusHolder holder);

   /**
    * @param map stored paramate
    * @param holder treat PagingStatus
    * @return Num counted  
    */
   int getAssociatedAuthoritiesNum(Map map, PagingStatusHolder holder);

}
