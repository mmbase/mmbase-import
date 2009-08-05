<%@page language="java" contentType="text/html;charset=UTF-8"
%><%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"
%><%
String language = (String)request.getAttribute("language");
String did = (String)request.getAttribute("did");
String wizardname = (String)request.getAttribute("wizardname");
String sessionkey = (String)request.getAttribute("sessionkey");
String popupid = (String)request.getAttribute("popupid");
String maxsize = (String)request.getAttribute("maxsize");

String returnMessage = (String)request.getAttribute("returnmessage");
String errorMessage = (String)request.getAttribute("errormessage");
%>
<mm:content type="text/html" expires="0" language="<%=language%>">
<html>
<body bgcolor="white">
<h1>Upload</h1>
<hr />
<%
if (returnMessage!=null) { %>
  <script language="javascript">
    try { // Mac IE doesn't always support window.opener.
      window.opener.doRefresh();
      window.close();
    } catch (e) {}
  </script>
<%} else if (errorMessage!=null) { %>
      <%=errorMessage%><br />
      <a href="<mm:url page="upload.jsp" />?proceed=true&did=<%=did%>&sessionkey=<%=sessionkey%>&wizard=<%=wizardname%>&maxsize=<%=maxsize%>">Try again</a> or
      <a href="javascript:window.close();">abandon upload</a>.
<%} %>
</body>
</html>
</mm:content>
