<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<mm:import externid="portal" /> 
<mm:import externid="page" /> 
<mm:import externid="person" /> 
<mm:import externid="email" /> 
<mm:import externid="firstname" /> 
<mm:import externid="lastname" /> 
<mm:present referid="email">
<mm:cloud method="delegate" authenticate="class">
<mm:listnodes type="users" constraints="email='$email'" max="1">
	<mm:import id="usernumber"><mm:field name="number" /></mm:import>
</mm:listnodes> 
<mm:notpresent referid="usernumber">
	<mm:info id="password" nodemanager="users" command="newpassword" write="false" />
	<mm:write referid="firstname" jspvar="account_base" vartype="String" write="false">
	<% // check if accoung is already in use
	int i = 0;
	String account = account_base; 
	boolean uniqueAccount = false;
	while(!uniqueAccount) {
		uniqueAccount = true;
		if(i>0) account = account_base + i;
		%><mm:list path="users" constraints="<%= "users.account='" + account + "'" %>" ><%
			uniqueAccount = false;
		%></mm:list><%
		i++;
	}
	%><mm:import id="account"><%= account %></mm:import>
	</mm:write>
	<mm:createnode id="usernode" type="users">
		<mm:setfield name="firstname"><mm:write referid="firstname" /></mm:setfield>
		<mm:setfield name="lastname"><mm:write referid="lastname" /></mm:setfield>
		<mm:setfield name="email"><mm:write referid="email" /></mm:setfield>
		<mm:setfield name="password"><mm:write referid="password" /></mm:setfield>
		<mm:setfield name="account"><mm:write referid="account" /></mm:setfield>
	</mm:createnode>
	<h5>An account was created.</h5>
</mm:notpresent>
<mm:present referid="usernumber">
	<mm:node number="$usernumber">
		<mm:import id="account"><mm:field name="account" /></mm:import>
		<mm:import id="password"><mm:field name="password" /></mm:import>
	</mm:node>
</mm:present>
<mm:createnode id="emailnode" type="email">
	<mm:setfield name="mailtype">1</mm:setfield>
	<mm:setfield name="to"><mm:write referid="email" /></mm:setfield>
	<mm:setfield name="from">info@mmbase.org</mm:setfield>
	<mm:setfield name="subject">Your MMBase Peoplefinder account</mm:setfield>
	<mm:setfield name="body">
		Your account info for the MMBase Peoplefinder :

		account : <mm:write referid="account" /> (only necessary for the Bugtracker)
		password : <mm:write referid="password" />
	</mm:setfield>
</mm:createnode>
	<h5>Your account information is sent to you by email.</h5>
	<a href="<mm:url page="/index.jsp" referids="portal,page,person" />">back to the previous page</a>
</mm:cloud>
</mm:present>
