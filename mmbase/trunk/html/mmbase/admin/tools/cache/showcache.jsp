<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "DTD/xhtml1-strict.dtd">
<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"
%><%@page import="org.mmbase.bridge.*,org.mmbase.cache.Cache,java.util.*,java.util.regex.*,org.mmbase.storage.search.implementation.database.BasicSqlHandler,org.mmbase.storage.search.SearchQuery" 
%><%@include file="../../settings.jsp" %>
<mm:content expires="0">
<mm:cloud method="$method" authenticate="$authenticate" rank="administrator" jspvar="cloud">
<html xmlns="http://www.w3.org/TR/xhtml">
  <head>
    <title>Cache Monitor, Multi Level Cache</title>
    <link rel="stylesheet" type="text/css" href="<mm:url page="/mmbase/style/css/mmbase.css" />" />
  </head>
  <mm:import externid="cache" jspvar="cacheName" vartype="String" required="true" />
  <mm:import externid="deleteentry" jspvar="deleteentry" vartype="integer">-1</mm:import>
  <mm:import externid="deletekey"   jspvar="deletekey" vartype="string" />
  
<body class="basic" >

<form action="<mm:url referids="cache" />" method="post">
<table summary="applications">
<%
	BasicSqlHandler sqlHandler = new BasicSqlHandler();

 Cache cache = Cache.getCache(cacheName);
 if (cache != null) {
%>

<tr>
  <th class="header" colspan="5">Cache Monitor</th>
</tr>
<tr>
  <td class="multidata" colspan="5"><%= cache.getDescription() %> Cache - first 500 of <%= cache.size() %> entries</td>
</tr>
<mm:import externid="key" jspvar="key">.*</mm:import>
<mm:import externid="value" jspvar="value">.*</mm:import>
<tr>
  <th class="header">Position</th>
  <th class="header">Count</th>
  <th class="header">Key<input type="text" name="key" value="<mm:write referid="key" />" /></th>
  <th class="header">Value<input type="text" name="value" value="<mm:write referid="value" />" /></th>
  <th class="header"><input type="submit" value="search" /></th>
</tr>
<%

synchronized(cache) {
   Iterator i = cache.entrySet().iterator();
   int j = 0;
   Pattern keyPattern = Pattern.compile(key);
   Pattern valuePattern = Pattern.compile(value);
   int deleted = 0;  
   while(i.hasNext() && j < 500 + deleted) {
     Map.Entry entry = (Map.Entry) i.next();
     String k = entry.getKey() instanceof SearchQuery ? sqlHandler.toSql((SearchQuery)entry.getKey(), sqlHandler) : entry.getKey().toString();
     String v = "" + entry.getValue();
     if(!keyPattern.matcher(k).matches()) continue;
     if(!valuePattern.matcher(v).matches()) continue;
     try {
       if(deleteentry.intValue() == j && deletekey.equals(k)) {
         i.remove();
         j++;
         continue;
       }
     } catch (Exception e) { 
     %>
     <tr><td class="data" colsan="5">ERROR<%=e.toString() %></td></tr>
<%
     }
%>
<tr>
  <td class="data"><%=j%></td>
  <td class="data"><%=cache.getCount(entry.getKey())%></td>
  <td class="data">
	<p><%=k%></p>
 </td>
  <td class="data"><%=v%></td>
  <td class="data"><a href="<mm:url referids="cache"><mm:param name="deletekey" value="<%=k%>" /><mm:param name="deleteentry" value="<%="" + j%>" /></mm:url>">remove</a></td>
  <% j++; %>
</tr>
<% } }%>

<tr><td>&nbsp;</td></tr>
<% } %>
<tr class="footer">
<td class="navigate"><a href="<mm:url page="../cache.jsp" />"><img src="<mm:url page="/mmbase/style/images/back.gif" />" alt="back" border="0" /></td>
<td class="data" colspan="3">Return to Cache Monitor</td>
</tr>
</table>
</form>
</body>
</html>
</mm:cloud>
</mm:content>
