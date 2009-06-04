<%@ include file="page_base.jsp"
%><mm:content type="text/html" language="$config.lang" country="$config.country" expires="0">
<mm:cloud  loginpage="login.jsp" sessionname="$config.session"  rank="$rank">
<mm:context id="new_relation">

<mm:import externid="node"               required="true" />
<mm:import externid="node_type"          required="true" />
<mm:import externid="role_name"          required="true" />
<mm:import externid="direction"          required="true" />
<mm:import externid="create_relation"    required="false" />

<mm:notpresent referid="create_relation">
   <mm:write referid="style" escape="none" />
   <title><%=m.getString("new_relation.new")%></title>
   </head>
   <body class="basic" onLoad="document.search.elements[0].focus();">
     <p class="crumbpath"><%= toHtml(urlStack, request) %></p>
     <!-- TODO escapeamps=false was not needed in 1.8 -->
   <mm:import externid="to_page"><mm:url escapeamps="false" absolute="context"
                                         referids="role_name,node_type,node,direction">
                                 <mm:param name="create_relation">yes</mm:param>
                                 </mm:url></mm:import>
   <mm:import id="maylink">yes</mm:import>



   <mm:node referid="node">

     <table class="edit" summary="node editor" width="93%"  cellspacing="1" cellpadding="3" border="0">
     <tr><th><%=m.getString("new_relation.new")%></th></tr>

    <mm:compare referid="direction" value="create_child">
        <tr><th><%=m.getString("new_relation.from")%>: <mm:nodeinfo type="gui" /></th></tr>
    </mm:compare>
    <mm:compare referid="direction" value="create_parent">
        <tr><th><%=m.getString("new_relation.to")%>: <mm:nodeinfo type="gui" /></th></tr>
    </mm:compare>

     <tr><td>
       <mm:include  referids="to_page" page="search_node_with_type.jsp?nopush=true" />
     </td></tr>
     </table>

   </mm:node>

</mm:notpresent>


<mm:present referid="create_relation">
   <title><%=m.getString("new_relation.new")%></title>

   <mm:import externid="annotate_relation" />
   <mm:import externid="node_number" required="true"/>

   <mm:node id="node" referid="node">

   <mm:import id="redirectTo"><mm:url page="change_node.jsp"><mm:param name="node_number"><mm:field name="number" /></mm:param></mm:url></mm:import>

   <mm:notpresent referid="annotate_relation">
     <mm:node id="node_number" referid="node_number">

    <mm:compare referid="direction" value="create_child">
        <mm:createrelation id="relation" source="node" destination="node_number" role="$role_name" >
            <mm:fieldlist type="edit">
                <mm:first><mm:import id="annotate">true</mm:import></mm:first>
            </mm:fieldlist>
        </mm:createrelation>
    </mm:compare>

    <mm:compare referid="direction" value="create_parent">
        <!-- if role could also be the dname, this code wouldnt be nessecary -->
        <!-- solved by replacing source with destination -->
        <mm:createrelation id="relation" source="node_number" destination="node" role="$role_name" >
            <mm:fieldlist type="edit">
                <mm:first><mm:import id="annotate">true</mm:import></mm:first>
            </mm:fieldlist>
        </mm:createrelation>
    </mm:compare>

    <mm:present referid="annotate">
        <mm:write referid="style" escape="none" />
        </head>
        <body class="basic" onLoad="document.new.elements[2].focus();">
          <p class="crumbpath"><%= toHtml(urlStack, request) %></p>
        <form name="new" method="post" action='<mm:url referids="node,node_number,node_type,role_name,direction" />' >
        <input type="hidden" name="create_relation" value="yes" />
        <table class="edit" summary="node editor" width="93%"  cellspacing="1" cellpadding="3" border="0">
        <tr><th colspan="2"><%= m.getString("new_relation.new") %> (<mm:write referid="role_name" />)</th></tr>
        <tr><th colspan="2">between <mm:nodeinfo node="node_number" type="gui" /> and <mm:nodeinfo node="node" type="gui" /></th></tr>
        <mm:node referid="relation">
            <input type="hidden" name="relation" value="<mm:field name="number" />" />
        <mm:fieldlist id="edit_relation" type="edit"><mm:context>
         <tr><td><mm:fieldinfo type="guiname" /></td><td><mm:fieldinfo type="input" /></td></tr>
        </mm:context></mm:fieldlist>
        </mm:node>
        <tr><td colspan="2" class="data"><input type="submit" id="okbutton" name="annotate_relation" value="ok" /></td></tr>
        </table>
       </form>
     </mm:present>

     <mm:notpresent referid="annotate">
       <!-- do the redirect to the page where we want to go to... -->
       <META HTTP-EQUIV="refresh" content="0; url=<mm:url  page="$redirectTo" />">
       <mm:write referid="style" escape="none" />
       </head>
       <body class="basic">
       <h1>Redirecting</h1>
       <a href="<mm:url page="$redirectTo" />"><%=m.getString("new_relation.redirect")%></a>
     </mm:notpresent>

    </mm:node>
  </mm:notpresent>

  <mm:present referid="annotate_relation">
    <mm:import externid="relation" required="true" />

    <mm:node referid="relation">
       <mm:fieldlist id="edit_relation" type="edit"><mm:context>
          <mm:fieldinfo type="useinput" />
       </mm:context></mm:fieldlist>
    </mm:node>
    <meta http-equiv="refresh" content="0; url=<mm:url page="$redirectTo" />">
    <mm:write referid="style" escape="none" />
    </head>
    <body>
    <h1>Redirecting</h1>
    <a href="<mm:url page="$redirectTo" />"><%=m.getString("new_relation.redirect")%></a>
    <%-- never mind .. --%>
    <mm:redirect referid="redirectTo" />

  </mm:present>

  </mm:node>
</mm:present>

</mm:context>
<%@ include file="foot.jsp"  %>
</mm:cloud>
</mm:content>
