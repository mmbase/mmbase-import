<%@include file="../../includes/templateheader.jsp" %>
<%
	String filterId = request.getParameter("filter"); 
%>
<%	String imageFilter = "s(100)"; 
	if(filterId!=null) imageFilter =  filterId + "+" + imageFilter; 
%>

<html>
<head></head>
<body>
USED FILTER: <%= imageFilter %><br>
<table>
<tr>
	<mm:list path="employees,images" fields="images.number" max="3">
	<%	String imageTemplate = imageFilter; %>
	<td><img src=<%@include file="../../includes/imagessource.jsp" %> alt="" border="0" ></td>
	</mm:list>
</tr>
</table>
</body>
<%@include file="imagemagick.txt" %>
<%@include file="../../includes/templatefooter.jsp" %>
