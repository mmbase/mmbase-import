<%@ include file="page_base.jsp"
%><mm:import externid="userlogon" from="parameters" />
<mm:content language="$config.lang" country="$config.country" type="text/html" expires="0">
<mm:cloud  loginpage="login.jsp" logon="$userlogon" sessionname="$config.session" rank="$rank" jspvar="cloud">
<mm:write referid="style" escape="none" />
<!-- mm:timer name="search_node"-->
<title><%=m.getString("search_node.search")%></title>
</head>
<mm:write referid="config.liststyle" vartype="string" jspvar="liststyle" >
<mm:context id="edit">
<mm:import externid="node_type"  jspvar="node_type" from="parameters"/>

<body class="basic" <mm:present referid="node_type">onLoad="document.search.elements[0].focus();"</mm:present>>

<% urlStack.clear(); %>
<mm:import externid="search" />
<mm:import externid="page" />

<mm:url referids="node_type?,search?,page?" id="thisurl" write="false">
  <mm:present referid="search">
    <mm:fieldlist id="search_form" nodetype="$node_type" type="search">
      <mm:fieldinfo type="reusesearchinput" />
    </mm:fieldlist>
  </mm:present>
</mm:url>

<mm:write referid="thisurl" vartype="org.mmbase.bridge.jsp.taglib.pageflow.Url" jspvar="url">
 <%  push(urlStack, "home", url.get(false));  %>
</mm:write>

<table summary="node editors" width="100%" class="super">
  <tr align="left">
    <th width="20%">
      <mm:present referid="node_type"><a href="<mm:url page="search_node.jsp" />"><%=m.getString("search_node.search")%></a></mm:present>

      <mm:notpresent referid="node_type">
        <%=m.getString("search_node.search")%>
      </mm:notpresent>

    (<mm:compare referid="config.liststyle" value="short" >
    <a href="<mm:url page="search_node.jsp" ><mm:param name="mmjspeditors_liststyle">long</mm:param></mm:url>"><span class="navigate"><%=m.getString("search_node.showall")%></span></a>
  </mm:compare>
  <mm:compare referid="config.liststyle" value="short" inverse="true" >
    <a href="<mm:url page="search_node.jsp" ><mm:param name="mmjspeditors_liststyle">short</mm:param></mm:url>"><span class="navigate"><%=m.getString("search_node.showshortlist")%></span></a>
    </mm:compare>)

  </th>
  <mm:present referid="node_type">
    <th width="80%"><%=m.getString("search_node.type")%> <mm:nodeinfo nodetype="$node_type" type="guitype" />
      (<mm:nodeinfo nodetype="$node_type" type="type" />)
      <mm:maycreate type="$node_type">
        <a href="<mm:url referids="node_type" page="create_node.jsp" />" >
          <span class="create"><!-- needed for IE --></span><span class="alt">[create]</span>
        </a>
    </mm:maycreate>
    </th>
  </mm:present>
  <mm:notpresent referid="node_type">
    <th width="80%"><%=m.getString("search_node.nonodes")%>
        </th>
  </mm:notpresent>
    	</tr>
    	<tr valign="top">
    	    <td><!-- node manager overview -->
             <!-- mm:timer name="node_managers"-->
             <!-- quick search by number/alias: -->
             <form method="post" action="<mm:url page="change_node.jsp"/>">
    	    	<table summary="node managers" width="100%" cellspacing="1" cellpadding="3" border="0">
		    <% // functionality for listing nodemanagers is not (yet?) in taglib, using MMCI.
                     NodeManagerList l = cloud.getNodeManagers();
                    java.util.Collections.sort(l, new java.util.Comparator() {
                       public int  compare(Object o1, Object o2) {
                         NodeManager n1 = ((Node)o1).toNodeManager();
                         NodeManager n2 = ((Node)o2).toNodeManager();
                         return n1.getGUIName().toUpperCase().compareTo(n2.getGUIName().toUpperCase());
                       }
                    } ); // MMCI doesn't sort, do it ourselves.
                    for (int i=0; i<l.size(); i++) {
                        NodeManager nt = l.getNodeManager(i);
                        if ( (nt.mayCreateNode() && !nt.hasField("dnumber")) || !"short".equals(liststyle)) {
                %>
      	    	    <tr valign="top">
      	    	    	<td title="<%=nt.getName()%>" class="data" width="100%" colspan="2">
                    <%=nt.getGUIName()%><span class="nmname"><%=nt.getName()%></span></td>
                  </td>
      	    	    	<td class="navigate">
                        <% if (nt.mayCreateNode()) { %>
			    <a href="<mm:url page="create_node.jsp"><mm:param name="node_type"><%=nt.getName()%></mm:param></mm:url>" >
                  <span class="create"><!-- needed for IE --></span><span class="alt">[create]</span>
             	  </a>
                       <% } else { %>&nbsp;<% } %>
      	    	    	</td>
      	    	    	<td class="navigate">
			    <% if (! nt.getName().equals(node_type)) { %>
            	 <a href="<mm:url><mm:param name="node_type"><%=nt.getName()%></mm:param></mm:url>">
                  <span class="select"><!-- needed for IE --></span><span class="alt">[list]</span>
               </a>
			    <% } else { %>
			    &nbsp;
			    <% } %>
      	    	    	</td>
		    </tr>
		    <%          }
    	    	    }
		    %>
    	    	    	<tr>
			    <td class="data"><%=m.getString("alias")%></td><td class="data" width="100%"><input class="small" type="text" size="5" name="node_number" /></td>
			    <td colspan="2" class="navigate"><input type="submit"  name="change" value="--&gt;" /></td>
			</tr>
    	    	</table>
            </form>
            <%-- /mm:timer--%>
    	    </td>
    	    <td><%-- right column, present search result (if clicked on node manager)--%>
          <mm:present referid="node_type">
            <%-- following page needs the param 'to_page' --%>
            <mm:import id="to_page"><mm:url page="change_node.jsp"/></mm:import>
            <mm:include referids="to_page" page="search_node_with_type.jsp" />
            &nbsp;
          </mm:present>
          <mm:notpresent referid="node_type">
            <form name="search" method="post" action="<mm:url page="change_node.jsp"/>">
              <table class="search" align="center" width="100%" border="0" cellspacing="1">
                <tr><td width="20%"><%=m.getString("aliasornumber")%></td><td width="80%"><input class="small" type="text" size="5" name="node_number" /></td></tr>
                <tr><td colspan="2"><input class="search" type="submit" name="search" value="<%=m.getString("search")%>" /></td></tr>
              </table>
            </form>
          </mm:notpresent>
	    </td>
    	</tr>
    </table>
</mm:context>
</mm:write>
<%@ include file="foot.jsp"  %>
<!-- mm:timer -->
</mm:cloud>
</mm:content>
