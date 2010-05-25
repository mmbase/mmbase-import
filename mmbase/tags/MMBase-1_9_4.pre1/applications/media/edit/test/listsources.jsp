<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@page language="java" contentType="text/html;charset=UTF-8" 
%><%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" 
%><?xml version="1.0" encoding="UTF-8"?>
<html>
  <head>
    <title>Test</title>
  </head>
  <body>
  <mm:import id="language">nl</mm:import>
  <mm:locale language="$language">
  <mm:cloud>
 <hr />
  <mm:listnodes type="mediafragments" max="10">
      <mm:field name="title" />:<br />
        issub: <mm:booleanfunction name="issubfragment">Yes!</mm:booleanfunction><br />
      <a href="<mm:field name="url()" />">url</a>,
      <a href="<mm:field name="url(rm)" />">rm-url</a>,
      <a href="<mm:field name="url(asf)" />">asf-url</a>
     <hr />
  </mm:listnodes>
  <mm:listnodes type="mediasources" max="10">
    <mm:function name="mimetype" />:  <mm:function name="format" />
    <ul>
      <mm:listfunction name="urls" jspvar="uc">
        <li><%= ((org.mmbase.applications.media.urlcomposers.URLComposer)uc).getURL() %></li>
      </mm:listfunction>
    </ul>
  </mm:listnodes> 
  </mm:cloud>
  </mm:locale>
  </body>
</html>
