<%
String templateQueryString = "";
if(!pageId.equals("")){ templateQueryString += "?p=" + pageId; } 
if(!articleId.equals("")){ templateQueryString += "&article=" + articleId; }
if(!categoryId.equals("")){ templateQueryString += "&category=" + categoryId; }
if(!projectId.equals("")){ templateQueryString += "&project=" + projectId; }
if(!educationId.equals("")){ templateQueryString += "&e=" + educationId; }

String imageTemplate = "";

%><%@include file="getstyle.jsp" %>