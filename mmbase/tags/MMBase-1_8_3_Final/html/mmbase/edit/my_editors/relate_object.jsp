<% String title = "Relate objects"; %>
<%@ include file="inc_top.jsp" %>
<mm:cloud name="mmbase" jspvar="wolk" method="loginpage" loginpage="login.jsp" rank="$rank">

<mm:context id="relate_node">
<mm:import externid="ntype" jspvar="ntype" required="true" />	<%-- nodetype to relate with --%>
<mm:import externid="nr" 	jspvar="nr"	required="true" />		<%-- originale node (nr) that wants relation --%>
<mm:import externid="dir" 	jspvar="dir" />						<%-- direction of relation --%>
<mm:import externid="rkind" jspvar="rkind" />					<%-- kind of relation --%>
<mm:import externid="rnr" />									<%-- node (nr) to relate with --%>


<% String path1 = ntype;		// Eerst stukje van kruimelpad %>
<%@ include file="inc_head.jsp" %>

<!-- main table -->
<table border="0" cellspacing="0" cellpadding="3">
<tr>
  <td valign="top" width="20%">
	
	<!-- table with back button -->
	<table width="230" border="0" cellspacing="0" cellpadding="3" class="table-left">	
	<tr bgcolor="#EFEFEF">
	  <td width="24" class="right"><a href="<mm:url page="edit_object.jsp" referids="nr" />"><img src="img/mmbase-edit.gif" alt="edit" width="21" height="20" border="0" /></a></td>
	  <td><a href="<mm:url page="edit_object.jsp" referids="nr" />">Back</a> to editing <b><mm:node referid="nr"><mm:nodeinfo type="type" /></mm:node></b> object</td>
	</tr>
	<tr valign="top">
	  <td>&nbsp;</td>
	  <td>
	  <mm:node referid="nr">
		  <mm:fieldlist type="list">
			  <mm:fieldinfo type="guiname" /> <mm:first><b></mm:first> <mm:fieldinfo type="guivalue" /><mm:first></b></mm:first><br />
		  </mm:fieldlist>
	  </mm:node>
	  </td>
	</tr>
	</table>
	<!-- end table back button -->
	<%--	<%@ include file="inc_relations.jsp" %> --%>

  </td>
  <td valign="top" width="80%">
  	<!-- ### Search results -->	
	<mm:notpresent referid="rnr">
	  <mm:compare referid="searchbox" value="after" inverse="true"><%@ include file="inc_searchform.jsp" %></mm:compare>
  	  <%@ include file="inc_searchresults.jsp" %>
  	</mm:notpresent>
  	
  	<!-- ### Relate nodes -->
	<%@ include file="inc_relate.jsp" %>


  </td>
</tr><tr>
  <td width="20%" align="center" valign="top">&nbsp;</td>
  <td width="80%" valign="top">
  	<!-- ### Search form -->
	<mm:notpresent referid="rnr">
	  <mm:compare referid="searchbox" value="after"><%@ include file="inc_searchform.jsp" %></mm:compare>
	</mm:notpresent>
  </td>
</tr>
</table>
<!-- end main table -->

</mm:context>

<%@ include file="inc_foot.jsp" %>
</mm:cloud>
