<%@page language="java" contentType="text/html; charset=utf-8"%>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%@include file="includes/cleanText.jsp" %>
<%@include file="parcleaner/cleanHtml.jsp" %>


<mm:cloud>

<mm:import id="referrer"><%= new java.io.File(request.getServletPath()) %></mm:import>

<%
String actieId = request.getParameter("actie"); 
if(actieId==null){ actieId = ""; }
String nextId = request.getParameter("next"); 
if(nextId==null){ nextId = ""; }
String parId = request.getParameter("paragraph");
if(parId!=null){ %>

<%	if(actieId.equals("accept")) { %>
		<mm:transaction id="the_transaction" name="another_transaction" commitonclose="true">
		<mm:node number="<%= parId %>">
			<mm:field name="title" jspvar="title" vartype="String" write="false">
				<mm:setfield name="title"><%= cleanHtml(title) %></mm:setfield>
			</mm:field>
			<mm:field name="body" jspvar="body" vartype="String" write="false">
				<mm:setfield name="body"><%= cleanHtml(body) %></mm:setfield>
			</mm:field>
		</mm:node>
		</mm:transaction>
<%	} else if(actieId.equals("remove")) { %>
		<mm:transaction id="the_transaction" name="another_transaction" commitonclose="true">
			<mm:deletenode number="<%= parId %>" deleterelations="true"/>
		</mm:transaction>
		<% parId = nextId; %>
<% } %>

<%	String firstPar = "";
	String prevPar = "";
	int prevParCount = -1;
	String nextPar = "";
	int nextParCount = -1;
	String nextParToChange = "";
	int nextParToChangeCount = -1;
	int totalParCount = 0;
	boolean parFound = false;
%>
	<mm:list path="paragraph" fields="paragraph.number" orderby="paragraph.number" directions="UP"
		><mm:field name="paragraph.number" jspvar="paragraph_number" vartype="String" write="false"
			><%	if(!parFound) {
					if(parId.equals(paragraph_number)) {
						parFound = true;
					} else {
						prevPar = paragraph_number; 
						prevParCount = totalParCount;
					}
				} else if(nextPar.equals("")) {
					nextPar = paragraph_number;
					nextParCount = totalParCount;
					boolean willChange = false; %>
					<mm:node number="<%= paragraph_number %>">
						<mm:field name="title" jspvar="title" vartype="String" write="false">
							<% if(!cleanHtml(title).equals(title)){ willChange = true; } %>
						</mm:field>
						<mm:field name="body" jspvar="body" vartype="String" write="false">
							<% if(!cleanHtml(body).equals(body)){ willChange = true; } %>
						</mm:field>
					</mm:node>
					<% if(willChange){ nextParToChange = paragraph_number; nextParToChangeCount = totalParCount; }
				} else if(nextParToChange.equals("")){ 
					boolean willChange = false; %>
					<mm:node number="<%= paragraph_number %>">
						<mm:field name="title" jspvar="title" vartype="String" write="false">
							<% if(!cleanHtml(title).equals(title)){ willChange = true; } %>
						</mm:field>
						<mm:field name="body" jspvar="body" vartype="String" write="false">
							<% if(!cleanHtml(body).equals(body)){ willChange = true; } %>
						</mm:field>
					</mm:node>
					<% if(willChange){ nextParToChange = paragraph_number; nextParToChangeCount = totalParCount; }
				}
				if(firstPar.equals("")) {
					firstPar = paragraph_number;
				}
				totalParCount++;
			
		%></mm:field
	></mm:list
	><% if(nextPar.equals("")) {
			nextPar = firstPar;
			nextParCount = 1;
	}
	if(nextParToChange.equals("")) {
			nextParToChange = firstPar;
			nextParToChangeCount = 1;
	}
	%>
<html>
<head><link rel="stylesheet" type="text/css" href="css/editors.css">
<SCRIPT LANGUAGE="JavaScript">
<!--
var cancelClick = false;
function doDelete(prompt) {
				var conf;
				if (prompt && prompt!="") {
					conf = confirm(prompt);
				} else conf=true;
				cancelClick=true;
				return conf;
			}
//-->
</SCRIPT>
</head>
<body>
	<table cellpadding="0" cellspacing="0" align="center">
		<tr>
			<td><a target="wizard" href="clean.jsp?paragraph=<%= prevPar %>">vorige (<%= prevParCount %>/<%= totalParCount %>)</a></td>
			<td><a target="_blank" href="<mm:url referids="referrer" page="/mmapps/editwizard/jsp/wizard.jsp">
            											<mm:param name="wizard">wizards/paragraph/paragraph</mm:param>
            											<mm:param name="objectnumber"><%= parId %></mm:param>
            											</mm:url>">editwizard</a></td>
			<td><a target="_blank" href="/mmeditors/jsp/change_node.jsp?node_number=<%= parId %>">jsp-editors</a></td>
			<td><a target="wizard" 
				onclick="return doDelete('Weet u zeker dat u deze paragraaf wilt verwijderen?');" 
				onmousedown="cancelClick=true;"
				href="clean.jsp?paragraph=<%= parId %>&actie=remove&next=<%= nextPar %>">verwijderen (<mm:node number="<%= parId %>"><mm:countrelations /></mm:node>)</a></td>
			<td><a target="wizard" 
				onclick="return doDelete('Weet u zeker dat u paragraaf wilt opschonen?');" 
				onmousedown="cancelClick=true;"
				href="clean.jsp?paragraph=<%= parId %>&actie=accept">accepteren</a></td>
			<td><a target="wizard" href="clean.jsp?paragraph=<%= nextPar %>">volgende (<%= nextParCount %>/<%= totalParCount %>)</a></td>
			<td><a target="wizard" href="clean.jsp?paragraph=<%= nextParToChange %>">volgende wijziging (<%= nextParToChangeCount %>/<%= totalParCount %>)</a></div></td>
		</tr>
		<tr>
			<td><img src="media/spacer.gif" width="140" height="1"></td>
			<td><img src="media/spacer.gif" width="100" height="1"></td>
			<td><img src="media/spacer.gif" width="100" height="1"></td>
			<td><img src="media/spacer.gif" width="140" height="1"></td>
			<td><img src="media/spacer.gif" width="100" height="1"></td>
			<td><img src="media/spacer.gif" width="140" height="1"></td>
			<td><img src="media/spacer.gif" width="250" height="1"></td>
		</tr>
	</table>
</body>
</html>

<% } %>

</mm:cloud>
