<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "DTD/xhtml1-strict.dtd">
<%@page language="java" contentType="text/html;charset=utf-8" 
%><%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" 
%><mm:content type="text/html">
<html>
<head>
  <title>EditWizard Examples</title>
  <link rel="stylesheet" type="text/css" href="style.css" />
</head>
<body>
  <h1>Editwizard Examples</h1>
  
  <ul>
    <li><a href="<mm:url page="simple/" />">A very simple example</a></li>
    <li><a href="<mm:url page="dutch/" />">Also very simple. Only Shows how to internationalize (to dutch)</a></li>
    <li><a href="<mm:url page="advanced/" />">A more sophisticated example</a></li>
    <li><a href="<mm:url page="startwizard/" />">All kind of 'start-wizards'</a></li>
  </ul>
  <hr />
  <p>
    Lots of these examples edit data of the <a href="<mm:url page="/mmexamples/jsp/mynews" />">My News example</a>. 
  </p>
  </body>
</html>
</mm:content>