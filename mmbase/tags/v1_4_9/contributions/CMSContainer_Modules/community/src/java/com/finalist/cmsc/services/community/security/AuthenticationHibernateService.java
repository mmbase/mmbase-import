/*

 This software is OSI Certified Open Source Software.
 OSI Certified is a certification mark of the Open Source Initiative.

 The license (Mozilla version 1.0) can be read at the MMBase site.
 See http://www.MMBase.org/license

 */
package com.finalist.cmsc.services.community.security;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Transactional;

import com.finalist.cmsc.services.HibernateService;

/**
 * @author Remco Bos
 */
public class AuthenticationHibernateService extends HibernateService implements AuthenticationService {

   private AuthorityService authorityService;

   /** {@inheritDoc} */
   @Transactional
   public Authentication createAuthentication(String userId, String password) {
      Authentication authentication = new Authentication();
      authentication.setUserId(userId);
      authentication.setPassword(encodePassword(password, userId));
      authentication.setEnabled(true);
      Long id = (Long) getSession().save(authentication);
      getSession().flush();
      authentication.setId(id);
      return authentication;
   }

   /** {@inheritDoc} */
   @Transactional
   public void updateAuthentication(String userId, String oldPassword, String newPassword) {
      Authentication authentication = findAuthenticationByUserId(userId);
      if (authentication.getPassword().equals(oldPassword)) {
         authentication.setPassword(encodePassword(newPassword, userId));
         getSession().flush();
      }
   }

   /** {@inheritDoc} */
   @Transactional
   public void updateAuthenticationPassword(String userId, String newPassword) {
      Authentication authentication = findAuthenticationByUserId(userId);
      authentication.setPassword(encodePassword(newPassword, userId));
      getSession().flush();
   }

   /** {@inheritDoc} */
   @Transactional
   public void deleteAuthentication(Long id) {
      Authentication authentication = getAuthenticationById(id);
      getSession().delete(authentication);
   }

   /** {@inheritDoc} */
   @Transactional(readOnly = true)
   public boolean authenticationExists(String userId) {
      Authentication authentication = findAuthenticationByUserId(userId);
      return authentication != null;
   }

   /** {@inheritDoc} */
   @Transactional
   public void setAuthenticationEnabled(String userId, boolean enabled) {
      Authentication authentication = findAuthenticationByUserId(userId);
      authentication.setEnabled(enabled);
      getSession().flush();
   }

   /** {@inheritDoc} */
   @Transactional(readOnly = true)
   public boolean isAuthenticationEnabled(String userId) {
      Authentication authentication = findAuthenticationByUserId(userId);
      return authentication.isEnabled();
   }

   /** {@inheritDoc} */
   @Transactional(readOnly = true)
   public boolean authenticate(String userId, String password) {
      Authentication authentication = findAuthenticationByUserId(userId);
      return authentication.getPassword().equals(encodePassword(password, userId));
   }

   /** {@inheritDoc} */
   @Transactional
   public void addAuthorityToUser(String userId, String authorityName) {
      Authentication authentication = findAuthenticationByUserId(userId);
      Authority authority = authorityService.findAuthorityByName(authorityName);
      authentication.addAuthority(authority);
      getSession().flush();
   }

   /** {@inheritDoc} */
   @Transactional
   public void removeAuthorityFromUser(String userId, String authorityName) {
      Authentication authentication = findAuthenticationByUserId(userId);
      Authority authority = authorityService.findAuthorityByName(authorityName);
      authentication.removeAuthority(authority);
      getSession().flush();
   }

   /** {@inheritDoc} */
   @Transactional(readOnly = true)
   public Authentication findAuthentication(String userId) {
      return findAuthenticationByUserId(userId);
   }

   /** {@inheritDoc} */
   @Transactional(readOnly = true)
   public Long getAuthenticationIdForUserId(String userId) {
      Authentication authentication = findAuthenticationByUserId(userId);
      return authentication == null ? null : authentication.getId();
   }

   private Authentication findAuthenticationByUserId(String userId) {
      Criteria criteria = getSession().createCriteria(Authentication.class).add(Restrictions.eq("userId", userId));
      return findAuthenticationByCriteria(criteria);
   }

   @SuppressWarnings("unchecked")
   private Authentication findAuthenticationByCriteria(Criteria criteria) {
      List authenticationList = criteria.list();
      return authenticationList.size() == 1 ? (Authentication) authenticationList.get(0) : null;
   }

   private String encodePassword(String password, String salt) {
      // MessageDigestPasswordEncoder encoder = new Md5PasswordEncoder();
      // // TODO Add salt to password encoder??
      // // return encoder.encodePassword(password, salt);
      // return encoder.encodePassword(password, null);
      return password;
   }

   /** {@inheritDoc} */
   @Transactional(readOnly = true)
   public List<Authentication> findAuthentications() {
      Criteria criteria = getSession().createCriteria(Authentication.class);
      return findAuthenticationListByCriteria(criteria);
   }

   /** {@inheritDoc} */
   @Transactional(readOnly = true)
   public List<Authentication> findAuthenticationsForAuthority(String name) {
      Criteria criteria = getSession().createCriteria(Authentication.class).createCriteria("authorities").add(Restrictions.eq("name", name));
      return findAuthenticationListByCriteria(criteria);
   }

   @SuppressWarnings("unchecked")
   private List<Authentication> findAuthenticationListByCriteria(Criteria criteria) {
      List<Authentication> result = new ArrayList<Authentication>();
      List authenticationList = criteria.list();
      for (Iterator iter = authenticationList.iterator(); iter.hasNext();) {
         Authentication authentication = (Authentication) iter.next();
         result.add(authentication);
      }
      return result;
   }

   @Required
   public void setAuthorityService(AuthorityService authorityService) {
      this.authorityService = authorityService;
   }

   /** {@inheritDoc} */
   @Transactional(readOnly = true)
   public Authentication getAuthenticationById(Long authenticationId) {
      return (Authentication) getSession().get(Authentication.class, authenticationId);
   }
}
