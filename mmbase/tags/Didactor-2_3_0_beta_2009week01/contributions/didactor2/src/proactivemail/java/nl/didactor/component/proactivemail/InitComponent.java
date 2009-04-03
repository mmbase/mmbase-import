package nl.didactor.component.proactivemail;

import javax.servlet.http.HttpServlet;
import javax.servlet.ServletException;

public class InitComponent extends HttpServlet {

    public InitComponent() {
    }
    
    public void init() throws ServletException {
        EmailTemplateToUsers.internalUrl = (String)getServletContext().getInitParameter("internalUrl");
    }
    
    public void destroy() {}

    public java.lang.String getServletInfo() {
        return "ProActiveMail::InitComponent";
    }
}
