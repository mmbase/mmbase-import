<% String title = "Home"; %>
<%@ include file="inc_top.jsp" %>
<mm:cloud name="mmbase" jspvar="wolk" method="loginpage" loginpage="login.jsp" rank="$rank">
<mm:import externid="ntype" jspvar="ntype" />
<mm:import externid="nr" />
<% String path1 = ntype;		// Eerst stukje van kruimelpad %>
<%@ include file="inc_head.jsp" %>
<table border="0" cellspacing="0" cellpadding="3">
<tr>
<td valign="top" width="20%">
<!-- Choose a node type -->
<table width="230" border="0" cellspacing="0" cellpadding="3" class="table-left">
<tr bgcolor="#CCCCCC">
  <td colspan="3" align="center"> List of <b><mm:write referid="conf_list" /></b> node types (<a href="config.jsp">configure</a>)</td> 
</tr><tr bgcolor="#CCCCCC">
  <td align="right">

<mm:import from="cookie" id="conf_sort" externid="my_editors_sort">nu</mm:import>
<mm:import externid="sort" jspvar="sort"><mm:write referid="conf_sort" /></mm:import>
<mm:present referid="sort">
  <mm:write cookie="my_editors_sort" referid="sort" />
</mm:present>

<%
String nsort = sort;
String gsort = sort;
/*
sort:
nu = name up
nd = name down
gu = guiname up
gd = guiname down
*/
if (sort.equals("nd") || sort.equals("gd")) nsort = "nu"; 
if (sort.equals("nu") || sort.equals("gu")) nsort = "nd"; 
if (sort.equals("gd") || sort.equals("nd")) gsort = "gu";
if (sort.equals("gu") || sort.equals("nu")) gsort = "gd";
%>  
  sort by <a href="<mm:url page="index.jsp">
    <mm:param name="sort"><%= nsort %></mm:param>
  </mm:url>">name</a>
  </td>
  <td> <a href="<mm:url page="index.jsp">
    <mm:param name="sort"><%= gsort %></mm:param>
  </mm:url>">guiname</a> </td>
  <td>&nbsp;</td>
</tr>
<% // Choose a node type
int j = 0;
Map nmMap = new HashMap();
List nameList = new ArrayList();
List nmNrList = new ArrayList();	// list to build menu

NodeManagerList nml = wolk.getNodeManagers();
Collections.sort(nml);
if (sort.equals("gd")) Collections.reverse(nml);

NodeManagerIterator nmi = nml.nodeManagerIterator();
while (nmi.hasNext()) {
	NodeManager nm = nmi.nextNodeManager();
	String nmNr = String.valueOf(nm.getNumber());
	String nmName = nm.getName();
	
	if (!nmNrList.contains(nmNr)) nmNrList.add(nmNr);
	
	if (!nameList.contains(nmName)) {
		nameList.add(nmName);
		nmMap.put(nmName, nmNr);
	}
}

if (sort.equals("nd") || sort.equals("nu")) {	// we're sorting on Name (not GuiName)
  	nmNrList.clear();
  	
	Collections.sort(nameList);
	if (sort.equals("nd")) Collections.reverse(nameList);

  	Iterator it = nameList.iterator();
  	while (it.hasNext()) {
		String name = (String)it.next();
		String nmstr = (String)nmMap.get(name);
		// out.println("nmstr: " + nmstr);
	 	if (!nmNrList.contains(nmstr)) nmNrList.add(nmstr);
	 	
	}	
}

Iterator nrit = nmNrList.iterator();
while (nrit.hasNext()) {
 	String nmstr = (String)nrit.next();
 	int nmnr = Integer.parseInt(nmstr);
 	NodeManager nm = wolk.getNodeManager(nmnr);
 	j++;
 	// if (!nm.hasField("dnumber") || conf_list.equals("all")) { // Are we allowed to create?
 	%>
	<tr<%if (j % 2 == 0) { %> bgcolor="#FFFFFF"<% } %>>
	  <td class="right"><b><%= nm.getName() %></b></td>
	  <td><a href="index.jsp?ntype=<%= nm.getName() %>" title="show nodes"><%= nm.getGUIName() %></a></td>
	  <td nowrap="nowrap"> 
	  	<a href="index.jsp?ntype=<%= nm.getName() %>" title="show nodes"><img 
	  	  src="img/mmbase-search.gif" alt="show recent nodes" width="21" height="20" border="0" /></a>
	    <% 
	    if (nm.mayCreateNode()) { 
	    %><a href="new_object.jsp?ntype=<%= nm.getName() %>" title="new node"><img 
	      src="img/mmbase-new.gif" alt="new node" width="21" height="20" border="0" /></a>
	    <% 
	      } 
	    %>
	  </td>
	</tr>
	<%
	//} // all
} 
%>
</table>
<!-- /nodetypes -->
</td>
<td valign="top" width="80%">

<!-- Search and search results -->
<mm:compare referid="searchbox" value="after" inverse="true"><%@ include file="inc_searchform.jsp" %></mm:compare>
<%@ include file="inc_searchresults.jsp" %>
<mm:compare referid="searchbox" value="after"><%@ include file="inc_searchform.jsp" %></mm:compare>

</td>
</tr>
</table>
<%@ include file="inc_foot.jsp" %>
</mm:cloud>
