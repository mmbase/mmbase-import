<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml/DTD/transitional.dtd">
<%@ page contentType="text/html; charset=utf-8" language="java" %>
<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<mm:cloud rank="administrator">
<%@ include file="thememanager/loadvars.jsp" %>
<html>
  <head>
    <link rel="stylesheet" type="text/css" href="<mm:write referid="style_default" />" />
    <title>MMBase Package Manager</title>
  </head>
  <mm:import externid="main" >bundles</mm:import>
  <mm:import externid="sub" >none</mm:import>
  <mm:import externid="id" >none</mm:import>
  <mm:import externid="help" >on</mm:import>
  
  
  
  <body onload="doLoad()">
  <!-- first the selection part -->
  <center>
    <table cellpadding="0" cellspacing="0" class="list" style="margin-top : 30px;" width="95%">      
      <tr>        
        <th colspan="8">
          MMBase Package Manager - version 0.74 (1.8)
        </th>
      </tr>
    </table>

    <%@ include file="headers/main.jsp" %>
    <mm:compare referid="help" value="on">
      <%@ include file="help/main.jsp" %>
    </mm:compare>
    <mm:write referid="main">
      <mm:compare value="bundles"><%@ include file="bundles/index.jsp" %></mm:compare>
      <mm:compare value="packages"><%@ include file="packages/index.jsp" %></mm:compare>
      <mm:compare value="providers"><%@ include file="providers/index.jsp" %></mm:compare>
      <mm:compare value="sharing"><%@ include file="sharing/index.jsp" %></mm:compare>
      <mm:compare value="manual"><%@ include file="manual/index.jsp" %></mm:compare>
      <mm:compare value="settings"><%@ include file="settings/index.jsp" %></mm:compare>
    </mm:write>
  </center>
</body>
</html>    
</mm:cloud>
