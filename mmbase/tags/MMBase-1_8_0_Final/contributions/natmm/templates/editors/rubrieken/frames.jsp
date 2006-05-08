<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"%>
<mm:cloud method="http" rank="basic user" jspvar="cloud">

<html>
   <head>
   <title>Rubriek Management</title>
   </head>
    <frameset cols="325,*" framespacing="2" frameborder="1">
    <frame src="rubrieken.jsp" name="tree" frameborder="1" scrolling="auto">
    <frame src="../empty.html" name="workpane" frameborder="1" scrolling="auto">
    </frameset>
</html>
</mm:cloud>