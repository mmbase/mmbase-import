package com.finalist.cmsc.services.community.security;


import org.springframework.test.AbstractTransactionalDataSourceSpringContextTests;
import org.springframework.util.Assert;
import org.springframework.beans.factory.annotation.Required;


public class AuthenticationServiceTest extends AbstractTransactionalDataSourceSpringContextTests   {

    private AuthenticationService authenticationService;

    @Override
    protected String[] getConfigLocations() {
        return new String[] {"spring-community.xml"};
    }
    
    public void testCreateAuthentication() {
        authenticationService.createAuthentication("admin", "admin2k");
        Assert.isTrue(authenticationService.authenticationExists("admin"));
        Assert.isTrue(authenticationService.authenticate("admin", "admin2k"));
    }
    public void testAuthenticationExists() {
        Assert.isTrue(!authenticationService.authenticationExists("SomeUser"));
        authenticationService.createAuthentication("SomeUser", "asdf");
        Assert.isTrue(authenticationService.authenticationExists("SomeUser"));
    }
    public void testUpdateAuthenticationPassword() {
        authenticationService.createAuthentication("SomeUser", "asdf");
        authenticationService.updateAuthenticationPassword("SomeUser", "jkl;");
        Assert.isTrue(authenticationService.authenticate("SomeUser", "jkl;"));
    }
    public void testDeleteAuthentication() {
        authenticationService.createAuthentication("SomeUser", "asdf");
        Assert.isTrue(authenticationService.authenticationExists("SomeUser"));
        authenticationService.deleteAuthentication("SomeUser");
        Assert.isTrue(!authenticationService.authenticationExists("SomeUser"));
    }
    public void testIsAuthenticationEnabled() {
        authenticationService.createAuthentication("SomeUser", "asdf");
        Assert.isTrue(authenticationService.isAuthenticationEnabled("SomeUser"));
    }
    public void testSetAuthenticationEnabled() {
        authenticationService.createAuthentication("SomeUser", "asdf");
        Assert.isTrue(authenticationService.isAuthenticationEnabled("SomeUser"));
        authenticationService.setAuthenticationEnabled("SomeUser", false);
        Assert.isTrue(!authenticationService.isAuthenticationEnabled("SomeUser"));
        authenticationService.setAuthenticationEnabled("SomeUser", true);
        Assert.isTrue(authenticationService.isAuthenticationEnabled("SomeUser"));
    }
    @Required
    public void testAuthenticate() {
        authenticationService.createAuthentication("SomeUser", "asdf");
        Assert.isTrue(authenticationService.authenticate("SomeUser", "asdf"));
    }
    @Required
    public void setAuthenticationService(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }
}