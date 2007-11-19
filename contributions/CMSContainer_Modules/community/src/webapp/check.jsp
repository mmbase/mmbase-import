<%@page import="nl.PassiveCallbackHandler" %>
<%@page import="javax.security.auth.login.LoginContext, java.util.*" %>
<%@page import="java.io.*" %>
<%
LoginContext lc = (LoginContext) request.getSession().getAttribute("lc");

		Iterator it = lc.getSubject().getPrincipals().iterator();
        while (it.hasNext()) 
            out.println("Authenticated: " + it.next().toString() + "<br>");

        it = lc.getSubject().getPublicCredentials(Properties.class).iterator();

        while (it.hasNext()) 
            out.println(it.next().toString());
            %>