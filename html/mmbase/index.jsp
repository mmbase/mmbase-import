<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "DTD/xhtml1-strict.dtd">
<%@page language="java" contentType="text/html;charset=utf-8" session="false"
%><%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" 
%><mm:content type="text/html">
<html>
  <head>
    <title>Welcome to MMBase</title>
    <link rel="stylesheet" href="<mm:url page="/mmbase/style/css/mmbase.css" />" type="text/css" />
    <link rel="icon" href="<mm:url page="/mmbase/style/images/favicon.ico" />" type="image/x-icon" />
    <link rel="shortcut icon" href="<mm:url page="/mmbase/style/images/favicon.ico" />" type="image/x-icon" />

  </head>
<body  class="basic">
  <mm:cloud rank="basic user">
    <h1><mm:cloudinfo type="mmbaseversion" /></h1>
    <ul>
      <mm:haspage page="edit">
        <li>
          <a href="edit">Generic editors</a>
          <mm:haspage page="edit/my_editors">
            (<a href="edit/my_editors">my editors</a>)
          </mm:haspage>
        </li>
      </mm:haspage>
      <mm:haspage page="admin">
        <li><a href="admin">Admin pages</a></li>
      </mm:haspage>
      <mm:haspage page="security">
        <li><a href="security">Security administration</a></li>
      </mm:haspage>
      <mm:haspage page="mpl-1.0.jsp">
        <li><a href="mpl-1.0.jsp">License</a></li>
      </mm:haspage>
    </ul>
    <hr />
    Logged in as <mm:cloudinfo type="user" /> (<mm:cloudinfo type="rank" />)
  </mm:cloud>
</body>
</html>
</mm:content>
