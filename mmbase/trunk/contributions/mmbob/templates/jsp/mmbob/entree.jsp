<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml/DTD/transitional.dtd">
<%@ page contentType="text/html; charset=utf-8" language="java" %>
<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<mm:cloud>
<mm:content type="text/html" encoding="UTF-8" escaper="entities" expires="0">
<mm:import externid="forumid" />
<%@ include file="thememanager/loadvars.jsp" %>


<!-- login part -->
  <%@ include file="getposterid.jsp" %>
<!-- end login part -->
<mm:import id="entree" reset="true"><%= request.getHeader("aad_nummer") %></mm:import>
<mm:nodefunction set="mmbob" name="getForumInfo" referids="forumid,posterid">
        <mm:import id="hasnick"><mm:field name="hasnick" /></mm:import>
</mm:nodefunction>
                                                                                                                    
<mm:locale language="$lang">
<%@ include file="loadtranslations.jsp" %>

<html>
<head>
   <link rel="stylesheet" type="text/css" href="<mm:write referid="style_default" />" />
   <title>MMBob</title>
</head>
<body>

<div class="header">
    <mm:import id="headerpath" jspvar="headerpath"><mm:function set="mmbob" name="getForumHeaderPath" referids="forumid"/></mm:import>
    <jsp:include page="<%=headerpath%>"/>
</div>
                                                                                              
<div class="bodypart">
</div>

<div class="body">
	<mm:compare referid="entree" value="null">
	<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 50px;" width="75%">
  		<tr><th width="25%" align="left">Entree niet gevonden</th><th align="left"><a href="/mmbob/index.jsp?forumid=<mm:write referid="forumid" />">ok</a></th></tr>	
        </table>
	</mm:compare>
	<mm:compare referid="entree" value="null" inverse="true">
        <mm:import id="ea"><%= request.getHeader("sm_user") %></mm:import>
        <mm:nodefunction set="mmbob" name="getPosterPassword" referids="forumid,ea@account">
		<mm:field name="failed">
		<mm:compare value="false">
			<mm:import id="ep"><mm:field name="password" /></mm:import>
			<mm:write referid="ea" cookie="caf$forumid" />
			<mm:write referid="ep" cookie="cwf$forumid" />
			<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 50px;" width="75%">
  				<tr><td width="25%" align="left"><center>Je bent nu ingelogged op het forum</center></td></tr><tr><th align="left"><form action="/mmbob/index.jsp?forumid=<mm:write referid="forumid" />" method="post"><center><input type="submit" value="ok" /></center></form></th></tr>	
        		</table>
		</mm:compare>
		<mm:compare value="true">
		        <form action="<mm:url page="newposter.jsp" referids="forumid" />" method="post">
			<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 50px;" width="50%">
			<tr><th width="150" ><mm:write referid="mlg.Account"/></th><td>
        			<input type="hidden" name="newaccount" value="<%= request.getHeader("sm_user") %>">
				<%= request.getHeader("sm_user") %>
				<mm:import id="np"><%=(new org.mmbase.util.PasswordGenerator()).getPassword()%></mm:import>
        			<input type="hidden" name="newpassword" value="<mm:write referid="np" />">
        			<input type="hidden" name="newconfirmpassword" value="<mm:write referid="np" />">
			</td></tr>
			<mm:compare referid="hasnick" value="true">
			<tr><th width="150" >Nick</th><td>
				<input name="newnick" value="" style="width: 100%" />
			</td></tr>
			</mm:compare>
			<tr><th><mm:write referid="mlg.Firstname"/></th><td>
        			<input type="hidden" name="newfirstname" value="<%= request.getHeader("aad_voornaam") %>">
				<%= request.getHeader("aad_voornaam") %>
				</td></tr>
			<tr><th><mm:write referid="mlg.Lastname"/></th><td>
				<mm:import id="tan"><%= request.getHeader("aad_achternaam") %></mm:import>
				<mm:compare referid="tan" value="null" inverse="true">
        			<input type="hidden" name="newlastname" value="<%= request.getHeader("aad_achternaam") %>">
				<%= request.getHeader("aad_achternaam") %>
				</td></tr>
				</mm:compare>
				<mm:compare referid="tan" value="null">
        			<input type="hidden" name="newlastname" value="   ">
				missing
				</td></tr>
				</mm:compare>
			<tr><th><mm:write referid="mlg.Email"/></th><td>
				<input name="newemail" style="width: 100%" value="<%= request.getHeader("aad_emailadres") %>" />
				</td></tr>
			<tr><th><mm:write referid="mlg.Location"/></th><td>
				<input name="newlocation" value="" style="width: 100%" />
				</td></tr>
			<tr><th><mm:write referid="mlg.Gender"/></th><td>
				<select name="newgender">
				<option value="male"><mm:write referid="mlg.Male"/>
				<option value="female"><mm:write referid="mlg.Female"/>
				</select>
			</td></tr>
		        <tr><th colspan="2">
        		<input type="hidden" name="action" value="createposter">
		        <center><input type="submit" value="<mm:write referid="mlg.Save"/>"></center>
        		</form>
		</table>
		</mm:compare>
		</mm:field>
	</mm:nodefunction>
	</mm:compare>
</div>

<div class="footer">
    <mm:import id="footerpath" jspvar="footerpath"><mm:function set="mmbob" name="getForumFooterPath" referids="forumid"/></mm:import>
    <jsp:include page="<%=footerpath%>"/>
</div>  
                                                                                              
</body>
</html>

</mm:locale>
</mm:content>
</mm:cloud>

