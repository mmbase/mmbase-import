package nl.didactor.security.plain;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.util.*;
import org.mmbase.util.*;
import java.io.InputStream;

import org.mmbase.module.core.MMObjectNode;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;
import org.mmbase.security.*;
import org.mmbase.security.SecurityException;
import nl.didactor.builders.PeopleBuilder;
import nl.didactor.security.AuthenticationComponent;

import nl.didactor.security.UserContext;

/**
 * Default AuthenticationComponent for Didactor.
 * @javadoc
 * @version $Id: PlainSecurityComponent.java,v 1.23 2008-03-20 19:56:49 michiel Exp $
 */

public class PlainSecurityComponent implements AuthenticationComponent {
    private static final Logger log = Logging.getLoggerInstance(PlainSecurityComponent.class);

    private PeopleBuilder users;
    private final Map<String, String> properties = new HashMap<String, String>();

    private void checkBuilder() throws org.mmbase.security.SecurityException {
        if (users == null) {
            org.mmbase.module.core.MMBase mmb = org.mmbase.module.core.MMBase.getMMBase();
            users = (PeopleBuilder) mmb.getBuilder("people");
            if (users == null) {
                throw new org.mmbase.security.SecurityException("builder people not found");
            }
        }
    }

    public PlainSecurityComponent() {
        ResourceWatcher fileWatcher = new ResourceWatcher() {
                public void onChange(String file) {
                    configure(file);
                }
            };
        fileWatcher.add("security/login.properties");
        fileWatcher.setDelay(10 * 1000);
        fileWatcher.start();
        fileWatcher.onChange();
    }

    protected void configure(String file) {
        properties.clear();
        Properties props = new Properties();
        try {
            InputStream is = ResourceLoader.getConfigurationRoot().getResource(file).openStream();
            props.load(is);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        for (String prop : Collections.list((Enumeration<String>) props.propertyNames())) {
            properties.put(prop, props.getProperty(prop));
        }

    }

    protected String getUserName(HttpServletRequest request) {
        String un = request == null ? null : request.getParameter("username");
        if (un == null && request != null) un = (String) request.getAttribute("username");
        return un;
    }
    protected String getPassword(HttpServletRequest request) {
        String p = request == null ? null : request.getParameter("password");
        if (p == null && request != null) p = (String) request.getAttribute("password");
        return p;
    }

    public UserContext processLogin(HttpServletRequest request, HttpServletResponse response, String application) {
        checkBuilder();

        String login    = getUserName(request);
        String password = getPassword(request);


        if (login == null || password == null) {
            log.debug("Did not find matching credentials");
            return null;
        }
        login    = login.trim();
        password = password.trim();

        log.debug("Processing log in");
        MMObjectNode user = users.getUser(login, password);
        if (user == null) {
            log.debug("No user found for " + login);
            user = users.getUser(login);
            if (user == null) {
                throw new SecurityException("No such user '" + login + "'");
            } else {
                throw new SecurityException("Wrong password");
            }
        }

        if ("".equals(user.getStringValue("password"))) {
            throw new SecurityException("User '" + login + "' has an empty password");
        }

        log.debug("Found matching credentials, so user is now logged in.");
        HttpSession session = request.getSession(true);
        session.setAttribute("didactor-plainlogin-userid", "" + user.getNumber());
        session.setAttribute("didactor-plainlogin-application", application);
        UserContext uc = new UserContext(user, application);
        if (! uc.getRank().equals(Rank.ADMIN)) {
            if (! user.getBooleanValue("person_status")) {
                throw new SecurityException("User '" + login + "' is disabled");
            }
        }
        return uc;
    }

    public UserContext isLoggedIn(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request == null ? null : request.getSession(false);
        if (session != null) {
            String onum = (String) session.getAttribute("didactor-plainlogin-userid");
            String app  = (String) session.getAttribute("didactor-plainlogin-application");
            if (onum != null) {
                try {
                    checkBuilder();
                    MMObjectNode user = users.getNode(onum);
                    if (user != null) {
                        if (log.isDebugEnabled()) {
                            log.debug("Found 'didactor-plainlogin-userid' in session user: " + user);
                        }
                        try {
                            return new UserContext(user, app == null ? "login" : app);
                        } catch (Exception e) {
                            log.warn(e.getMessage(), e);
                            return null;
                        }
                    } else {
                        log.debug("Could not find user object number " + onum);
                        session.removeAttribute("didactor-plainlogin-userid");
                    }
                } catch (Throwable t) {
                    log.warn("Something went wrong during checking session " + t, t);
                    return null;
                }

            } else {
                log.debug("There is a session, but no 'didactor-plainlogin-userid' in it");
            }
        } else {
            log.debug("No session, so the user is not logged in");
        }
        return null;
    }

    protected String getLoginPage(HttpServletRequest request) {

        log.debug("Trying " + request.getServerName() + request.getContextPath() + ".plain.login_page property ");
        String page = request == null ? null : (String) properties.get(request.getServerName() + request.getContextPath() + ".plain.login_page");
        if (page == null) {
            if (log.isDebugEnabled()) {
                log.debug("No " + request.getServerName() + request.getContextPath() + ".plain.login_page property found.");
            }
            page = request == null ? null : (String) properties.get(request.getServerName() + ".plain.login_page");
        }
        if (page == null) {
            if (log.isDebugEnabled()) {
                log.debug("No " + request.getServerName() + ".plain.login_page property found.");
            }
            page = (String) properties.get("plain.login_page");
        }
        if (page == null) {
            if (log.isDebugEnabled()) {
                log.debug("No plain.login_page property found.");
            }
            org.mmbase.module.core.MMBase mmb = org.mmbase.module.core.MMBase.getMMBase();
            if (mmb.getRootBuilder().getNode("component.portal") != null) {
                page = "/portal";
            }
        }
        return page == null ? "/login/" : page;
    }


    public String getLoginPage(HttpServletRequest request, HttpServletResponse response) {
        String login    = getUserName(request);
        String password = getPassword(request);
        if (login != null && password != null) {
            return getLoginPage(request) + "?reason=failed";
        } else {
            return getLoginPage(request);
        }
    }

    public String getName() {
        return "didactor-plainlogin";
    }

    public void logout(HttpServletRequest request, HttpServletResponse respose) {
        log.debug("logout called");
        HttpSession session = request == null ? null : request.getSession(false);
        if (session != null) {
            session.removeAttribute("didactor-plainlogin-userid");
        }
    }
}
