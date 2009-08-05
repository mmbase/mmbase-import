<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" 
%><%@page language="java" contentType="text/html; charset=utf-8"

%><mm:cloud
><%@include file="/includes/getids.jsp" 
%><%@include file="/includes/header.jsp"

%><td class="white" colspan="2" valign="top"><IFRAME SRC="/development/mailarchive/maillist.html" TITLE="The Famous Recipe" width="100%" height="100%" frameborder="0">
<!-- Alternate content for non-supporting browsers -->
<H2>The Famous Recipe</H2>
<H3>Ingredients</H3>
...
</IFRAME>

</td>

<%@include file="/includes/footer.jsp"
%></mm:cloud>
