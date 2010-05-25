<%@ page language="java" contentType="text/html" %>
<%@ page import="java.util.*" %>
<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>

<%@include file="include/inc_settings.jsp" %>

<html>
<mm:cloud name="mmbase" logon="<%= username %>" pwd="<%= password %>" >

<head>
<% // Get the submitted info
String pollNr = request.getParameter("poll");
String action = request.getParameter("action");
String antw = request.getParameter("antw");

// Declare some variable for future use
String tot_answers = "";	// Total votes for this answers
int tot_general = 0;		// Total number of votes

// Get info from the poll
%>
<mm:list 
	path="poll"
	fields="poll.number"
	max="1">
	<mm:field name="poll.number" jspvar="poll_number" vartype="String" write="false">
		<% if(pollNr==null) { pollNr = poll_number; } %>
	</mm:field>
</mm:list>
	<meta http-equiv="content-type" content="text/html; charset=iso-8859-1">
	<title>Poll Results</title>
<link rel="stylesheet" href="css/poll.css" type="text/css">
</head>
<body bgcolor="#FFFFFF" topmargin="0" leftmargin="0" marginwidth="0" marginheight="0">
<table cellspacing="0" cellpadding="0" border="0">
  <tr> 
    <td colspan="2"><img src="media/popup_header.gif" width="475" height="69" alt="" border="0" usemap="#close"></td>
  </tr><tr>
	<td width="449" bgcolor="#909DF0"> 
	<!-- begin inhoud -->
	<map name="close"> 
	  <area shape="RECT" coords="384,23,450,42" href="javascript:self.close()" alt="Close this window">
	</map>

<div class="poll-answer">
<% // Here we start with processing of what has been submitted
String cookiestr = "poll" + pollNr;
if (action != null && action.equals("Vote")) {
	// Check whether this person already voted by using the cookie
	boolean alreadyvoted = false;
	long timeDelta = 0;
	Cookie[] koekjes = request.getCookies();
	for (int c = 0; c < koekjes.length; c++) {
		String koekje = koekjes[c].getName();
		if (koekje.equals(cookiestr)) {
			timeDelta = (new Date()).getTime() - Long.parseLong(koekjes[c].getValue());			
			out.println("\n<!-- We found our cookie: " + cookiestr + " of age " + timeDelta / 1000 + " seconds -->");
			alreadyvoted = true;
		}
	}
	if (alreadyvoted) {
		out.println("<p>You can only vote once a day!</p>");
	}
	// Check if we have made a choice
	else if (antw != null && !antw.equals("")) {
		// Get total votes for this answer
%>
		<mm:node number="<%= antw %>">
			<mm:field name="total_answers" jspvar="my_total_answers" vartype="String" write="false">
				<% tot_answers = my_total_answers; %>
			</mm:field>
		</mm:node>
<%		// Add 1 to total_answers 
		int ta = Integer.parseInt(tot_answers);
		if(ta<0) ta = 0;
		ta++;
%>
<mm:transaction id="the_transaction" name="another_transaction" commitonclose="true">
	<mm:node number="<%= antw %>">
		<mm:setfield name="total_answers"><%= ta %></mm:setfield>
	</mm:node>
</mm:transaction>
<% 		// Set the cookie
		Cookie koekje = new Cookie(cookiestr, String.valueOf((new Date()).getTime()) );
		int expires = 60 * 60 * 12;	// Cookie expires after 12 hours
		koekje.setMaxAge(expires);		// The maximum age in seconds
		response.addCookie(koekje);
	}
	else {
		out.println("<p>You did not enter a choice!</p>");
	}
}
%>
</div>
<div class="poll-answer">The poll result at this moment:</div>

	<div class="poll-question"><mm:node number="<%= pollNr %>"><mm:field name="question" /></mm:node></div>

	<%
	String[] answer_title = new String[9];
	String[] answer_description = new String[9];
	String[] answer_tot = new String[9];
	int i = 0;
	%>
	<mm:list nodes="<%= pollNr %>"
		path="poll,posrel,answer"
		fields="posrel.pos,answer.number,answer.answer,answer.total_answers,answer.description"
		orderby="posrel.pos">
		<mm:field name="answer.answer" jspvar="my_answer" vartype="String" write="false">
			<% answer_title[i] = my_answer; %>
		</mm:field>
		<mm:field name="answer.description" jspvar="my_answer_descr" vartype="String" write="false">
			<% answer_description[i] = my_answer_descr; %>
		</mm:field>
		<mm:field name="answer.total_answers" jspvar="my_total_answers" vartype="String" write="false">
			<%  if(my_total_answers.equals("-1")) my_total_answers = "0";
				answer_tot[i] = my_total_answers;
			%>
		</mm:field>
		<% 
		tot_general = tot_general + Integer.parseInt(answer_tot[i]); 
		i++; 
		%>
	</mm:list>
	
	<% // Calculations for the chart
	if(tot_general==0) tot_general = 1;
	long[] procent = new long[9];
	long[] width = new long[9];
	for (int j = 0; j < i; j++) {
		double ta = Double.parseDouble(answer_tot[j]);
		double to = (double)tot_general;
		long uitkomst = Math.round((ta/to) * 1000);
		procent[j] = uitkomst / 10;
		width[j] = procent[j] * 3;
	}
	%>
	    <div class="poll-answer-description">Total: <%= tot_general %> (100%)</div>
	<%
	for (int k = 0; k < i; k++) {
	%>
		<div class="poll-answer-description"><b><%= answer_title[k] %>:</b> <%= procent[k] %>% (<%= answer_tot[k] %>)
		<br><img src="media/bar-orange.gif" alt="" width="<%= width[k] %>" height="10" border=0><br>
		<%= answer_description[k] %></div>
	<%
	}
	%>
	 <div class="poll-answer"><mm:node number="<%= pollNr %>"><mm:field name="description" /></mm:node></div>

	<!-- einde inhoud -->
	</td>
	<td width="26" background="media/popup_shadow.gif"><img src="media/spacer.gif" width="1" height="1" alt="" border="0"></td>
  </tr>
  <tr> 
    <td colspan="2"><img src="media/popup_bottom.gif" width="475" height="30" alt="" border="0"></td>
  </tr>
</table>

</body>

</mm:cloud>
</html>
