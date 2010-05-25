<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" 
%><mm:cloud method="delegate" authenticate="class">

  <%@include file="parameters.jsp" %>

  <mm:import externid="newaccount"   required="true"/>
  <mm:import externid="newfirstname" required="true"/>
  <mm:import externid="newlastname"  required="true"/>
  <mm:import externid="newemail"     required="true"/>


  <!-- first check if all fields where entered -->
  <mm:url id="infoerrorpage" referids="parameters,$parameters" write="false"><mm:param name="btemplate" value="newUser.jsp" /><mm:param name="error" value="info" /></mm:url>
	<mm:isempty referid="newaccount"><mm:redirect referid="infoerrorpage" /></mm:isempty>
	<mm:isempty referid="newfirstname"><mm:redirect referid="infoerrorpage" /></mm:isempty>
	<mm:isempty referid="newlastname"><mm:redirect referid="infoerrorpage" /></mm:isempty>


	<!-- check if email allready has a account and warn the user -->	
  <mm:url id="emailerrorpage" referids="parameters,$parameters" write="false"><mm:param name="btemplate" value="newUser.jsp" /><mm:param name="error" value="email" /></mm:url>
	<mm:listnodes type="users" constraints="email='$newemail'" max="1">
    <mm:redirect referid="emailerrorpage" />
	</mm:listnodes>


	<!-- check of the account name is allready in use -->
  <mm:url id="accounterrorpage" referids="parameters,$parameters" write="false"><mm:param name="btemplate" value="newUser.jsp" /><mm:param name="error" value="account" /></mm:url>
	<mm:listnodes type="users" constraints="account='$newaccount'" max="1">
    <mm:redirect referid="accounterrorpage" />
  </mm:listnodes>



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

  <%--//IF MMBASE_ORG
  <mm:createnode id="personsnode" type="persons">
    <mm:setfield name="firstname"><mm:write referid="newfirstname" /></mm:setfield>
    <mm:setfield name="lastname"><mm:write referid="newlastname" /></mm:setfield>
    <mm:setfield name="email"><mm:write referid="newemail" /></mm:setfield>
  </mm:createnode>
  <mm:createrelation role="related" source="usernode" destination="personsnode" />
  //FI MMBASE_ORG--%>


	<!-- send a email with the account info -->
	<mm:createnode id="emailnode" type="email">
		<mm:setfield name="mailtype">1</mm:setfield>
		<mm:setfield name="to"><mm:write referid="newemail" /></mm:setfield>
		<mm:setfield name="from">bugtracker@mmbase.org</mm:setfield>
		<mm:setfield name="subject">Your MMBase BugTracker account</mm:setfield>
		<mm:setfield name="body">
			Your account info for the MMBase Bugtracker :

			account : <mm:write referid="newaccount" />
			password : <mm:write referid="newpassword" />
		</mm:setfield>
  </mm:createnode>
   <mm:node referid="emailnode">
     <mm:field name="mail(oneshot)" />
   </mm:node>
	<!-- end of sending the email -->

  <mm:redirect referids="parameters,$parameters">
    <mm:param name="btemplate" value="showMessage.jsp" />
    <mm:param name="message" value="newuser" />
  </mm:redirect>

</mm:cloud>
