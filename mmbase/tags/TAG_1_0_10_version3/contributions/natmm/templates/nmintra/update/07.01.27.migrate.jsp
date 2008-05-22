<%@page import="org.mmbase.bridge.*" %>
<%@include file="/taglibs.jsp" %>
<mm:cloud logon="admin" pwd="<%= (String) com.finalist.mmbase.util.CloudFactory.getAdminUserCredentials().get("password") %>" method="pagelogon" jspvar="cloud">
<mm:log jspvar="log">
   <% log.info("07.01.27 start"); %>
   Saving all educations.<br/>
	<mm:listnodes type="educations">
      <mm:setfield name="use_verloopdatum">0</mm:setfield>
   </mm:listnodes>
</mm:log>
</mm:cloud>
