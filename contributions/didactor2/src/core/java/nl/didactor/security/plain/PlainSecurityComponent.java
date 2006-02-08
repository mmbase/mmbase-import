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
            return null;
        }

        MMObjectNode user = users.getUser(sLogin, sPassword);
        if (user == null) {
            return null;
        }

        request.getSession(true).setAttribute("didactor-plainlogin-userid", "" + user.getNumber());

        return new UserContext(user);
    }

    public UserContext isLoggedIn(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            String onum = (String) session.getAttribute("didactor-plainlogin-userid");
            if (onum != null) {
                checkBuilder();
                MMObjectNode user = users.getNode(onum);
                return new UserContext(user);
            }
        }
        return null;
    }
    
    public String getLoginPage(HttpServletRequest request, HttpServletResponse response) {
        return "/login_plain.jsp";
    }
    
    public String getName() {
        return "didactor-plainlogin";
    }
    
    public void logout(HttpServletRequest request, HttpServletResponse respose) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.removeAttribute("didactor-plainlogin-userid");
        }
    }
}
