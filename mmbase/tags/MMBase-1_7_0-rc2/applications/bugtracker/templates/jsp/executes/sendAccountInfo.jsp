<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<mm:cloud logon="admin" pwd="admin2k">
<mm:import externid="email" />

    <mm:listnodes type="users" constraints="email='$email'" max="1">
	<mm:import id="account"><mm:field name="account" /></mm:import>
	<mm:import id="password"><mm:field name="password" /></mm:import>
	<mm:createnode id="emailnode" type="email">
		<mm:setfield name="mailtype">1</mm:setfield>
		<mm:setfield name="to"><mm:write referid="email" /></mm:setfield>
		<mm:setfield name="from">daniel@195.169.149.135</mm:setfield>
		<mm:setfield name="subject">Your MMBase BugTracker account</mm:setfield>
		<mm:setfield name="body">
			Your account info for the MMBase Bugtracker :

			account : <mm:write referid="account" />
			password : <mm:write referid="password" />
		</mm:setfield>
	</mm:createnode>
    </mm:listnodes> 
    <mm:present referid="emailnode">
		<%response.sendRedirect("/bugtracker/jsp/showMessage.jsp?message=email");%>
    </mm:present>
    <mm:present referid="emailnode" inverse="true">
		<%response.sendRedirect("../showMessage.jsp?message=emailnotfound");%>
    </mm:present>
</mm:cloud>
