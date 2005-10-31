<%-- show historic nodes --%>
<%-- wat moet 't doen? nog buggie!
1. node opslaan die 'vooraan staat'
2. maar history moet weer geleegd worden als een van de startnodes 'vooraan staat'
3. nodes mogen er niet dubbel inkomen
--%>
<%
List hl = new ArrayList();
if (session.getAttribute("yammedithistory") != null) {
	hl = (ArrayList)session.getAttribute("yammedithistory");
	
	// clear history when we'er back @ a startnode
	if (Arrays.binarySearch(str_nodes, nr) > -1) { hl.clear(); }
	
	Iterator he = hl.iterator();
	while (he.hasNext()) {
		String hisnr = (String)he.next();
		if (!hisnr.equals(nr)) {
	%>
		<mm:node number="<%= hisnr %>">
			<h3><span class="buttons"><a href="<mm:url referids="nrs,nodes,fields,paths">
			  <mm:param name="nr"><%= hisnr %></mm:param>
			</mm:url>"><img src="/mmbase/edit/my_editors/img/mmbase-edit.gif" alt="edit" width="21" height="20" /></a></span><mm:function name="gui" /></h3>
		</mm:node>
	<% 
		}
	} 
}
if (!hl.contains(nr)) { hl.add(nr); }
session.putValue("yammedithistory", hl);
%>
