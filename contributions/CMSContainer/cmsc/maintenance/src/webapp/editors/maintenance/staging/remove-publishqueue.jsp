<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@include file="../globals.jsp"%>
<mm:cloud method="http">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
</head>
<body>

<mm:import externid="nodenumber" />
<mm:import externid="action" />
<mm:import externid="status" />


<form method="post">
	<input type="text" name="nodenumber" value="${nodenumber}"/>
	<select name="status">
		<option value="fail">Fail</option>
		<option value="init">Init</option>
		<option value="done">Done</option>
	</select>
	<input type="submit" value="show"/>
</form>

<mm:present referid="nodenumber">
<form method="post">
	<input type="hidden" name="nodenumber" value="${nodenumber}"/>
	<input type="hidden" name="status" value="${status}"/>
	<input type="hidden" name="action" value="delete"/>
	<input type="submit" value="delete all"/>
</form>


<mm:listnodescontainer type="publishqueue">
<mm:constraint field="sourcenumber" operator="EQUAL" referid="nodenumber" />
<mm:constraint field="status" operator="EQUAL" value="${status}" />
nodenumber:	<mm:write referid="nodenumber" /><br />
status	<mm:write referid="status" /><br />
size: <mm:size /><br />
<br />
<mm:listnodes>

<mm:field name="number" />
<mm:field name="action" />
<mm:field name="destinationcloud" />

<mm:present referid="action">
<mm:compare referid="action" value="delete">
<mm:deletenode /> deleted
</mm:compare>
</mm:present>
<br />
</mm:listnodes>
</mm:listnodescontainer>

</mm:present>

</body>
</html>

</mm:cloud>