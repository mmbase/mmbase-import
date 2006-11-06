package nl.didactor.security;

import java.util.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.mmbase.bridge.*;
import org.mmbase.bridge.implementation.BasicCloudContext;
import org.mmbase.module.core.MMObjectNode;
import org.mmbase.security.Rank;
import org.mmbase.security.SecurityException;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;
import org.mmbase.util.functions.*;

import edu.emory.mathcs.backport.java.util.concurrent.CopyOnWriteArrayList;

import nl.didactor.events.*;
import nl.didactor.builders.*;
import nl.didactor.security.UserContext;

/**
 * Didactor authentication routines. This class authenticates users against the
 * cloud, and returns their rank based on the builder they belong to.
 *
 * @author Johannes Verelst &lt;johannes.verelst@eo.nl&gt;
 */
public class Authentication extends org.mmbase.security.Authentication {
    private static final Logger log = Logging.getLoggerInstance(Authentication.class);

    final List securityComponents = new CopyOnWriteArrayList();

    private static final String KEY_REQUEST = "request";

    private static final String KEY_RESPONSE = "response";

    /**
     * The method is called during loading of the security layer. It contains a
     * list of the security components that can be used.
     * TODO: the list of strings here should be changed to a method where components
     * can register themselves as security components.
     */
    protected void load() {
        String[] securityClasses = {
                "nl.didactor.security.aselect.ASelectSecurityComponent", // if available, use aselect first
                "nl.didactor.security.plain.PlainSecurityComponent"      // always fall back on plain
                };
        for (int i=0; i<securityClasses.length; i++) {
            try {
                Class cls = Class.forName(securityClasses[i]);
                securityComponents.add(cls.newInstance());
            } catch (Exception e) {
                log.warn("Cannot initialize security class [" + securityClasses[i] + "]");
            }
        }
    }
    static final UserContext ANONYMOUS;
    static {
        Rank.createRank(0, "didactor-anonymous");
        ANONYMOUS  = new UserContext("anonymous", "anonymous", Rank.getRank("didactor-anonymous"));
        Rank.createRank(200, "people");
    }

    private PeopleBuilder users;

    private void checkBuilder() throws org.mmbase.security.SecurityException {
        if (users == null) {
            org.mmbase.module.core.MMBase mmb = org.mmbase.module.core.MMBase.getMMBase();
            users = (PeopleBuilder) mmb.getBuilder("people");
            if (users == null) {
                throw new org.mmbase.security.SecurityException("builder people not found");
            }
        }
    }

