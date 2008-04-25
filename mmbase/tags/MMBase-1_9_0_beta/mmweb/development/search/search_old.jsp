<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%@page language="java" contentType="text/html; charset=iso8859-1" %>
<mm:cloud>

<%@include file="/includes/getids.jsp" %>
<%@include file="/includes/header.jsp" %>
<td class="white" colspan="2" valign="top">
<h1>WWW Site Search</h1>
<hr />
This search will allow you to search the contents of
all the publicly available WWW documents at this site.
<br />
<p>
<form method="get" action="<mm:url page="/development/search/search_results.jsp" />">
<font size="-1">
Match: <select name="method">
<option value="and">All</option>
<option value="or">Any</option>
<option value="boolean">Boolean</option>
</select>
Format: <select name="format">
<option value="builtin-long">Long</option>
<option value="builtin-short">Short</option>
</select>
Sort by: <select name="sort">
<option value="score">Score</option>
<option value="time">Time</option>
<option value="title">Title</option>
<option value="revscore">Reverse Score</option>
<option value="revtime">Reverse Time</option>
<option value="revtitle">Reverse Title</option>
</select>
<%--
Choose portal: <select name="restrict">
  <option value="" selected>All portals</option>
  <option value="/index.jsp?portal=199">Development portal</option>
  <option value="/index.jsp?portal=202">MMBase portal</option>
  <option value="/index.jsp?portal=205">Foundation portal</option>
</select>
--%>
Search in: 
 <select name="exclude">
  <option value="" selected>Whole site</option>
  <option value="/development/mailarchive">Not in mail archives</option>
 </select> 
</font>
<input type="hidden" name="config" value="htdig" />
<input type="hidden" name="exclude" value="" />
<input type="hidden" name="portal" value="<mm:write referid="portal"/>" />
<br>
Search:
<input type="text" size="30" name="words" value="" />
<input type="submit" value="Search" />
</form>
<hr noshade size="4">
</td>

<%@include file="/includes/footer.jsp" %>
</mm:cloud>
