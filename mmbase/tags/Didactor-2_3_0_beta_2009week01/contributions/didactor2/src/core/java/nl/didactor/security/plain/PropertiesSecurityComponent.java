package nl.didactor.security.plain;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.util.*;
import org.mmbase.util.*;
import java.io.InputStream;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;
import org.mmbase.security.*;
import org.mmbase.security.SecurityException;
import nl.didactor.builders.PeopleBuilder;
import nl.didactor.security.AuthenticationComponent;

import nl.didactor.security.UserContext;

/**
 * @version $Id: PropertiesSecurityComponent.java,v 1.2 2008-08-14 16:09:46 michiel Exp $
 */

public class PropertiesSecurityComponent implements AuthenticationComponent {
    private static final Logger log = Logging.getLoggerInstance(PropertiesSecurityComponent.class);

    private final Map<String, String> properties = new HashMap<String, String>();

    public PropertiesSecurityComponent() {
        ResourceWatcher fileWatcher = new ResourceWatcher() {
                public void onChange(String file) {
                    configure(file);
                }
            };
        fileWatcher.add("security/admins.properties");
        fileWatcher.setDelay(10 * 1000);
        fileWatcher.start();
        fileWatcher.onChange();
    }

    protected void configure(String file) {
        properties.clear();
        Properties props = new Properties();
        try {
            java.net.URL u = ResourceLoader.getConfigurationRoot().getResource(file);
            if (u.openConnection().getDoInput()) {
                InputStream is = ResourceLoader.getConfigurationRoot().getResource(file).openStream();
                props.load(is);
            }
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
        String login    = getUserName(request);
        String password = getPassword(request);


        if (login == null || password == null) {
            log.debug("Did not find matching credentials");
            return null;
        }
        login    = login.trim();
        password = password.trim();

        if (password.equals(properties.get(login))) {
            UserContext uc =  new UserContext(login, login, Rank.ADMIN, application);
            HttpSession session = request.getSession(true);
            session.setAttribute("didactor-propertieslogin-userid", "" + login);
            session.setAttribute("didactor-prpertieslogin-application", application);
            return uc;
        } else {
            throw new SecurityException("Cannot login");
        }


    }

    public UserContext isLoggedIn(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request == null ? null : request.getSession(false);
        if (session != null) {
            String user = (String) session.getAttribute("didactor-propertieslogin-userid");
            String app  = (String) session.getAttribute("didactor-propertieslogin-application");
            if (user != null && app != null) {
                log.service("Using " + properties.keySet() + " to login " + user);
                return  new UserContext(user, user, Rank.ADMIN, app);
            }
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
            session.removeAttribute("didactor-propertieslogin-userid");
        }
    }
}
