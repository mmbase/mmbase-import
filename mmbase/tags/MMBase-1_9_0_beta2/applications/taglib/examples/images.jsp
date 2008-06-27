<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "DTD/xhtml1-strict.dtd">
<%@page language="java" contentType="text/html;charset=utf-8" pageEncoding="UTF-8"
%><%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" 
%><mm:content type="text/html" language="en">
<mm:import id="euro">€</mm:import>
<html>
  <head>
    <link rel="stylesheet" type="text/css" href="style.css" />
  </head>
  <body>
    <%@ include file="menu.jsp"%>
    <mm:import externid="node">test.transparency</mm:import>
    <mm:import externid="size">100</mm:import>
    <mm:cloud>
      <h1>Image tag</h1>
      <mm:hasnode number="$node" inverse="true">
        <h2>The test-image with alias '${node}' is not available</h2>
      </mm:hasnode>
      <mm:node number="$node" notfound="skip">
        <table>
          <tr><th colspan="2">The image tag has several modes</th></tr>
          <tr valign="top">
            <td width="50%"><pre><mm:formatter format="escapexml"><mm:include page="codesamples/imagetagmodes.jsp" /></mm:formatter></pre></td>
            <td width="50%"><%@include file="codesamples/imagetagmodes.jsp" %></td>
          </tr>
          <tr><th colspan="2">The most important feature however is the conversion of images</th></tr>
          <tr valign="top">
            <td width="50%"><pre><mm:formatter format="escapexml"><mm:include page="codesamples/imagetagtemplates.jsp" /></mm:formatter></pre></td>
            <td width="50%"><%@include file="codesamples/imagetagtemplates.jsp" %></td>
          </tr>
        </table>
      </mm:node>
    </mm:cloud>
  </body>
</html>
</mm:content>
