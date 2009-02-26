package com.finalist.cmsc.services.community.security;


import org.springframework.test.AbstractTransactionalDataSourceSpringContextTests;
import org.springframework.util.Assert;
import org.springframework.beans.factory.annotation.Required;

import java.util.Set;


public class AuthorityServiceTest extends AbstractTransactionalDataSourceSpringContextTests   {

    private AuthorityService authorityService;
    private AuthenticationService authenticationService;

    @Override
    protected String[] getConfigLocations() {
        return new String[] {"spring-community.xml"};
    }

    public void testCreateAuthority() {
        authorityService.createAuthority(null, "SomeAuthority");
        Assert.isTrue(authorityService.authorityExists("SomeAuthority"));
    }
    public void testAuthorityExists() {
        Assert.isTrue(!authorityService.authorityExists("SomeAuthority"));
        authorityService.createAuthority(null, "SomeAuthority");
        Assert.isTrue(authorityService.authorityExists("SomeAuthority"));
    }
    public void testDeleteAuthority() {
        authorityService.createAuthority(null, "SomeAuthority");
        Assert.isTrue(authorityService.authorityExists("SomeAuthority"));
        authorityService.deleteAuthority("SomeAuthority");
        Assert.isTrue(!authorityService.authorityExists("SomeAuthority"));
    }
    public void testGetAuthorities() {
        authorityService.createAuthority(null, "SomeAuthority1");
        authorityService.createAuthority(null, "SomeAuthority2");
        authorityService.createAuthority(null, "SomeAuthority3");
        Set<String> authorities = authorityService.getAuthorityNames();
        Assert.isTrue(authorities.size() == 3);
    }

    public void testGetAuthoritiesForUser() {
        authorityService.createAuthority(null, "SomeAuthority1");
        authorityService.createAuthority(null, "SomeAuthority2");
        authorityService.createAuthority(null, "SomeAuthority3");

        authenticationService.createAuthentication("SomeUser", "asdf");
        authenticationService.addAuthorityToUser("SomeUser", "SomeAuthority1");

        Authority auth = authorityService.findAuthorityByName("SomeAuthority1");
        Set users = auth.getAuthentications();

        Set<String> authorities = authorityService.getAuthorityNamesForUser("SomeUser");
        Assert.isTrue(authorities.size() == 1);
    }

    @Required
    public void setAuthorityService(AuthorityService authorityService) {
        this.authorityService = authorityService;
    }

    @Required
    public void setAuthenticationService(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }
}