package nl.didactor.security.plain;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.mmbase.module.core.MMObjectNode;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import nl.didactor.builders.PeopleBuilder;
import nl.didactor.security.AuthenticationComponent;

import nl.didactor.security.UserContext;

public class PlainSecurityComponent implements AuthenticationComponent {
    private static Logger log = Logging.getLoggerInstance(PlainSecurityComponent.class.getName());

    private PeopleBuilder users;

    private void checkBuilder() throws org.mmbase.security.SecurityException {
        if (users == null) {
            org.mmbase.module.core.MMBase mmb = (org.mmbase.module.core.MMBase) org.mmbase.module.Module.getModule("mmbaseroot");
            users = (PeopleBuilder) mmb.getMMObject("people");
            if (users == null) {
                String msg = "builder people not found";
                log.error(msg);
                throw new org.mmbase.security.SecurityException(msg);
            }
        }
    }

    public PlainSecurityComponent() {

    }

    public UserContext processLogin(HttpServletRequest request, HttpServletResponse response, String application) {
        checkBuilder();

        String sLogin = request.getParameter("username");
        String sPassword = request.getParameter("password");

        if (sLogin == null || sPassword == null) {
            log.debug("Did not find matching credentials");
            return null;
        }

        MMObjectNode user = users.getUser(sLogin, sPassword);
        if (user == null) {
            log.debug("Found credentials, but no matching user. Returning null");
            return null;
        }

        log.debug("Found matching credentials, so user is now logged in.");
        HttpSession session = request.getSession( true ); 
        session.setAttribute("didactor-plainlogin-userid", "" + user.getNumber());
        return new UserContext(user);
    }

    public UserContext isLoggedIn(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            String onum = (String) session.getAttribute("didactor-plainlogin-userid");
            if (onum != null) {
                checkBuilder();
                MMObjectNode user = users.getNode(onum);
                log.debug("Found 'didactor-plainlogin-userid' in session");
                return new UserContext(user);
            } else {
                log.debug("There is a session, but no 'didactor-plainlogin-userid' in it");
            }
        } else {
            log.debug("No session, so the user is not logged in");
        }
        return null;
    }
    
    public String getLoginPage(HttpServletRequest request, HttpServletResponse response) {
        String sLogin = request.getParameter("username");
        String sPassword = request.getParameter("password");
        if (sLogin != null && sPassword != null) {
            return "/login_plain.jsp?reason=failed";
        } else {
            return "/login_plain.jsp";
        }
    }
    
    public String getName() {
        return "didactor-plainlogin";
    }
    
    public void logout(HttpServletRequest request, HttpServletResponse respose) {
        log.debug("logout() called");
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.removeAttribute("didactor-plainlogin-userid");
        }
    }
}
