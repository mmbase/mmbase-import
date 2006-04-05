<%@page import="java.io.*,java.util.*,org.mmbase.bridge.*" %>
<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"%>
<mm:cloud method="http" rank="basic user" jspvar="cloud">
<mm:log jspvar="log">
<html>
   <head>
   <LINK rel="stylesheet" type="text/css" href="/editors/css/editorstyle.css">
   <title>Natuurmonumenten</title>
   <style>
     table { width: 100%; }
     td { border: solid #000000 1px; padding: 3px; height: auto; vertical-align: top; } 
   </style>
   </head>
   <body style="width:100%;padding:5px;">
	1. Analyzing titels of articles and paragraaf to remove #NZ# string.<br/>
   Processing...<br/>
	<mm:listnodes type="artikel">
		<mm:field name="titel" jspvar="titel" vartype="String" write="false">
			<% if ((titel.indexOf("#NZ#")>-1)||(titel.indexOf("#nz#")>-1)) {
					titel = titel.replaceAll("#NZ#","").trim();
					titel = titel.replaceAll("#nz#","").trim(); %>
					<mm:setfield name="titel"><%= titel %></mm:setfield>
					<mm:setfield name="titel_zichtbaar">0</mm:setfield>
			<% } %>
		</mm:field>
	</mm:listnodes>
	<mm:listnodes type="paragraaf">
		<mm:field name="titel" jspvar="titel" vartype="String" write="false">
			<% if ((titel.indexOf("#NZ#")>-1)||(titel.indexOf("#nz#")>-1)) {
					titel = titel.replaceAll("#NZ#","").trim();
					titel = titel.replaceAll("#nz#","").trim(); %>
					<mm:setfield name="titel"><%= titel %></mm:setfield>
					<mm:setfield name="titel_zichtbaar">0</mm:setfield>
			<% } %>
		</mm:field>
	</mm:listnodes>
   Done.
	</body>
  </html>
</mm:log>
</mm:cloud>
