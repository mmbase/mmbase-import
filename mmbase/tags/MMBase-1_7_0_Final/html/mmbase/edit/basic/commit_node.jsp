<%@ include file="page_base.jsp" 
%><mm:cloud method="$config.method" loginpage="login.jsp" sessionname="$config.session" jspvar="cloud">
<title><%=m.getString("commit_node.commit")%></title>
<mm:context id="commit_node">
<mm:import externid="node_type" required="true" />
<mm:import externid="page">0</mm:import>


<mm:import externid="cancel" />
<mm:import externid="new" />
<mm:import externid="delete" />
<mm:import externid="deleterelations" />
<mm:import externid="ok" />
<mm:import externid="node_number" />

<mm:import id="redirectTo"><mm:url escapeamps="false"  page="<%=peek(urlStack)%>"><% if (urlStack.size() > 1) { %><mm:param name="nopush" value="url" /><% } %></mm:url></mm:import>

<mm:present referid="cancel">
    <!-- do nothing,... will be redirected -->
</mm:present>

<mm:present referid="delete">
    <mm:deletenode referid="node_number" notfound="skip" />
</mm:present>

<mm:present referid="deleterelations">
    <mm:deletenode deleterelations="true" referid="node_number" notfound="skip" />
</mm:present>

<mm:present referid="new"><!-- this was a create node -->
  <mm:present referid="ok">
    <mm:import externid="alias_name" />
    <mm:createnode id="new_node" type="$node_type">
      <mm:fieldlist id="my_form" type="edit">
        <mm:fieldinfo type="useinput" />
      </mm:fieldlist>
    </mm:createnode>	
    <mm:node id="new_node2" referid="new_node" jspvar="node">
      
      <mm:remove referid="redirectTo" /> 

      <mm:import externid="node" />
      <mm:present referid="node">
        <mm:import externid="role_name" />
        <mm:import externid="direction" />
        <mm:import id="redirectTo"><mm:url escapeamps="false" page="new_relation.jsp" referids="node,role_name,direction,node_type" >
          <mm:param name="create_relation">yes</mm:param>
          <mm:param name="node_number"><mm:field name="number" /></mm:param>
        </mm:url></mm:import>
      </mm:present>

      <mm:notpresent referid="node">
        <mm:import id="redirectTo"><mm:url escapeamps="false" page="change_node.jsp" >
          <mm:param name="node_number"><mm:field name="number" /></mm:param>
          <mm:param name="push"><mm:field name="number" /></mm:param>
        </mm:url></mm:import>
      </mm:notpresent>
        
    </mm:node>
	
    <!-- if alias added (only for new nodes), do that too --> 
    <mm:present referid="alias_name">
    	<mm:node id="new_node3" referid="new_node" >
        <mm:createalias name="$alias_name" />
      </mm:node>
    </mm:present>
   </mm:present>
</mm:present>

<mm:notpresent referid="new"><!-- this was a change node -->
<mm:present referid="ok">
    <mm:import externid="_my_form_change_context" />
    <mm:import externid="_my_form_context" />

    <mm:node referid="node_number" notfound="skip">
      <!-- handle the form -->
      <mm:maywrite>
        <mm:fieldlist id="my_form" type="edit" fields="owner">
          <mm:fieldinfo type="useinput" />
        </mm:fieldlist>
      </mm:maywrite>       
    </mm:node>
    <mm:remove referid="redirectTo" />
    <mm:import id="redirectTo"><mm:url escapeamps="false" page="<%=peek(urlStack)%>"><mm:param name="nopush" value="url" /></mm:url></mm:import>
</mm:present>
</mm:notpresent>

<!-- do the redirect to the page where we want to go to... -->
<META HTTP-EQUIV="refresh" content="0; url=<mm:url page="$redirectTo" />" />
<mm:write referid="style" />
</head>
<body>
<h1><%=m.getString("redirect")%></h1>
<a href="<mm:url page="$redirectTo" />">
<%= m.getString("commit_node.redirect")%></a>

<mm:write referid="redirectTo" jspvar="redirect" vartype="string">
<% response.sendRedirect(redirect); %>
</mm:write>

</mm:context>
<%@ include file="foot.jsp"  %>
</mm:cloud>