    /**
     * Login method: it tests the given credentials against MMBase.
     * The flow is as following:
     * - test to see if any of the
     *
     * @param application
     *            The application identifier
     * @param loginInfo
     *            A Map containing the login credentials
     * @param parameters
     *            A list of optional parameters
     */
    public org.mmbase.security.UserContext login(String application, Map loginInfo, Object[] parameters) throws org.mmbase.security.SecurityException {

        // Always allow anonymous access instantly
        if ("anonymous".equals(application)) {
            log.debug("Anonymous application: returning anonymous cloud");
            return ANONYMOUS;
        }

        if (log.isDebugEnabled()) {
            log.debug("login(" + application + ","+ loginInfo + "," + parameters + ")");
        }
        HttpServletRequest request = null;
        HttpServletResponse response = null;
        Rank desiredRank = null;
        if (loginInfo != null) {
            request = (HttpServletRequest) loginInfo.get(KEY_REQUEST);
            response = (HttpServletResponse) loginInfo.get(KEY_RESPONSE);
            desiredRank = (Rank) loginInfo.get("rank");
        }

        // Always allow system access instantly
        if ((request == null) || (response == null)) {
            // so if you do _not_ provide any credentials you get admin!?

            log.debug("No request/response; returning system login");
            return new UserContext(this.doSystemLogin(), application);
        }

        // If the action is logging-out, try to find the component on which the
        // user logged in, and let that component process the logout.
        if ("didactor-logout".equals(application)) {
            log.debug("Processing didactor logout");
            HttpSession session = request.getSession(false);
            if (session != null) {
                String loginComponent = (String)session.getAttribute("didactor-logincomponent");
                if (loginComponent != null) {
                    for (int i=0; i<securityComponents.size(); i++) {
                        AuthenticationComponent ac = (AuthenticationComponent)securityComponents.get(i);
                        if (ac != null && loginComponent.equals(ac.getName())) {
                            log.debug("Sending logout command to component '" + loginComponent + "'");
                            UserContext uc = ac.isLoggedIn(request, response);
                            if (uc == null) {
                                log.warn("Logging out a user who is not logged in! This cannot be reported!");
                            } else {
                                Event event = new Event(uc.getIdentifier(), request.getSession(true).getId(), 
                                        null, null, null, "LOGOUT", null, "logout");
                                EventDispatcher.report(event, request, response);
                            }
                            ac.logout(request, response);
                        }
                    }
                }
            } else {
                log.warn("Cannot logout a user whos session is null");
            }

            return null;
        }

        // First see if we can determine if the user is already logged in previously
        for (int i = 0; i < securityComponents.size(); i++) {
            AuthenticationComponent ac = (AuthenticationComponent)securityComponents.get(i);
            UserContext uc = ac.isLoggedIn(request, response);
            if (log.isDebugEnabled()) {
                log.debug("" + ac + ".isLoggedIn() -> " + uc);
            }
            if (uc != null) {
                if (! uc.getAuthenticationType().equals(application)) {
                    return new UserContext(uc, application);
                } else {
                    return uc;
                }
            }
        }

        // Apparently not, so we ask the components if they can process the login,
        // maybe there was a post to the current page?
        for (int i = 0; i < securityComponents.size(); i++) {
            AuthenticationComponent ac = (AuthenticationComponent)securityComponents.get(i);
            UserContext uc = ac.processLogin(request, response, application);
            if (log.isDebugEnabled()) {
                log.debug("" + ac + ".processLogin() -> " + uc);
            }
            if (uc != null) {
                request.getSession(true).setAttribute("didactor-logincomponent", ac.getName());
                Integer usernumber = uc.getUserNumber();
                Event event = new Event(uc.getIdentifier(), request.getSession(true).getId(), null, null, null,
                        "LOGIN", usernumber != null ? usernumber.toString() : null, 
                        "login");
                EventDispatcher.report(event, request, response);
                if (! uc.getAuthenticationType().equals(application)) {
                    return new UserContext(uc, application);
                } else {
                    return uc;
                }
            }
        }

        // "Asis" means that we want a cloud as previously authenticated, or a new anonymous
        // one if there is no authenticated cloud. If we were authenticated we would not
        // have reached this point, so return an anonymous cloud here.
        if ("asis".equals(application)  && (desiredRank == null || desiredRank.getInt() == Rank.ANONYMOUS.getInt())) {
            log.debug("Asis application and not logged in: returning anonymous cloud");
            // wrapping in new user context because application should be correct.
            return new UserContext(new UserContext("anonymous", "anonymous", Rank.getRank("didactor-anonymous")), "asis");
        }

        // Still nothing, that means that we have to redirect to the loginpage
        // We iterate the components to see if there is one that knows where to
        // go to.
        // TODO: maybe this should be configurable, so that you can specify which
        // security component should be used first by default. It will now automatically
        // go to the first one (aselect if that one is compiled in)
        for (int i = 0; i < securityComponents.size(); i++) {
            AuthenticationComponent ac = (AuthenticationComponent)securityComponents.get(i);
            String loginPage = ac.getLoginPage(request, response);
            if (log.isDebugEnabled()) {
                log.debug("" + ac + ".getLoginPage() -> " + loginPage);
            }
            if (loginPage != null) {
                try {
                    String referUrl = loginPage;
                    if (referUrl.indexOf("?") > -1) {
                        referUrl += "&"; 
                    } else {
                        referUrl += "?";
                    }
                    referUrl += "referrer=" + request.getRequestURI(); 
                    if (referUrl.startsWith("/")) {
                        referUrl = request.getContextPath() + referUrl;
                    }
                    // how about the paramters alreayd present. This seems to be too simple. Escaping?
                    String sRedirect = response.encodeRedirectURL(referUrl);
                    response.sendRedirect(sRedirect);
                } catch (Exception e) {
                    throw new SecurityException("Can't redirect to login page(" + loginPage + ")", e);
                }
                return null;
            }
        }

        // Nothing left to do
        return null;
    }

    public boolean isValid(org.mmbase.security.UserContext usercontext) throws org.mmbase.security.SecurityException {
        return true;
    }

    /**
     * Admin's backdoor for Cron &amp; etc Doesn't require password for login
     *
     * @todo this problable can be dropped in favour of a 'class security' implementation.
     * @return UserContext
     */
    private UserContext doSystemLogin() {
        checkBuilder();
        MMObjectNode user = users.getUser("admin");
        if (user == null) {
            return null;
        }

        return new UserContext(user);
    }

    public static Node getCurrentUserNode(Cloud cloud){
       try{
              NodeList nlUsers = cloud.getList("",
                  "people",
                  "people.number",
                  "people.username='" + cloud.getUser().getIdentifier() + "'",
                  null, null, null, false);

              if(nlUsers.size() == 1){
                 Node nodeUser = cloud.getNode(nlUsers.getNode(0).getStringValue("people.number"));
                 return nodeUser;
              }
       }
       catch(Exception e){
       }
       return null;
    }

    public Parameters createParameters(String application) {
        // TODO think more deeply about this, and return an actual Parametrs object!
        return new AutodefiningParameters();
    }

    /**
     * @since MMBase-1.8
     */
    public int getMethod(String m) {
        return super.getMethod(m);
    }

    /**
     * {@inheritDoc}
     * @since MMBase-1.8
     */
    public int getDefaultMethod(String protocol) {
        return METHOD_DELEGATE;
    }

    public String[] getTypes(int method) {
        if (method == METHOD_ASIS) {
            return new String[] {"asis", "anonymous", "login", "class", "didactor-logout"};
        } else if (method == METHOD_DELEGATE) {
            return new String[] {"login", "asis", "class", "didactor-logout"};
        } else {
            return new String[] {"asis", "login", "class", "didactor-logout"};
        }
    }

}
