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
  <mm:listnodes type="mediafragments" >
      <mm:field name="title" />:<br />
        issub: <mm:field name="subfragment()" /><br />
      <a href="<mm:field name="url()" />">url</a>,
      <a href="<mm:field name="url(rm)" />">rm-url</a>,
      <a href="<mm:field name="url(asf)" />">asf-url</a>,   
     <hr />
  </mm:listnodes>
  <mm:listnodes type="mediasources">
    <mm:field name="mimetype()" />:     <mm:field name="format()" />
    <br />
    <mm:field name="urls()" />
  </mm:listnodes> 
  </mm:cloud>
  </mm:locale>
  </body>
</html>
