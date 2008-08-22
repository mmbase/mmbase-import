package nl.didactor.security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import nl.didactor.security.UserContext;

/**
 * How is this different from org.mmbase.security.Authentication?
 */
public interface AuthenticationComponent {
    public UserContext processLogin(HttpServletRequest request, HttpServletResponse response, String application);
    public UserContext isLoggedIn(HttpServletRequest request, HttpServletResponse response);
    public String getLoginPage(HttpServletRequest request, HttpServletResponse response);
    public String getName();
    public void logout(HttpServletRequest request, HttpServletResponse respose);
}
