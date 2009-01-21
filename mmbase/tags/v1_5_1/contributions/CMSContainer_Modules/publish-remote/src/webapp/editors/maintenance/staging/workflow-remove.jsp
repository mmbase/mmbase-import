<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@include file="../../publish-remote/globals.jsp"%>
<html>
<head>
    <link href="../style.css" type="text/css" rel="stylesheet"/>
    <title>workflow-remove</title>
</head>
    <body>
       <h2>workflow-remove</h2>
<mm:cloud jspvar="cloud" loginpage="../../editors/login.jsp" rank="administrator">
<mm:log jspvar="log">

<mm:import externid="username"/>
<mm:import externid="status"/>
<form method="post">
	<select name="username">
		<mm:listnodes type="user">
			<option value="${_node.username}">${_node.username}</option>
		</mm:listnodes>
	</select>
	<select name="status">
		<option value="0">draft</option>
		<option value="1">finished</option>
		<option value="2">approved</option>
		<option value="3">published</option>
	</select>
	<input type="submit" value="remove"/>
</form>

<mm:present referid="username">
username:	<mm:write referid="username" /><br />
status:	<mm:write referid="status" /><br />

<mm:listnodescontainer type="user">
   <mm:constraint field="username" operator="EQUAL" referid="username" />
   <mm:listnodes>
     <mm:relatednodescontainer type="workflowitem" role="creatorrel">
        <mm:constraint field="status" operator="EQUAL" value="${status}" />
     	<mm:relatednodes>
     	  workflow <mm:field name="number" /> deleted <br />
     	  <mm:deletenode deleterelations="true" />
     	</mm:relatednodes>
     </mm:relatednodescontainer>
   </mm:listnodes>
</mm:listnodescontainer>
</mm:present>

</mm:log>
</mm:cloud>
      Done!
   </body>
</html>