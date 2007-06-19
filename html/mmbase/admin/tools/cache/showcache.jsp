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
  
<body class="basic" >

  <jsp:directive.include file="/mmbase/components/core/cache/showcache.jsp" />
</body>
</html>
</mm:cloud>
</mm:content>
