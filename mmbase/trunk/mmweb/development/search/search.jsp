<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%@page language="java" contentType="text/html; charset=utf-8" %>
<mm:cloud>
<mm:import externid="words" />
<mm:import externid="sort" />
<mm:import externid="restrict" />
<mm:import externid="method" />
<%@include file="/includes/getids.jsp" %>
<%@include file="/includes/header.jsp" %>
<td class="white" colspan="2" valign="top">
<mm:isnotempty referid="words">
<mm:include page="http://www.mmbase.org:8686/cgi-bin/htsearch.cgi" referids="words,restrict,sort,method">
  <mm:param name="config" value="htdig" />
</mm:include>
</mm:isnotempty>
<mm:isempty referid="words">
  <mm:include page="http://www.mmbase.org:8686/cgi-bin/htsearch.cgi">
    <mm:param name="config" value="htdig" />
    <mm:param name="page" value="htdig" />
  </mm:include>
</mm:isempty>
</td>

<%@include file="/includes/footer.jsp" %>
</mm:cloud>
