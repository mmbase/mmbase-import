<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@include file="globals.jsp" %>
<mm:content type="text/html" encoding="UTF-8">
<mm:cloud>
<mm:node number="${param['nodenumber']}">

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
  <title><mm:field name="title" /></title>
  <style type="text/css">
   body { margin: 0px; padding: 0px; }
   img { margin: 0px; padding: 0px; }
  </style>
</head>
<body>
  <mm:image mode="img" />
</body>
</html>

</mm:node>
</mm:cloud>
</mm:content>