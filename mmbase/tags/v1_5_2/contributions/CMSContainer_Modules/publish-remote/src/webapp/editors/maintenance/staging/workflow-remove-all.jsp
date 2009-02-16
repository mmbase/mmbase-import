<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@include file="../globals.jsp"%>
<html>
<head>
    <link href="../style.css" type="text/css" rel="stylesheet"/>
    <title>workflow-remove-all</title>
</head>
    <body>
       <h2>workflow-remove-all</h2>
<mm:cloud jspvar="cloud" loginpage="../../editors/login.jsp" rank="administrator">
<mm:log jspvar="log">

<mm:import externid="username"/>
This script removes all the workflow and publishqueue items. Useful e.g. if 
you like to remove the workflow modules from a project. <br/>
<form method="post">
   <input type="submit" value="remove all workflow items"/>
   <input type="hidden" name="username"/>
</form>

<mm:present referid="username">
   Removing workflow items<br/>
   <mm:listnodes type="workflowitem">
      Workflow: <mm:field name="number" /> deleted <br />
      <mm:deletenode deleterelations="true"/>
   </mm:listnodes>
   
   Removing publishqueue items<br/>
   <mm:listnodes type="publishqueue">
      Publishqueue: <mm:field name="number" /> deleted <br />
      <mm:deletenode deleterelations="true"/>
   </mm:listnodes>
</mm:present>

</mm:log>
</mm:cloud>
      Done!
   </body>
</html>