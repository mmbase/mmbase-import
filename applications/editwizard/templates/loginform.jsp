<%@ include file="settings.jsp"
%><%@ page import="org.mmbase.applications.editwizard.*" %>
<%

	response.addHeader("Expires","-1");

	String username = request.getParameter("username");
	if (username == null){username = "";}
	
%>
<html>
<head>
	<title>Edit Wizard</title>
	<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
	<link rel="stylesheet" href="style.css" type="text/css" />
</head>
<body bgcolor="#ffffff" text="#000000" link="#18248C" vlink="#00046C" alink="#3844AC" onload="document.forms[0].elements['username'].focus();">
<form name="form1" method="post" action="login.jsp">
<%
	String notHiddenNames = "|username|password|messageCode|";
	java.util.Enumeration names = request.getParameterNames();
	while (names.hasMoreElements()){
		String name = (String)names.nextElement();
		if (notHiddenNames.indexOf("|" + name + "|") == -1){
%>
		<input type="hidden" name="<%= name %>" value="<%= request.getParameter(name) %>" />
<%
		}
	}
%>
  <p><table border="0" cellspacing="0" cellpadding="7" bgcolor="#F4E6DA">
    <tr> 
      <td colspan="1" class="head">Edit Wizard</td>
      <td colspan="3" class="superhead" align="right"> 
        <div align="right">Log in</div>
      </td>
    </tr>
    <tr>
      <td colspan="4" class="divider"><p><img src="media/n.gif" width="16" height="16" /></p></td>
    </tr>
    <tr>
      <td class="divider" align="right"> 
        <p><img src="media/n.gif" width="16" height="16" />Gebruikersnaam:</b></p>
      </td>
      <td colspan="2" class="divider"> 
        <p> 
          <input type="text" name="username" value="<%= username %>">
        </p>
      </td>
      <td class="divider" align="right"> 
        <p><img src="media/n.gif" width="16" height="16" /></p>
      </td>
    </tr>
    <tr> 
      <td class="divider" align="right"> 
        <p><img src="media/n.gif" width="16" height="16" />Wachtwoord:</p>
      </td>
      <td colspan="2" class="divider"> 
        <p> 
          <input type="password" name="password" >
        </p>
      </td>
      <td class="divider" align="right"> 
        <p><img src="media/n.gif" width="16" height="16" /></p>
      </td>
    </tr>
<% 
	String messageCode = request.getParameter("messageCode");
	if (messageCode != null && messageCode.equals("INVALID_LOGIN")){
%>
	<tr>
      <td colspan="4" class="divider"><p><img src="media/n.gif" width="16" height="32" /></p></td>
    </tr>
    <tr> 
      <td colspan="4" class="xnotvalid">
        <center>
          <b>U hebt geen correcte gebruikersnaam of wachtwoord ingevoerd.</b>
		</center>
      </td>
    </tr>
<%
	}
%>
    <tr> 
      <td colspan="4" class="head" align="right"><input class="submitbutton" type="submit" value="&gt; Log in"></input></td>
    </tr>
  </table></p>
</form>
<p> </p>
</body>
</html>
