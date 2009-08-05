<%@page isErrorPage="true"
%><!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1.1-strict.dtd">
<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"
%><%@page language="java" contentType="text/html;charset=utf-8"
%><%@ include file="util/nocache.jsp"
%><mm:import externid="language">nl</mm:import>
<mm:import externid="submit"  />
<mm:content language="$language" postprocessor="reducespace" expires="0">
<html>
  <head>
    <title>Change Password</title>
    <link rel="icon" href="<mm:url id="favi" page="images/edit.ico" />" type="image/x-icon" />
    <link rel="shortcut icon" href="<mm:write referid="favi" />" type="image/x-icon" />
    <link rel="stylesheet" href="css/edit.css" />
  </head>
  <body>
      <h1>Change your password - error</h1>
      <p>Your password could not be changed, either because you didn't confirm your password or entered in wrong.
      </p>
      <a href="<mm:url page="password.jsp" />">Try again</a> or
      <a href="<mm:url page="index.jsp" />">Back to the editors</a>
  </body>
</html>
</mm:content>