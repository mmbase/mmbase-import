 <%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" 
%><mm:cloud logon="admin" pwd="admin2k">

  <mm:import externid="newaccount" />
  <mm:import externid="newfirstname" />
  <mm:import externid="newlastname" />
  <mm:import externid="newemail" />

	<mm:import id="noerror">ok</mm:import>

	<!-- first check if all fields where entered -->
	<mm:compare referid="newaccount" value=""><%response.sendRedirect("../newUser.jsp?error=info");%><mm:remove referid="noerror" /></mm:compare>
	<mm:compare referid="newfirstname" value=""><%response.sendRedirect("../newUser.jsp?error=info");%><mm:remove referid="noerror" /></mm:compare>
	<mm:compare referid="newlastname" value=""><%response.sendRedirect("../newUser.jsp?error=info");%><mm:remove referid="noerror" /></mm:compare>
	<mm:compare referid="newemail" value=""><%response.sendRedirect("../newUser.jsp?error=info");%><mm:remove referid="noerror" /></mm:compare>
	<!-- end checks for empty fields -->

	<!-- check if email allready has a account and warn the user -->	
	<mm:listnodes type="users" constraints="email='$newemail'" max="1">
		<%response.sendRedirect("../newUser.jsp?error=email");%>
		<mm:remove referid="noerror" />
	</mm:listnodes>
	<!-- end of email allready has a account check -->

	<!-- check of the account name is allready in use -->
	<mm:listnodes type="users" constraints="account='$newaccount'" max="1">
		<%response.sendRedirect("../newUser.jsp?error=account");%>
		<mm:remove referid="noerror" />
	</mm:listnodes>
	<!-- end of check of the account name is allready in use -->

	<mm:present referid="noerror">
	<!-- oke so we can make a account, link it to the possible interested 
	     group and send a email with the info -->

    		<mm:info id="newpassword" nodemanager="users" command="newpassword" write="false" />
		<mm:node number="Bugtracker.Interested" id="groupnode" />
		<mm:createnode id="usernode" type="users">
			<mm:setfield name="account"><mm:write referid="newaccount" /></mm:setfield>
			<mm:setfield name="firstname"><mm:write referid="newfirstname" /></mm:setfield>
			<mm:setfield name="lastname"><mm:write referid="newlastname" /></mm:setfield>
			<mm:setfield name="email"><mm:write referid="newemail" /></mm:setfield>
			<mm:setfield name="password"><mm:write referid="newpassword" /></mm:setfield>
		</mm:createnode>
	
    		<mm:createrelation role="related" source="groupnode" destination="usernode" />
	<!-- end of create a new account -->

	<!-- send a email with the account info -->
	<mm:createnode type="email">
		<mm:setfield name="mailtype">1</mm:setfield>
		<mm:setfield name="to"><mm:write referid="newemail" /></mm:setfield>
		<mm:setfield name="from">daniel@submarinechannel.com</mm:setfield>
		<mm:setfield name="subject">Your MMBase BugTracker account</mm:setfield>
		<mm:setfield name="body">
			Your account info for the MMBase Bugtracker :

			account : <mm:write referid="newaccount" />
			password : <mm:write referid="newpassword" />
		</mm:setfield>
	</mm:createnode>
	<!-- end of sending the email -->

	</mm:present>
</mm:cloud>
<%response.sendRedirect("../showMessage.jsp?message=newuser");%>
