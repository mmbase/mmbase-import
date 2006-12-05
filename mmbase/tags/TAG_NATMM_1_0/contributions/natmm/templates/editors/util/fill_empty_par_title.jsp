<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%@include file="/taglibs.jsp" %>
<mm:cloud name="mmbase" method="http" rank="basic user" jspvar="cloud">
<html>
<head>
<title>MMBase editors (logged on as <%= cloud.getUser().getIdentifier() %>)</title>
<link rel="stylesheet" type="text/css" href="../css/editorstyle.css">
</head>
<body style="overflow:auto;padding:5px;">
<% 
String [] otype = {  "paragraaf", "artikel" }; 
String [] ofield = { "tekst", 	 "intro" };
for(int i = 0; i<otype.length; i++ ) {
	%> 
	<mm:listnodes type="<%= otype[i] %>" constraints="titel=='' OR titel==' ' OR titel=='  ' OR titel=='    '"
		><mm:field name="<%= ofield[i] %>" jspvar="text" vartype="String" write="false"><% 
			if(text!=null&&!HtmlCleaner.cleanText(text,"<",">","").trim().equals("")) { 
				text = HtmlCleaner.cleanText(text,"<",">","").trim();
				int spacePos = text.indexOf(" ",50); 
				if(spacePos>-1) { 
					 text = text.substring(0,spacePos);
				}
				%><mm:field name="number" /> - <%= text %><br/>
				<mm:setfield name="titel"><%= text %></mm:setfield
				><mm:setfield name="titel_zichtbaar">0</mm:setfield><% 
			} 
		%></mm:field
	></mm:listnodes>
	<%
} %>
</table>
</body>
</html>
</mm:cloud>
