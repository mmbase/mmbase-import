<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>

<html>
<head>
<title>Do you like MMBase?</title>
<link rel="stylesheet" type="text/css" href="css/poll.css">
</head>
<body>

<mm:cloud>

<%-- the following part can be used as an include on your page --%>
<% 
String pollNr = request.getParameter("poll"); 
if(pollNr==null){ pollNr="";}
String pollDescription = ""; 
int totalAnswers = 0;
%>


<mm:list nodes="<%= pollNr %>"
	path="poll"
	fields="poll.number,poll.question" >
	<mm:field name="poll.number" jspvar="dummy" vartype="String" write="false">
		<% pollNr = dummy;  %>
	</mm:field>
	<table width="130" cellpadding="0" cellspacing="0" border="0">
	  <tr> 
		<td width="130" height="26"><img alt="" src="media/poll_header.gif" width="130" height="26" alt="" border="0"></td>
	  </tr>
	  <tr> 
		<td background="media/poll_backgr.gif" valign="middle" width="130">
	  	<div class="poll-question" style="margin-top: 5;"><form name="poll<%= pollNr %>" method="post" target="poll<%= pollNr %>">
		<b><mm:field name="poll.question" /></b><br>
		<mm:list nodes="<%= pollNr %>"
			path="poll,posrel,answer"
			fields="posrel.pos,answer.number,answer.answer"
			orderby="posrel.pos">
			<input type="radio" name="antwoord" value="<mm:field name="answer.number" />"> <mm:field name="answer.answer" /><br>
			<% totalAnswers++; %>
		</mm:list>
		<input type="image" value="Kies" onclick="postIt<%= pollNr %>()" src="media/but_verstuur.gif" width="72" height="20">
		</form></div>
		</td>
	  </tr>
	  <tr> 
		<td height="16" width="130" valign="top" align="center"><img alt="" src="media/poll_onder.gif" width="130" height="16"></td>
	  </tr>
	</table>

<% String openComment = "<!--"; %>
<% String closeComment = "//-->"; %>
<script language="JavaScript" type="text/javascript">
<%= openComment %>
// Javascript that is used to control the poll
function postIt<%= pollNr %>() {
  window.open('','poll<%= pollNr %>','height=350,width=490, scrollbars=NO, menubar=0, toolbar=0, status=0, directories=0, resizable=1');
  var antw = "";
  for (i = 0; i < <%= totalAnswers %>; i++) {
	if (document.poll<%= pollNr %>.antwoord[i].checked) {
	  antw = document.poll<%= pollNr %>.antwoord[i].value;
    }
  }
  document.poll<%= pollNr %>.action = "poll_result.jsp?poll=<%= pollNr %>&action=Vote&antw="+antw;
}
<%= closeComment %>
</script>

</mm:list>
<br><br><br>
<a href="readme.txt">Documentation</a><br>
<a href="poll.zip">Sources</a><br>
</body>

</mm:cloud>
