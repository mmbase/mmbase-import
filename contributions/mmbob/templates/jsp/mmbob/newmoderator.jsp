<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml/DTD/transitional.dtd">
<%@ page contentType="text/html; charset=utf-8" language="java" %>
<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<mm:cloud>
<mm:content type="text/html" encoding="UTF-8" escaper="entities" expires="0">
<%@ include file="thememanager/loadvars.jsp" %>

<mm:import externid="forumid" />
<mm:import externid="postareaid" />

<!-- action check -->
<mm:import externid="action" />
<mm:present referid="action">
 <mm:include page="actions.jsp" />
</mm:present>

<!-- login part -->
  <%@ include file="getposterid.jsp" %>
<!-- end login part -->

<mm:locale language="$lang">
<%@ include file="loadtranslations.jsp" %>

<!-- end action check -->
<html>
<head>
   <link rel="stylesheet" type="text/css" href="<mm:write referid="style_default" />" />
   <title>MMBob</title>
</head>
<body>

<div class="header">
</div>
                                                                                              
<div class="bodypart">

<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 50px;" width="75%">
  <tr><th colspan="3" align="left" ><mm:write referid="mlg.Add_moderator_for" /> : <mm:node number="$postareaid"><mm:field name="name" /></mm:node>
  
  </th></tr>

  <form action="<mm:url page="postarea.jsp">
					<mm:param name="forumid" value="$forumid" />
					<mm:param name="postareaid" value="$postareaid" />
					<mm:param name="admincheck" value="true" />
				</mm:url>" method="post">
	<tr><th align="left"><mm:write referid="mlg.Current_moderators" /></th><td colspan="2" align="left">
  		  <mm:nodelistfunction set="mmbob" name="getModerators" referids="forumid,postareaid">
			<mm:field name="account" /> (<mm:field name="firstname" /> <mm:field name="lastname" />)<br />
		  </mm:nodelistfunction>
	<p />
	</td></tr>
	<tr><th align="left"><mm:write referid="mlg.Possible_moderators" /></th><td colspan="2">
		  <select name="newmoderator">
  		  <mm:nodelistfunction set="mmbob" name="getNonModerators" referids="forumid,postareaid">
				<option value="<mm:field name="id" />"><mm:field name="account" /> (<mm:field name="firstname" /> <mm:field name="lastname" />)<br />
		  </mm:nodelistfunction>
		</select>
	</td></tr>
	<input type="hidden" name="admincheck" value="true">
	<input type="hidden" name="action" value="newmoderator">
	<tr><th>&nbsp;</th><td align="middle" >
	<input type="submit" value="<mm:write referid="mlg.Add" />">
  	</form>
	</td>
	<td align="center">
  	<form action="<mm:url page="postarea.jsp">
	<mm:param name="forumid" value="$forumid" />
	<mm:param name="postareaid" value="$postareaid" />
	</mm:url>"
 	method="post">
	<p />
	<input type="submit" value="<mm:write referid="mlg.Cancel" />">
  	</form>
	</td>
	</tr>

</table>
</div>

<div class="footer">
</div>
                                                                                              
</body>
</html>

</mm:locale>
</mm:content>
</mm:cloud>

