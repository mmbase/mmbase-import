<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" 
%><%@page language="java" contentType="text/html; charset=utf-8" session="true"

%><mm:cloud
><%@include file="/includes/getids.jsp" 
%><%@include file="/includes/header.jsp"

%>
<td class="white" colspan="2" valign="top">
<h2>Most recent news</h2>
<mm:listnodes type="news" max="99" orderby="number" directions="DOWN">
	<mm:field name="title" /><br>
</mm:listnodes>


<h2>Most recently added image...</h2>
<mm:listnodes type="images" max="1" orderby="number" directions="DOWN">
	<img src="<mm:image template="s(200)" />" alt="" width="200" hspace="10" vspace="10" border="1">
</mm:listnodes>
</td>
<%@include file="/includes/footer.jsp"
%></mm:cloud>
