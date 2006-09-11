<%@include file="/taglibs.jsp" %>
<mm:cloud logon="admin" pwd="<%= (String) com.finalist.mmbase.util.CloudFactory.getAdminUserCredentials().get("password") %>" method="pagelogon" jspvar="cloud">
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
	<jsp:include page="06.03.24.migrate.jsp"/>
	<jsp:include page="06.03.30.migrate.jsp"/>
	<jsp:include page="06.04.03.migrate.jsp"/>
	<jsp:include page="06.05.09.migrate.jsp"/>
	<jsp:include page="06.06.05.migrate.jsp"/>
	<jsp:include page="06.06.10.migrate.jsp"/>
	<jsp:include page="06.06.19.migrate.jsp"/>
	<jsp:include page="06.06.20.migrate.jsp"/>
	<jsp:include page="06.06.22.migrate.jsp"/>
	<jsp:include page="06.07.18.migrate.jsp"/>
	<jsp:include page="06.08.24.migrate.jsp"/>
	<jsp:include page="06.08.31.migrate.jsp"/>
	</body>
</html>
</mm:cloud>
