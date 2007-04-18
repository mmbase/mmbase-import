<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@include file="../globals.jsp"%>
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
<form method="post">
	<input type="text" name="username" value=""/>
	<input type="submit" value="publish"/>
</form>

<mm:present referid="username">
username:	<mm:write referid="username" /><br />

<mm:listnodescontainer type="user">
   <mm:constraint field="username" operator="EQUAL" referid="username" />
   <mm:listnodes>
     <mm:relatednodescontainer type="workflowitem" role="creatorrel">
        <mm:constraint field="status" operator="EQUAL" value="approved" />
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