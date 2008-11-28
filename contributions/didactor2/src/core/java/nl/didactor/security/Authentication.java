package nl.didactor.security;

import java.util.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.mmbase.bridge.*;
import org.mmbase.bridge.util.Queries;
import org.mmbase.storage.search.FieldCompareConstraint;
import org.mmbase.bridge.implementation.BasicCloudContext;
import org.mmbase.bridge.jsp.taglib.pageflow.TreeHelper;
import org.mmbase.module.core.MMObjectNode;
import org.mmbase.security.Rank;
import org.mmbase.security.SecurityException;
import org.mmbase.security.classsecurity.*;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;
import org.mmbase.util.functions.*;
import org.mmbase.util.transformers.*;
import org.mmbase.util.Casting;


import java.util.concurrent.CopyOnWriteArrayList;


import nl.didactor.events.*;
import nl.didactor.builders.*;
import nl.didactor.security.UserContext;
import nl.didactor.security.plain.*;

/**
 * Didactor authentication routines. This class authenticates users against the
 * cloud, and returns their rank based on the builder they belong to.
 *
 * @author Johannes Verelst &lt;johannes.verelst@eo.nl&gt;
 */
public class Authentication extends org.mmbase.security.Authentication {
    private static final Logger log = Logging.getLoggerInstance(Authentication.class);

    public static String REASON_KEY = "nl.didactor.security.reason";

    final List<AuthenticationComponent> securityComponents = new CopyOnWriteArrayList<AuthenticationComponent>();

