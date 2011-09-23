<%@ page language="java" contentType="text/html; charset=utf-8" 
%><%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" 
%><%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" 
%><mm:import id="rank"><%= org.mmbase.util.xml.UtilReader.get("editors.xml").getProperties().getProperty("rank", "basic user")%></mm:import>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
        "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<mm:cloud method="loginpage" loginpage="../login.jsp" rank="$rank">
<mm:import externid="ntype" />
<mm:import externid="action" />
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
  <title>Delete more nodes</title>
  <link rel="stylesheet" href="styles.css" type="text/css" />
<script type="text/javascript" language="javascript">
/* <![CDATA[ */
/* Check all checkboxes */
function markAll() {
	var flag = true;
	for(var i=0; i < document.theform.IDs.length; i++) { document.theform.IDs[i].checked = true; }
}

/* Toggle visibility */
function toggle(targetId){
  if (document.getElementById){
  		target = document.getElementById(targetId);
  			if (target.style.display == "none"){
  				target.style.display = "";
  			} else {
  				target.style.display = "none";
  			}
  	}
}

/* ]]> */
</script>
</head>
<body>
<div id="top">
  [<a href="../index.jsp">back to my_editors</a>] 
  [<a href="<mm:url />">start again</a>] 
  [<a href="<mm:url referids="ntype?" />">reload</a>] 
</div>

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
  <h2>Delete nodes of type <mm:write referid="ntype" /></h2>
  <mm:present referid="action">
  <ol class="message">
  <% 
  String[] nodeIDs = request.getParameterValues("IDs");
  if (nodeIDs != null && nodeIDs.length != 0) {
	  for (int i = 0; i < nodeIDs.length; i++) {
  %>
	<mm:compare referid="action" value="Delete selected">
	  <mm:node number="<%= nodeIDs[i] %>" notfound="skipbody">
		<li>[<%= nodeIDs[i] %>] <mm:function name="gui" /> is deleted.</li>
		<mm:deletenode deleterelations="true" />
	  </mm:node>
	</mm:compare>
  <%
	  }
  }
  %>
  </ol>
  </mm:present><%-- /action --%>

  <mm:import externid="offset">0</mm:import>
  <mm:import externid="max">100</mm:import>
  
  <form action="<mm:url referids="ntype" />" method="post" name="theform" id="theform">
	<mm:fieldlist type="list" nodetype="$ntype"><mm:import id="span" reset="true"><mm:size /></mm:import></mm:fieldlist>
	<mm:listnodescontainer type="$ntype" id="node">
	  <mm:size id="total" write="false" />
	  <mm:maxnumber value="$max" />
	  <mm:offset value="$offset" />
	  <mm:sortorder field="number" direction="DOWN" />
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
	
	<mm:compare referid="total" value="0"><p>No nodes found or left</p></mm:compare>
	
	<input name="action" id="action" type="submit" value="Delete selected" />
	<input name="check" id="check" type="button" value="Check all" onclick="markAll();" />
  </form>
</mm:present><%-- /ntype --%>

</body>
</html>
</mm:cloud>
