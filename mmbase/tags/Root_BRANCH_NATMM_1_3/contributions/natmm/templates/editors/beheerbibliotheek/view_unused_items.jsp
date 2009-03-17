<%@page import="org.mmbase.bridge.*,
                 java.util.StringTokenizer,
                 nl.leocms.util.ContentHelper,
                 nl.leocms.util.ContentTypeHelper,
                 java.text.*,
                 java.util.Locale,
                 java.util.ArrayList,
                 nl.leocms.util.PropertiesUtil,
                 nl.leocms.authorization.*,
                 org.mmbase.util.logging.*,nl.leocms.content.*" %>
<%@include file="/taglibs.jsp" %>
<mm:import externid="language">nl</mm:import>
<mm:cloud method="http" rank="basic user" jspvar="cloud">
	<%@include file="settings.jsp" %>
	<html>
   <head>
     <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
     <title>BeheerBibliotheek</title>
     <style>
     <!--
      .erf         { background-color: #C0C0C0; line-height: 100%; font-size: 8pt }
      .normal      { background-color: #CFA0A0; font-size: 8pt }
      body         { font-family: Verdana; font-size: 8pt; background-color: #9DBDD8 }
      td           { font-family: Verdana; font-size: 8pt }
      td.titel     { background-color: #C0C0C0 }
      th           { background-color: #C0C0C0; font-size: 10pt }
      a.th:link    { color: black }
      a.th:visited { color: black }
      a:link       { color: #0000FF }
      a:visited    { color: #0000FF }
      a:hover      { color: #0000FF; text-decoration: none }
      div          { display: none }
      p.paginatitel { font-family: Verdana; font-size: 10pt; font-weight: bold }
      img.button   { cursor: pointer; cursor: hand; border-width=0px; }
      -->
      </style>
      <script language="JavaScript">
         <%@include file="js.jsp" %>
      </script>
   </head>
	<body>
		<form>
		<% boolean show_unused = true;
			searchIsOn = true; %>
		<%@include file="searchresults.jsp" %>
		<% String checked = request.getParameter("popupEditWizards");
         checked = ((checked != null) && (checked.equals("on"))) ? "checked" : ""; %>
      &nbsp;&nbsp;<input type="checkbox" name="popupEditWizards" <%=checked%>>Contentelement openen in popup
      </p>
		</form>
		<form action="../../editors/WizardInitAction.eb" method="post" name="callEditwizardForm">
      <input type="hidden" name="objectnumber"/>
      <input type="hidden" name="returnurl" value="/editors/beheerbibliotheek/view_unused_items.jsp">
   </form>
	</body>
</mm:cloud>
