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
        <mm:list path="pools,mediafragments" max="1">
            <mm:node element="mediafragments">
              <mm:nodeinfo type="gui" />
            </mm:node>
             <br />
         </mm:list> 
              </mm:cloud>
         </mm:locale>
        </body>
      </html>
