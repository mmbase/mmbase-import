<%@include file="/taglibs.jsp" %>
<html>
<head>
<link href="<mm:url page="<%= editwizard_location %>"/>/style/color/wizard.css" type="text/css" rel="stylesheet"/>
<link href="<mm:url page="<%= editwizard_location %>"/>/style/layout/wizard.css" type="text/css" rel="stylesheet"/>
</head>
<body style="overflow:auto;">
   Request url saved as application attribute by top1_params.jsp:
   <%= application.getAttribute("request_url") %>
</body>
</html>