    /**
     * The method is called during loading of the security layer. It contains a
     * list of the security components that can be used.
     * TODO: the list of strings here should be changed to a method where components
     * can register themselves as security components.
     */
    protected void load() {
        String[] securityClasses = {
            PropertiesSecurityComponent.class.getName(),
            "nl.didactor.security.aselect.ASelectSecurityComponent", // if available, use aselect first
            PlainSecurityComponent.class.getName()      // always fall back on plain
        };
        for (String className : securityClasses) {
            try {
                Class cls = Class.forName(className);
                securityComponents.add((AuthenticationComponent)cls.newInstance());
            } catch (ClassNotFoundException cnfe) {
                log.service("Cannot initialize security class [" + className + "], will not use it.");
            } catch (Exception e) {
                log.warn("Cannot initialize security class [" + className + "]");
            }
        }
    }
    static final UserContext ANONYMOUS;
    static {
        ANONYMOUS  = new UserContext("anonymous", "anonymous", Rank.ANONYMOUS, "anonymous");
        Rank.createRank(20,  "unparticipant");
        Rank.createRank(50,  "didactor user"); // lower than 'basic' user, because normally didactor
                                               // users may not enter generic editors.
        Rank.createRank(200, "editor");
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
     * Decorates the request with attributes related to current user, as expected by Didactor
     * templates.
     * @since Didactor-2.3
     */
    protected org.mmbase.security.UserContext request(org.mmbase.security.UserContext uc, HttpServletRequest req) {
        Node n = getUserNode(ContextProvider.getDefaultCloudContext().getCloud("mmbase"), uc.getIdentifier());
        req.setAttribute("user", n == null ? "0" : n.getNumber());
        log.debug("Found user " + (n == null ? "NULL" :  n.getNumber()) + " " + uc);
        Object education = req.getAttribute("education");
        if (education != null && n != null) {
            Function fun = n.getFunction("class");
            Parameters params = fun.createParameters();
            params.set("education", Casting.toInt(education));
            Node claz = (Node) fun.getFunctionValue(params);
            req.setAttribute("class", claz);
        }

        return uc;
    }
    /**
     * @since Didactor-2.3
     */
    protected void logout(HttpServletRequest request, HttpServletResponse response) {
        log.debug("Processing didactor logout because ", new Exception());
        HttpSession session = request == null ? null : request.getSession(false);
        if (session != null) {
            session.removeAttribute(nl.didactor.filter.ProviderFilter.USER_KEY);
            session.removeAttribute(nl.didactor.filter.ProviderFilter.EDUCATION_KEY);
            String loginComponent = (String)session.getAttribute("didactor-logincomponent");
            if (loginComponent != null) {
                for (AuthenticationComponent ac : securityComponents) {
                    if (ac != null && loginComponent.equals(ac.getName())) {
                        log.debug("Sending logout command to component '" + loginComponent + "'");
                        UserContext uc = ac.isLoggedIn(request, response);
                        if (uc == null) {
                            log.warn("Logging out a user who is not logged in! This cannot be reported!");
                        } else {
                            Event event = new Event(uc.getIdentifier(), request,
                                                    null, null, null, "LOGOUT", null, "logout");
                            org.mmbase.core.event.EventManager.getInstance().propagateEvent(event);
                        }
                        ac.logout(request, response);
                    }
                }
            }
        } else {
            log.warn("Cannot log out a user whose session is null");
        }
    }

    private static final CharTransformer PARAM_ESCAPER= new Url(Url.ESCAPE);
    /**
     * Login method: it tests the given credentials against MMBase.
     * The flow is as following:
     * - test to see if any of thee
     *
     * @param application
     *            The application identifier
     * @param loginInfo
     *            A Map containing the login credentials
     * @param parameters
     *            A list of optional parameters
     */
    @Override
    public org.mmbase.security.UserContext login(String application, Map loginInfo, Object[] parameters) throws org.mmbase.security.SecurityException {

        // Always allow anonymous access instantly
        if ("anonymous".equals(application)) {
            log.trace("Anonymous application: returning anonymous cloud ");
            if (loginInfo != null && Boolean.TRUE.equals(loginInfo.get("logout"))) {
                logout((HttpServletRequest) loginInfo.get(Parameter.REQUEST.getName()),
                       (HttpServletResponse) loginInfo.get(Parameter.RESPONSE.getName())
                       );
            }
            return ANONYMOUS;
        }
        if (log.isDebugEnabled()) {
            log.debug("login(" + application + ","+ loginInfo + "," + parameters + ")");
        }

        if ("class".equals(application)) {
            checkBuilder();
            ClassAuthentication.Login li = ClassAuthentication.classCheck("class");
            if (li == null) {
                throw new org.mmbase.security.SecurityException("Class authentication failed  '" + application + "' (class not authorized)");
            }
            String userName = (String) li.getMap().get(PARAMETER_USERNAME.getName());
            String rank     = (String) li.getMap().get(PARAMETER_RANK.getName());
            if (userName != null) {
                MMObjectNode user = users.getUser(userName);
                UserContext uc = new UserContext(user, "class");
                if (rank != null) {
                    if (uc.getRank().getInt() < Rank.getRank(rank).getInt()) {
                        return null;
                    }
                }
                return uc;
            } else {
                if (rank == null) rank = "basic user";
                UserContext uc = new UserContext("classuser", "classuser", Rank.getRank(rank), "class");
                return uc;
            }
        }
        if ("reincarnate".equals(application)) {
            checkBuilder();
            Cloud cloud = (Cloud) loginInfo.get(Parameter.CLOUD.getName());
            if (cloud == null) {
                throw new org.mmbase.security.SecurityException("Cannot reincarnate to other user, if not currently logged in.");
            }
            String userName = (String) loginInfo.get(PARAMETER_USERNAME.getName());
            if (userName == null) {
                throw new org.mmbase.security.SecurityException("Cannot reincarnate to other user without specifying username.");
            }
            MMObjectNode user = users.getUser(userName);
            if (user == null) {
                throw new org.mmbase.security.SecurityException("No such user '" + userName + "'");
            }
            UserContext uc = new UserContext(user, "reincarnate");
            org.mmbase.security.UserContext current = cloud.getUser();
            if (current.getRank().getInt() <= uc.getRank().getInt()) {
                throw new org.mmbase.security.SecurityException("Cannot reincarnate to user with higer or equal rank then yourself ('" + current+ "' -> '" + uc + "'");
            }
            log.info("Reincarnated " + current + " as " + uc);
            return uc;


        }

        HttpServletRequest request = null;
        HttpServletResponse response = null;
        Rank desiredRank = null;
        if (loginInfo != null) {
            request = (HttpServletRequest) loginInfo.get(Parameter.REQUEST.getName());
            response = (HttpServletResponse) loginInfo.get(Parameter.RESPONSE.getName());
            desiredRank = (Rank) loginInfo.get("rank");
        }

        if ("login".equals(application) && ((request == null) || (response == null))) {
            throw new org.mmbase.security.SecurityException("Cannot login withouth request and/or response objects");
        }

        // If the action is logging-out, try to find the component on which the
        // user logged in, and let that component process the logout.
        if ("logout".equals(application)) {
            logout(request, response);
            return null;
        }

        // First see if we can determine if the user is already logged in previously
        for (AuthenticationComponent ac : securityComponents) {
            UserContext uc = ac.isLoggedIn(request, response);
            if (log.isDebugEnabled()) {
                log.debug("" + ac + ".isLoggedIn() -> " + uc);
            }
            if (uc != null) {
                if (! uc.getAuthenticationType().equals(application)) {
                    return request(new UserContext(uc, application), request);
                } else {
                    return request(uc, request);
                }
            }
        }

        log.debug("Apparently not logged in yet, try to do that now: " + application);


        if ("name/password".equals(application)) {
            log.debug("Found 'name/password' application. Decorating request with name/password.");
            request.setAttribute("username", loginInfo.get("username"));
            request.setAttribute("password", loginInfo.get("password"));
            application = "login";
        }

        // Apparently not, so we ask the components if they can process the login,
        // maybe there was a post to the current page?
        for (AuthenticationComponent ac : securityComponents) {
            try {
                UserContext uc = ac.processLogin(request, response, application);
                if (log.isDebugEnabled()) {
                    log.debug("" + ac + ".processLogin() -> " + uc);
                }
                if (uc != null) {
                    HttpSession session = request.getSession(true);
                    session.setAttribute(REASON_KEY, null);
                    request.getSession(true).setAttribute("didactor-logincomponent", ac.getName());
                    Integer usernumber = uc.getUserNumber();
                    Event event = new Event(uc.getIdentifier(), request, null, null, null,
                                            "LOGIN", usernumber != null ? usernumber.toString() : null,
                                            "login");
                    org.mmbase.core.event.EventManager.getInstance().propagateEvent(event);
                    if (! uc.getAuthenticationType().equals(application)) {
                        return request(new UserContext(uc, application), request);
                    } else {
                        return request(uc, request);
                    }
                }
            } catch (SecurityException se) {
                HttpSession session = request.getSession(true);
                session.setAttribute(REASON_KEY, se.getMessage());
                log.service("For ac " + se.getMessage());
            }
        }

        // "Asis" means that we want a cloud as previously authenticated, or a new anonymous
        // one if there is no authenticated cloud. If we were authenticated we would not
        // have reached this point, so return an anonymous cloud here.
        if ("asis".equals(application)  && (desiredRank == null || desiredRank.getInt() == Rank.ANONYMOUS.getInt())) {
            log.debug("Asis application and not logged in: returning anonymous cloud");
            // wrapping in new user context because application should be correct.
            return new UserContext("anonymous", "anonymous", Rank.ANONYMOUS, "asis");
        }


        assert  application.equals("login") : "Unknown security application " + application;

        // Still nothing, that means that we have to redirect to the loginpage
        // We iterate the components to see if there is one that knows where to
        // go to.
        // TODO: maybe this should be configurable, so that you can specify which
        // security component should be used first by default. It will now automatically
        // go to the first one (aselect if that one is compiled in)
        for (AuthenticationComponent ac : securityComponents) {
            String loginPage = request != null ? ac.getLoginPage(request, response) : null;

            // Could use a TreeHelper here to do didactor specific tree-includeing on the login page
            // itself too.

            if (log.isDebugEnabled()) {
                log.debug("" + ac + ".getLoginPage() -> " + loginPage);
            }
            if (loginPage != null) {
                try {
                    StringBuilder referUrl = new StringBuilder(loginPage);
                    if (referUrl.indexOf("?") > -1) {
                        referUrl.append('&');
                    } else {
                        referUrl.append('?');
                    }
                    referUrl.append("referrer=");
                    String q = request.getQueryString();
                    String referrer = PARAM_ESCAPER.transform(request.getServletPath() + (q != null ? ("?" + q) : ""));
                    referUrl.append(referrer);
                    if (referUrl.toString().startsWith("/")) {
                        referUrl.insert(0, request.getContextPath());
                    }
                    // how about the paramters already present. This seems to be too simple. Escaping?
                    String redirect = response.encodeRedirectURL(referUrl.toString());
                    log.debug("Redirecting to " + redirect);
                    response.sendRedirect(redirect);
                } catch (Exception e) {
                    throw new SecurityException("Can't redirect to login page(" + loginPage + ") because " + e.getClass() + ":" + e.getMessage(), e);
                }
                return null;
            }
        }

        // Nothing left to do
        return null;
    }

    public  boolean isValid(org.mmbase.security.UserContext userContext) throws org.mmbase.security.SecurityException {
        if (userContext instanceof UserContext) {
            UserContext uc = (UserContext) userContext;
            log.debug("found " + uc);
            if (uc.getRank().equals(Rank.ADMIN)) return true;
            if (uc.getRank().equals(Rank.ANONYMOUS)) return true;
            checkBuilder();
            MMObjectNode user = users.getNode(uc.getUserNumber());
            if (user == null) {
                log.warn("No node found for node " + uc.getUserNumber());
                return false;
            }
            return user.getBooleanValue("person_status");
        } else {
            log.service("" + userContext + " is not valid because not of this security implementation");
            return false;
        }
    }

    /**
     * @deprecated
     */
    protected static Node getUserNode(Cloud cloud, String id){
        NodeManager people = cloud.getNodeManager("people");
        NodeQuery nq = people.createQuery();
        Queries.addConstraint(nq, Queries.createConstraint(nq, "username", FieldCompareConstraint.EQUAL, id, null, true));
        NodeList l = people.getList(nq);
        if (l.size() > 0) {
            return l.getNode(0);
        } else {
            return null;
        }
    }

    /**
     * @deprecated
     */
    public static Node getCurrentUserNode(Cloud cloud){
        return getUserNode(cloud, cloud.getUser().getIdentifier());

    }
    @Override public int getNode(org.mmbase.security.UserContext userContext) throws SecurityException {
        String id = userContext.getIdentifier();
        Node n = getUserNode(ContextProvider.getDefaultCloudContext().getCloud("mmbase", "class", null), id);
        return n == null ? -1 : n.getNumber();
    }


    private static final Parameter[]  NAME_PASSWORD_PARAMS = new Parameter[] {PARAMETER_USERNAME,
                                                                              PARAMETER_PASSWORD,
                                                                              Parameter.REQUEST,
                                                                              Parameter.RESPONSE};

    private static final Parameter[]  REINCARNATE_PARAMS = new Parameter[] {PARAMETER_USERNAME,
                                                                            Parameter.CLOUD};

    private static final Parameter[]  DEFAULT_PARAMS = new Parameter[] { Parameter.REQUEST, Parameter.RESPONSE};

    private static final Parameter[]  ANONYMOUS_PARAMS = new Parameter[] {new Parameter.Wrapper(PARAMETERS_ANONYMOUS),
                                                                          new Parameter.Wrapper(DEFAULT_PARAMS)};


    public Parameters createParameters(String application) {
        application = application.toLowerCase();
        Parameters parameters;
        if ("anonymous".equals(application)) {
            parameters =  new Parameters(ANONYMOUS_PARAMS);
        } else if ("class".equals(application)) {
            parameters =  Parameters.VOID;
        } else if ("name/password".equals(application)) {
            parameters = new Parameters(NAME_PASSWORD_PARAMS);
        } else if ("login".equals(application)) {
            parameters = new Parameters(DEFAULT_PARAMS);
        } else if ("reincarnate".equals(application)) {
            parameters = new Parameters(REINCARNATE_PARAMS);
        } else {
            parameters = new Parameters(DEFAULT_PARAMS);
        }
        if (log.isDebugEnabled()) {
            log.debug("Creating parameters for '" + application + "' -> " + parameters);
        }
        return parameters;
    }

    /**
     * {@inheritDoc}
     * @since MMBase-1.8
     */
    public int getDefaultMethod(String protocol) {
        return METHOD_SESSIONDELEGATE;
    }

    public String[] getTypes(int method) {
        switch(method) {
        case METHOD_ASIS:
            return new String[] {"anonymous", "login", "class"};
        case METHOD_HTTP:
            return new String[] {"name/password", "reincarnate"};
        case METHOD_DELEGATE:
        case METHOD_SESSIONDELEGATE:
            return new String[] {"login", "name/password", "reincarnate"}; // redirect page
        case METHOD_LOGINPAGE:
            return new String[] {"name/password", "login", "reincarnate"};
        default:
            return new String[] {"name/password", "login", "class", "reincarnate"};
        }
    }

}
