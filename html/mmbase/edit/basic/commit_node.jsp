<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-2.0"  prefix="mm"
%><%@include file="page_base_functionality.jsp"
%><mm:cloud loginpage="login.jsp"  sessionname="$config.session" jspvar="cloud" rank="$rank">
<mm:param name="org.mmbase.xml-mode" value="$config.xmlmode" />

<mm:log>Saving with XML-mode <%=cloud.getProperty("org.mmbase.xml-mode")%></mm:log>
<mm:context id="commit_node">
<mm:import externid="node_type" required="true" />
<mm:import externid="page">0</mm:import>

<mm:import externid="node_number" />
<mm:import externid="delete" />
<mm:import externid="deleterelations" />

<mm:import externid="cancel" />

<!-- first, check validity -->
<mm:notpresent referid="delete">
<mm:notpresent referid="deleterelations">
  <mm:notpresent referid="cancel">
    <mm:form mode="validate">
      <mm:present referid="node_number">
        <mm:node referid="node_number">
          <mm:context>
            <mm:fieldlist id="my_form" type="edit">
              <mm:fieldinfo type="check" />
            </mm:fieldlist>
          </mm:context>
        </mm:node>
      </mm:present>
      <mm:notpresent referid="node_number">
        <mm:context>
          <mm:fieldlist nodetype="$node_type" id="my_form" type="edit">
            <mm:fieldinfo type="check" />
          </mm:fieldlist>
        </mm:context>
      </mm:notpresent>
      <mm:valid inverse="true">
        <mm:import id="invalid" />
        <mm:present referid="node_number">
          <mm:include  page="change_node.jsp" />
        </mm:present>
        <mm:notpresent referid="node_number">
          <mm:include  page="create_node.jsp" />
        </mm:notpresent>
      </mm:valid>
    </mm:form>
  </mm:notpresent>
</mm:notpresent>
</mm:notpresent>
<mm:notpresent referid="invalid">
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "DTD/xhtml1-transitional.dtd">
<html>
  <head>
    <link rel="icon" href="images/favicon.ico" type="image/x-icon" />
    <link rel="shortcut icon" href="images/favicon.ico" type="image/x-icon" />
    <mm:import id="style">
      <link rel="StyleSheet" type="text/css" href="css/<mm:write referid="config.style_sheet" />"/>
    </mm:import>
<title><%=m.getString("commit_node.commit")%></title>
<mm:import externid="new" />
<mm:import externid="ok" />
<mm:import externid="save" />
<mm:present referid="save"><mm:notpresent referid="invalid"><mm:import id="ok" reset="true" /></mm:notpresent></mm:present>
<mm:url id="redirectTo" write="false"  page="<%=peek(urlStack)%>"><% if (urlStack.size() > 1) { %><mm:param name="nopush" value="url" /><% } %></mm:url>


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
    <mm:node id="new_node2" referid="new_node">

      <mm:remove referid="redirectTo" />

      <mm:import externid="node" />
      <mm:present referid="node">
        <mm:import externid="role_name" />
        <mm:import externid="direction" />
        <mm:url id="redirectTo" write="false" page="new_relation.jsp" referids="node,role_name,direction,node_type" >
          <mm:param name="create_relation">yes</mm:param>
          <mm:param name="node_number"><mm:field name="number" /></mm:param>
        </mm:url>
      </mm:present>

      <mm:notpresent referid="node">
        <mm:url id="redirectTo" write="false" page="change_node.jsp" >
          <mm:param name="node_number"><mm:field name="number" /></mm:param>
          <mm:param name="push"><mm:field name="number" /></mm:param>
        </mm:url>
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
      <mm:remove referid="redirectTo" />
      <mm:present referid="save">
        <mm:url id="redirectTo" write="false" page="change_node.jsp" >
          <mm:param name="node_number"><mm:field name="number" /></mm:param>
          <mm:param name="push"><mm:field name="number" /></mm:param>
        </mm:url>
      </mm:present>
      <mm:notpresent referid="save">
        <mm:url id="redirectTo" write="false" page="<%=peek(urlStack)%>"><mm:param name="nopush" value="url" /></mm:url>
      </mm:notpresent>
    </mm:node>

</mm:present>
</mm:notpresent>

<!-- do the redirect to the page where we want to go to... -->
<META HTTP-EQUIV="refresh" content="0; url=<mm:url page="$redirectTo" />" />
<mm:write referid="style" />
</head>
<body>
<h1><%=m.getString("redirect")%></h1>
<a href="<mm:url referid="redirectTo" />">
<%= m.getString("commit_node.redirect")%></a>


<mm:redirect referid="redirectTo" />
<%@ include file="foot.jsp"  %>

</mm:notpresent>
</mm:context>
</mm:cloud>
