<%@ include file="settings.jsp"
%><%@ page errorPage="exception.jsp"
%><%@ page import="org.mmbase.applications.editwizard.*"
%><%@ page import="org.w3c.dom.*"
%><%
	/**
		This page first shows a login page and then (after a user/pass combination
		has been entered) tries to check with the database if the user/pass combination
		is correct.
		
		If it is not correct, the login page is shown again.
		If it is correct, the username and password are stored in the session (under the 
		names "editwizard.username" and "editwizard.password") and the flow of control
		is handed to the page that is given by the request parameter "ok-page" (default
		is "list.jsp").
		
		@uses loginform.jsp
		@uses WizardDatabaseConnector
	*/

	String username = request.getParameter("username");
	String password = request.getParameter("password");
	String messageCode = "OK";
	String nextPage = request.getParameter("ok-page");
	
	if (username == null || password == null){
		messageCode = "WELCOME";
		nextPage = "loginform.jsp";
	}else{
		if (isValidUser(username,password)){
			session.setAttribute("editwizard.username",username);
			session.setAttribute("editwizard.password",password);
			session.setMaxInactiveInterval(settings_sessiontimeout);
			if (nextPage == null){nextPage = "list.jsp";}
		}else{
			messageCode = "INVALID_LOGIN";
			nextPage = "loginform.jsp";
		}
	}

	if (messageCode.equals("OK")){
		// Build a URL with all request parameters (except the ones we used for the login process).
		// And redirect to that URL.
		String notHiddenNames = "|username|password|messageCode|ok-page|";
		java.util.Enumeration names = request.getParameterNames();
		String separator = "?";
		while (names.hasMoreElements()){
			String name = (String)names.nextElement();
			if (notHiddenNames.indexOf("|" + name + "|") == -1){
				nextPage = nextPage + separator + name + "=" + request.getParameter(name); 
				separator = "&";
			}
		}
		response.sendRedirect(nextPage);
	}else{
		%>
			<jsp:forward page="<%= nextPage %>">
				<jsp:param name="messageCode" value="<%= messageCode %>" />
			</jsp:forward>
		<%
	}
%><%!

	public boolean isValidUser(String username, String password) throws Exception{
		String baseDir = settings_basedir;
		// initialize database connector
		WizardDatabaseConnector dbconn = new WizardDatabaseConnector();
		dbconn.init(baseDir);
		dbconn.setUser(username,password);
	
		String queryString = "<query xpath=\"/*@typedef\" where=\"number=0\" />";
		Node query = Utils.parseXML(queryString).getDocumentElement();
		Node searchResult = null;
		try{
			searchResult = dbconn.getList(query);
		} catch (org.mmbase.applications.editwizard.SecurityException secure){
			System.out.println("Login failed: " + secure.toString());
			return false;
		} catch (Exception e){
			System.out.println("Login failed for other reasons than security: " + e.toString());
			throw e;
		}

		return true;
	}
%>
