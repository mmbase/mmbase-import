<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%@page language="java" contentType="text/html; charset=iso8859-1" %>
<mm:cloud>

<%@include file="/includes/getids.jsp" %>
<%@include file="/includes/header.jsp" %>
<mm:import id="url">http://www.mmbase.org/cgi-bin/htsearch?<%=request.getQueryString()%></mm:import>
<%-- &<mm:write referid="portal"/></mm:import--%>
<td class="white" colspan="2" valign="top">
<mm:include page="$url" />
</td>

<%@include file="/includes/footer.jsp" %>
</mm:cloud>
