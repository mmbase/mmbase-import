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
        <mm:cloud logon="admin" pwd="admin2k">
       <hr />
        <mm:listnodes type="mediafragments">
          <mm:field name="title" />: <br />
            urls:  <mm:field name="urls()" /> <br />
            issub: <mm:field name="subfragment()" /> <br />
            count: <mm:countrelations type="mediasources" /> / <mm:countrelations type="mediafragments" /> /  <mm:countrelations /><br />
           <hr />
        </mm:listnodes> 
        </mm:cloud>
        </mm:locale>
        </body>
      </html>
