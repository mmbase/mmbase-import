<%@ page language="java" contentType="text/html;charset=utf-8" session="true"
%><%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" 
%><mm:cloud rank="basic user" method="http">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
        "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<title>YAMMEditor - Create node</title>
<script src="yammeditor.js" type="text/javascript"></script>
<link rel="stylesheet" rev="stylesheet" href="yammeditor.css" media="screen" />
</head>
<body>

  <mm:import externid="nr" />
  <mm:import externid="type" />
  <mm:import externid="role" />
  <mm:import externid="action" />

  <mm:present referid="type">
    <h2>Create <mm:nodeinfo nodetype="$type" type="guitype" /> 
      (<mm:write referid="type" />)</h2>
    
    <mm:notpresent referid="action">
      <form enctype="multipart/form-data" method="post" action="<mm:url referids="nr,type,role" />">
      <mm:fieldlist nodetype="$type" type="edit">
        <mm:fieldinfo type="guiname" /><br />
        <mm:fieldinfo type="input" /><br />
      </mm:fieldlist>
      <input type="submit" name="action" value="Save" />
      </form>
    </mm:notpresent>

    <mm:present referid="action">
      <mm:createnode type="$type" id="new_node">
        <mm:fieldlist type="edit"><mm:fieldinfo type="useinput" /></mm:fieldlist>
      </mm:createnode>
      <mm:createrelation source="nr" destination="new_node" role="$role" />
      <p>Your new node of type <mm:nodeinfo nodetype="$type" type="guitype" />
      is saved and related.</p>
    </mm:present>
    
  </mm:present>
<div id="footer"><a href="javascript:closeYAMMeditor();">close window</a></div>
</body>
</html>
</mm:cloud>
