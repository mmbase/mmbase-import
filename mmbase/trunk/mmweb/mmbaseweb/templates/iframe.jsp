<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" 
%><%@page language="java" contentType="text/html; charset=utf-8"

%><mm:cloud
><%@include file="/includes/getids.jsp" 
%><%@include file="/includes/header.jsp"

%><td class="white" width="100%" height="100%" colspan="2" valign="top">
<mm:list nodes="$page" path="pages,posrel,urls" max="1">
	<iframe src="<mm:field name="urls.url"/>" title="the MMBase mailarchive" width="100%" height="100%" frameborder="0">
	<a href="<mm:field name="urls.url"/>" target="_blank">the MMBase mailarchive</a>
	</iframe>
</mm:list>
</td>
<%@include file="/includes/footer.jsp"
%></mm:cloud>
