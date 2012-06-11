<%@ page language="java" contentType="text/html" session="false" 
%><%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" 
%><%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" 
%><!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN"
  "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<mm:cloud method="loginpage" loginpage="../login.jsp" rank="administrator">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="nl">
<head>
  <meta http-equiv="content-type" content="text/html; charset=utf-8" />
  <title>Changecontext</title>
  <link rel="stylesheet" href="styles.css" type="text/css" />
<script type="text/javascript" language="javascript">
/* <![CDATA[ */
/* Check all checkboxes */
function markAll() {
	for (var i = 0; i < document.theform.IDs.length; i++) {
	    document.theform.IDs[i].checked = true; 
	}
}
/* ]]> */
</script>
</head>
<body>
<h1>Change context of a bunch of nodes</h1>
<p>
  Changes the context of the selected nodes to 'default'. Normally you need to 
  be 'admin' (rank: administrator) to do this, but that depends on your type
  of security of course. If you need another context than 'default' you should
  change this jsp.
</p>
<div id="top">
  [<a href="../index.jsp">back to my_editors</a>] 
  [<a href="<mm:url />">start again</a>] 
  [<a href="<mm:url referids="ntype?" />">reload</a>] 
</div>

<mm:import externid="ntype" />
<mm:import externid="action" />

<mm:notpresent referid="ntype">
  <h2>Please choose a node type</h2>
  <form action="<mm:url />" method="post">
  <fieldset>
  <select id="ntype" name="ntype">
  <mm:listnodescontainer type="typedef">
	<mm:sortorder field="name" direction="UP" />
	<mm:listnodes>
	  <mm:import id="nmname" reset="true"><mm:field name="name" /></mm:import>
	  <option label="<mm:field name="name" />" value="<mm:field name="name" />"
		<mm:compare referid="ntype" value="$nmname">selected="selected"</mm:compare>
	  ><mm:field name="name" /></option>
	</mm:listnodes>
  </mm:listnodescontainer>
  </select>
  <input name="action" id="action" type="submit" value="OK" />
  </fieldset>
  </form>
</mm:notpresent>

<mm:present referid="ntype">
  
  <mm:present referid="action">
	<ol class="message">
	<% 
	String[] nodeIDs = request.getParameterValues("IDs");
	if (nodeIDs != null && nodeIDs.length != 0) {
		for (int i = 0; i < nodeIDs.length; i++) {
	%>
	  <mm:compare referid="action" value="Change selected">
		<mm:node number="<%= nodeIDs[i] %>" notfound="skipbody">
		  <li>[<%= nodeIDs[i] %>] <mm:function name="gui" /> is changed.</li>
		  <mm:setcontext name="default" />
		</mm:node>
	  </mm:compare>
	<%
		}
	}
	%>
	</ol>
  </mm:present><%-- /action --%>

  
  <mm:import externid="offset">0</mm:import>
  <mm:import externid="max">500</mm:import>
  
  <form action="<mm:url referids="ntype" />" method="post" name="theform" id="theform">
	<mm:fieldlist type="list" nodetype="$ntype"><mm:import id="span" reset="true"><mm:size /></mm:import></mm:fieldlist>
	<mm:listnodescontainer type="$ntype" id="node">
	  <mm:size id="total" write="false" />
	  <mm:maxnumber value="$max" />
	  <mm:offset value="$offset" />
	  <mm:listnodes>
		<mm:first>
		<table border="0" cellspacing="0" cellpadding="3">
		<caption>Total of ${total} ${ntype} nodes</caption>
		<tr>
		  <th>#</th>
		  <th>&nbsp;</th>
		  <mm:fieldlist type="list" nodetype="$ntype">
			<th><mm:fieldinfo type="guiname" /></th>
		  </mm:fieldlist>
		</tr>
		</mm:first>
		<tr>
		  <td><mm:index offset="${offset + 1}" /></td>
		  <td><input name="IDs" type="checkbox" value="<mm:field name="number" />" /></td>
		  <mm:fieldlist type="list" nodetype="$ntype">
			<td><mm:fieldinfo type="guivalue" /></td>
		  </mm:fieldlist>
		</tr>
		<mm:last></table></mm:last>
	  </mm:listnodes>
	  
	  <%-- browse pages --%>
	  <div id="browse">
		<mm:url id="baseurl" referids="max,ntype" write="false" />
		<div class="browseleft">
		  <c:if test="${offset != 0}">
			&laquo; <a href="<mm:url referid="baseurl">
			  <mm:param name="offset">${(offset - max)}</mm:param>
			</mm:url>">Previous</a>
		  </c:if>
		</div>
		<div class="browseright">
		  <c:if test="${(total - offset) > max}">
			<a href="<mm:url referid="baseurl">
			  <mm:param name="offset">${(max + offset)}</mm:param>
			</mm:url>">Next</a> &raquo;
		  </c:if>
		</div>
	  </div>
	</mm:listnodescontainer>
	
	<mm:compare referid="total" value="0"><p>No nodes found</p></mm:compare>
	
	<input name="action" id="action" type="submit" value="Change selected" />
	<input name="check" id="check" type="button" value="Check all" onclick="markAll();" />
	<input type="hidden" name="offset" value="${offset}" />
  </form>
</mm:present>


</body>
</html>
</mm:cloud>
