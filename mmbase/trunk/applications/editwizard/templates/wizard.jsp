<%@ include file="settings.jsp"
%><%@ page errorPage="exception.jsp"
%><%@ page import="org.mmbase.applications.editwizard.*, java.util.*, java.io.*"
%><%
	// This is the security check. It uses login.jsp to display a login page
	// if necessary.
	if (session.getAttribute("editwizard.username") == null){
		%>
			<jsp:forward page="login.jsp">
				<jsp:param name="ok-page" value="<%= request.getRequestURI() %>" />
			</jsp:forward>
		<%
	}
	boolean ok = false;
	
	String wizardinstance = request.getParameter("wizard");
	String wizardname = wizardinstance;
	String objectnumber = null;
	Wizard wiz = null;

	if (wizardinstance==null || wizardinstance.equals("")) {
		out.println(showError("No wizardname supplied. Please supply a wizardname.\nEditWizard needs a wizardname to run, eg.: samples/jumpers"));
		return;
	} else if (wizardinstance!=null && !wizardinstance.equals("")) {
		// ok.
		int pos = wizardinstance.indexOf("|");
		if (pos>-1) wizardname = wizardinstance.substring(0, pos);
		wiz = (Wizard)session.getValue("Wizard_"+wizardinstance);
		objectnumber = request.getParameter("objectnumber");

		// wizard in session? 
		if (wiz!=null && (objectnumber != null && wiz.wizardDataid.equals(objectnumber))) {
			// wizard already in session.
			// just process request.
			wiz.processRequest(request);
		} else {
			// new wizard!
			// TODO: verwijder de reeds aanwezige wizard (indien aanwezig dan, natuurlijk).
			if (objectnumber!=null && !objectnumber.equals("")) {
				// objectnumber was given. Create new wizard and store it!
				String username = (String)session.getAttribute("editwizard.username");
				String password = (String)session.getAttribute("editwizard.password");
				
				wiz = createNewWizard(settings_basedir, wizardname, objectnumber, username, password);
		
				if (wiz.errors.size()>0) {
					if (showErrors(wiz, out)) return;
				}
				
				// and store in session
				session.putValue("Wizard_"+wizardinstance, wiz);
				
				// set session timeout
				session.setMaxInactiveInterval(settings_sessiontimeout);
			} else {
				// no objectnumber was given. can't do anything, but we better clean up the existing instance, if there was any.
				session.removeValue("Wizard_"+wizardinstance);
			}
		}
	}

	if (wiz.mayBeClosed==true) {
		// wizard is ready, as far as he is concerned. Let's redirect to the list.
		
		// but first, remove the current wizard from the session.
		session.removeValue("Wizard_"+wizardinstance);
		
		// check to see if we know how to close this wizard.
		String popup = request.getParameter("popup");
		if (popup!=null && popup.equals("true")) {
			// this was a popup-wizard. Close it.
			%>
				<html><script language="javascript">window.close();</script></html>
			<%
		} else {
			response.sendRedirect("list.jsp");
		}
	} else {
		// send html back
		response.addHeader("Cache-Control","no-cache");
		response.addHeader("Pragma","no-cache");
		response.addHeader("Expires","0");
		wiz.writeHtmlForm(out, wizardinstance);
	}
%><%!

	// create new wizard
	private Wizard createNewWizard(String path, String wizardName, String objectnumber, String user, String pass) {
		try {
			Wizard wiz = new Wizard(path);
			wiz.initialize(wizardName, objectnumber, user, pass);
			return wiz;	
		} catch (Exception e) {
		}
		return null;
	}
	
	private boolean showErrors(Wizard wiz, Writer out) {
		String str;
		Iterator iter = wiz.errors.iterator();
		while (iter.hasNext()) {
			str = (String)iter.next();
			try {
				out.write(str);
			} catch (Exception e) {}
		}
		return true; // stop if any error is found!
	}
	
	private String showError(String msg) {
		return msg;
	}

%>