<%@include file="includes/templateheader.jsp" %><%

String templateQueryString = "?w=" +  websiteId + "&p=" + pageId + "&a=" + articleId;

String returnPath = request.getParameter("returnpath"); // get the returnpath from the editwizard
if(returnPath==null){ returnPath = ""; }

String returnPage = "/" + returnPath + "/index.jsp" + templateQueryString; // jump back to where you came from
String url = HttpUtils.getRequestURL(request).substring(0);
url = url.substring(0,url.lastIndexOf("/"));
url = url.substring(0,url.lastIndexOf("/")) + "/" + returnPath + "/";

%><!-- return page = <%= returnPage %>; base href = <%= url %> -->
<base href="<%= url %>">
<jsp:include page="<%= returnPage %>" 
></jsp:include
>