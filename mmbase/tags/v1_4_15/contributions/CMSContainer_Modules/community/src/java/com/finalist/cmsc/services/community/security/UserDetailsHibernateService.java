/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package com.finalist.cmsc.services.community.security;

import org.acegisecurity.GrantedAuthority;
import org.acegisecurity.GrantedAuthorityImpl;

import org.acegisecurity.userdetails.User;
import org.acegisecurity.userdetails.UserDetails;
import org.acegisecurity.userdetails.UserDetailsService;
import org.acegisecurity.userdetails.UsernameNotFoundException;

import org.springframework.dao.DataAccessException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Required;

import java.util.List;
import java.util.Set;
import java.util.ArrayList;

import com.finalist.cmsc.services.HibernateService;


/**
 * <p>Retrieves user details (username, password, enabled flag, and authorities) 
 * from the database.</p>
 *
 * @author Remco Bos
 */
public class UserDetailsHibernateService extends HibernateService implements UserDetailsService {

    private AuthenticationService authenticationService;

    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException, DataAccessException {
        /* Get authentication */
        Authentication authentication = authenticationService.findAuthentication(userName);
        if (authentication == null) {
            throw new UsernameNotFoundException("User not found");
        }

        /* Get authorities */
        Set<Authority> authorities = authentication.getAuthorities();
        List<GrantedAuthority> grantedAuthorities = new ArrayList<GrantedAuthority>();
        for (Authority authority : authorities) {
            grantedAuthorities.add(new GrantedAuthorityImpl(authority.getName()));
        }
        GrantedAuthority[] grantedAuthorityArray = (GrantedAuthority[]) grantedAuthorities.toArray(new GrantedAuthority[grantedAuthorities.size()]);

        /* Create user object */
        User user = new User(authentication.getUserId(), authentication.getPassword(), authentication.isEnabled(), true, true, true, grantedAuthorityArray);
        return user;
    }

    @Required
    public void setAuthenticationService(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }
}
