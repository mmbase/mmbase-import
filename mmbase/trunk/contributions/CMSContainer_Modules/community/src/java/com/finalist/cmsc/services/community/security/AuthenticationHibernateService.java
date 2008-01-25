/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package com.finalist.cmsc.services.community.security;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Required;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.acegisecurity.providers.encoding.MessageDigestPasswordEncoder;
import org.acegisecurity.providers.encoding.Md5PasswordEncoder;

import java.util.List;

import com.finalist.cmsc.services.HibernateService;

/**
 * @author Remco Bos
 */
public class AuthenticationHibernateService extends HibernateService implements AuthenticationService {

    private AuthorityService authorityService;

    /** {@inheritDoc} */
    @Transactional
    public void createAuthentication(String userName, String password) {
        Authentication authentication = new Authentication();
        authentication.setUserName(userName);
        authentication.setPassword(encodePassword(password, userName));
        authentication.setEnabled(true);
        getSession().save(authentication);
    }

    /** {@inheritDoc} */
    @Transactional
    public void updateAuthentication(String userName, String oldPassword, String newPassword) {
        Authentication authentication = findAuthenticationByUserName(userName);
        if (authentication.getPassword().equals(oldPassword)) {
            authentication.setPassword(encodePassword(newPassword, userName));
            getSession().flush();
        }
    }

    /** {@inheritDoc} */
    @Transactional
    public void updateAuthenticationPassword(String userName, String newPassword) {
        Authentication authentication = findAuthenticationByUserName(userName);
        authentication.setPassword(encodePassword(newPassword, userName));
        getSession().flush();
    }

    /** {@inheritDoc} */
    @Transactional
    public void deleteAuthentication(String userName) {
        Authentication authentication = findAuthenticationByUserName(userName);
        getSession().delete(authentication);
    }

    /** {@inheritDoc} */
    @Transactional(readOnly = true)
    public boolean authenticationExists(String userName) {
        Authentication authentication = findAuthenticationByUserName(userName);
        return authentication != null;
    }

    /** {@inheritDoc} */
    @Transactional
    public void setAuthenticationEnabled(String userName, boolean enabled) {
        Authentication authentication = findAuthenticationByUserName(userName);
        authentication.setEnabled(enabled);
        getSession().flush();
    }

    /** {@inheritDoc} */
    @Transactional(readOnly = true)
    public boolean isAuthenticationEnabled(String userName) {
        Authentication authentication = findAuthenticationByUserName(userName);
        return authentication.isEnabled();
    }

    /** {@inheritDoc} */
    @Transactional(readOnly = true)
    public boolean authenticate(String userName, String password) {
        Authentication authentication = findAuthenticationByUserName(userName);
        return authentication.getPassword().equals(password);
    }

    /** {@inheritDoc} */
    @Transactional
    public void addAuthorityToUser(String userName, String authorityName) {
        Authentication authentication = findAuthenticationByUserName(userName);
        Authority authority = authorityService.findAuthorityByAuthorityName(authorityName);
        authentication.addAuthority(authority);
        getSession().flush();
    }
    
    /** {@inheritDoc} */
    @Transactional
    public void removeAuthorityFromUser(String userName, String authorityName) {
        Authentication authentication = findAuthenticationByUserName(userName);
        Authority authority = authorityService.findAuthorityByAuthorityName(authorityName);
        authentication.removeAuthority(authority);
        getSession().flush();
    }

    /** {@inheritDoc} */
    @Transactional(readOnly = true)
    public Authentication findAuthentication(String userName) {
        return findAuthenticationByUserName(userName);
    }
    
    private Authentication findAuthenticationByUserName(String userName) {
        Criteria criteria = getSession()
                .createCriteria(Authentication.class)
                .add(Restrictions.eq("userName", userName));
        return findAuthenticationByCriteria(criteria);
    }

    private Authentication findAuthenticationByCriteria(Criteria criteria) {
        List authenticationList = criteria.list();
        return authenticationList.size() == 1 ? (Authentication) authenticationList.get(0) : null;
    }

    private String encodePassword(String password, String salt) {
        MessageDigestPasswordEncoder encoder = new Md5PasswordEncoder();
        return encoder.encodePassword(password, salt);
    }

    @Required
    public void setAuthorityService(AuthorityService authorityService) {
        this.authorityService = authorityService;
    }

}
