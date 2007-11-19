<%@page import="nl.RdbmsLoginModule, nl.PassiveCallbackHandler" %>
<%@page import="javax.security.auth.login.LoginContext, java.util.*" %>
<%@page import="java.io.*" %>

<!-- 
    jaas.jsp: Simple JSP page to test custom JAAS RdbmsLoginModule.
-->

<%
String configFileLocation = getServletContext().getRealPath("/WEB-INF/config/jaas.config");
//String policyFileLocation = getServletContext().getRealPath("/WEB-INF/config/jaas.policy");
System.err.println(configFileLocation);
//System.err.println(policyFileLocation);
System.setProperty("java.security.auth.login.config", configFileLocation );

if (request.getParameter("user") == null) {
%>
	<form action="jaas.jsp" method="POST">
		<input type=text name=user><br>
		<input type=text name=pass><br>
		<input type=submit value=submit>
	</form>
<%
} else {
	// just so you can see the debug messages
	//System.setOut(new PrintStream(response.getOutputStream()));

    try {
        // Get the form's username & password fields
        //
	    String user = request.getParameter("user");
	    String pass = request.getParameter("pass");

        // Use the username/password to initialize the
        // callback handler and then do the authentication.
        //
	    PassiveCallbackHandler cbh = new PassiveCallbackHandler(user, pass);

	    LoginContext lc = new LoginContext("Example", cbh);

	    lc.login();

        // Loop through all Principals and Credentials.
        //
        Iterator it = lc.getSubject().getPrincipals().iterator();
        while (it.hasNext()) 
            out.println("Authenticated: " + it.next().toString() + "<br>");

        it = lc.getSubject().getPublicCredentials(Properties.class).iterator();

        while (it.hasNext()) 
            out.println(it.next().toString());
	
	    lc.logout();

    } catch (Exception e) {
        out.println("Caught Exception: " + e);
    }
}
%>

