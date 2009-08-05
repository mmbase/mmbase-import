<%@ page language="java" contentType="text/html;charset=utf-8" session="true"
%><%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" 
%><mm:cloud rank="basic user" method="http" jspvar="cloud">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
        "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<title>YAMMEditor - Search node</title>
<script src="yammeditor.js" type="text/javascript"></script>
<link rel="stylesheet" rev="stylesheet" href="yammeditor.css" media="screen" />
</head>
<body>

  <mm:import externid="nr" jspvar="nr" vartype="String" />
  <mm:import externid="type" />
  <mm:import externid="role" jspvar="role" vartype="String" />
  <mm:import externid="rnr" />
  <mm:import externid="action" />
  <mm:import externid="age">99</mm:import>

  <mm:present referid="type">
    <h2>Search <mm:nodeinfo nodetype="$type" type="guitype" /> (<mm:write referid="type" />)</h2>
    
    <mm:notpresent referid="action">
      <form enctype="multipart/form-data" method="post" action="<mm:url referids="nr,type,role" />">
      <mm:fieldlist nodetype="$type" type="search">
        <mm:fieldinfo type="guiname" /><br />
        <mm:fieldinfo type="searchinput" /><br />
      </mm:fieldlist>
	  Days old<br />
	  <input type="text" class="small" size="80" name="age" value="<mm:write referid="age" />" /><br />
      <input type="submit" name="action" value="Search" />
      </form>
    </mm:notpresent>

    <mm:present referid="action">
    <mm:listnodescontainer type="$type">
	  <mm:context>
		<mm:fieldlist nodetype="$type" type="search">
		  <mm:fieldinfo type="usesearchinput" /><%-- 'usesearchinput' can add constraints to the surrounding container --%>
		</mm:fieldlist>             
	  </mm:context>
  	  <mm:ageconstraint minage="0" maxage="$age" />
	  <mm:listnodes	id="node_number"
		directions="DOWN" orderby="number">
  	    <mm:first>
  	    <table border="0" width="100%" cellspacing="0" cellpadding="0">
  	    <tr>
  	      <td class="path" colspan="3">
  	        <span class="fltleft"><mm:size /> <mm:nodeinfo nodetype="$type" type="type" /> found to relate to <mm:node number="$nr"><mm:function name="gui" /></mm:node></span>
  	        <span class="fltright"><a href="<mm:url referids="nr,type,role" />">new search</a></span>
  	      </td>
  	    </tr>
  	    </mm:first>
		<tr>
		  <td class="rel0">
		  
		  <mm:maycreaterelation source="nr" destination="node_number" role="$role">
			<a title="relate" href="<mm:url referids="nr,type,role"><mm:param name="rnr"><mm:field name="number" /></mm:param></mm:url>"><img src="/mmbase/edit/my_editors/img/mmbase-relation-right.gif" alt="-&gt;" width="21" height="20" border="0" /></a>
		  </mm:maycreaterelation>
		  <mm:maycreaterelation source="node_number" destination="nr" role="$role">
			<a title="relate" href="<mm:url referids="nr,type,role"><mm:param name="rnr"><mm:field name="number" /></mm:param></mm:url>"><img src="/mmbase/edit/my_editors/img/mmbase-relation-right.gif" alt="-&gt;" width="21" height="20" border="0" /></a>
		  </mm:maycreaterelation>
		  <strong><mm:function name="gui" /></strong>
		  [nodenr: <mm:field name="number" />]
		  
		  </td>
		</tr>
		<mm:last></table></mm:last>
	  </mm:listnodes>

    </mm:listnodescontainer>
    </mm:present>
    
	<mm:present referid="rnr">
	  <mm:createrelation source="nr" destination="rnr" role="$role" />
	  <p class="message">Message: Your found node of type <mm:nodeinfo nodetype="$type" type="guitype" /> is related.</p>
	  
	</mm:present>
    
  </mm:present><%-- /type --%>
<div id="footer"><a href="javascript:closeYAMMeditor();">close window</a></div>
</body>
</html>
</mm:cloud>
