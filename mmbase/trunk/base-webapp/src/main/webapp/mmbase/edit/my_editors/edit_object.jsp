<%@ include file="inc/top.jsp" %>
<mm:content type="text/html" postprocessor="none" escaper="none" expires="0">
<mm:cloud jspvar="cloud" method="loginpage" loginpage="login.jsp" rank="$rank">
<mm:import externid="nr" escape="text/html,trimmer" />  <%-- the node we're going to edit --%>
<mm:import id="nr" reset="true"><mm:node number="$nr" notfound="skipbody"><mm:field name="number" /><mm:import id="nodefound">y</mm:import></mm:node></mm:import>
<mm:node number="$nr" notfound="skipbody">
  <mm:nodeinfo type="type" id="ntype" write="false" />
  <mm:import id="pagetitle">
    <mm:hasfield name="title"><mm:field name="title" escape="inline" /></mm:hasfield>
    <mm:hasfield name="title" inverse="true"><mm:function name="gui" escape="tagstripper" /></mm:hasfield>
    : <mm:nodeinfo type="guitype" escape="lowercase" /> - my_editors
  </mm:import>
</mm:node>
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="nl">
<head>
  <%@ include file="inc/head.jsp" %>
</head>
<body>
<div id="frame">
<%@ include file="inc/pageheader.jsp" %>
<div id="sidebar">
  <div class="padsidebar">
  <mm:node number="$nr" jspvar="node" notfound="skipbody">
    <%-- changes to relations --%>
    <mm:import externid="rel" reset="true" />
    <mm:import externid="changerel" reset="true" />
    <mm:import externid="deleterel" reset="true" />
    <mm:present referid="rel">
      <mm:node number="$rel">
      <mm:present referid="changerel">
        <mm:fieldlist type="edit" id="fields${rel}" fields="owner"><mm:fieldinfo type="useinput" /></mm:fieldlist>
      </mm:present>
      <mm:present referid="deleterel">
        <div class="message"><mm:deletenode number="$rel" /> The relation is removed.</div>
      </mm:present>
      </mm:node>
    </mm:present>
    <%-- /changes to relations --%>
    <% 
    int c = 0; 
    NodeManager nm = node.getNodeManager();
    RelationManagerIterator relIterator;
    %>
    <mm:import id="searchdir" jspvar="searchdir">source</mm:import>
    <%@ include file="inc/relations.jsp" %>
    <mm:import id="searchdir" jspvar="searchdir" reset="true">destination</mm:import>
    <%@ include file="inc/relations.jsp" %>
    <mm:present referid="rel">
      <script type="text/javascript">
      toggle('edit_<mm:write referid="rel" />');
      </script>
    </mm:present>
  </mm:node>
  </div><!-- / .padsidebar -->
</div><!-- / #sidebar -->
<div id="content">
  <div class="padcontent">

<mm:node number="$nr" notfound="skipbody">
<div id="node">
<mm:import externid="change" />
<mm:import externid="new_alias" />
<mm:import externid="property_key" /><mm:import externid="property_value" />
<form enctype="multipart/form-data" method="post" action="<mm:url referids="nr" />">
  <fieldset>
  <div class="firstrow">
    <strong><mm:link page="edit_object.jsp" referids="nr"><a href="${_}">#  <mm:field name="number" /></a></mm:link></strong>
    <mm:maydelete><a href="<mm:url page="delete_object.jsp" referids="nr" />" title="delete"><img src="img/mmbase-delete.png" alt="delete" width="21" height="20" /></a></mm:maydelete>
    <h2>Edit node of type <mm:nodeinfo type="guinodemanager" />  (<mm:nodeinfo type="type" />)</h2>
  </div>

  <%-- saving changes --%>
  <mm:present referid="change">
  <div class="row">
    <mm:fieldlist type="edit" fields="owner"><mm:fieldinfo type="useinput" /></mm:fieldlist>
    <mm:present referid="new_alias"><%-- When there is was a alias, create that 1 2 --%> 
        <mm:node id="the_alias">
        <mm:createalias name="$new_alias" />
      </mm:node>
    </mm:present>
    <div class="message">The node <strong><mm:function name="gui" /></strong> (<mm:write referid="nr" />) is changed.</div>
  </div>
  </mm:present>
  <%-- /saving changes --%>

  <%-- editfields --%>
  <mm:maywrite><mm:import id="formtype">input</mm:import></mm:maywrite>
  <mm:maywrite inverse="true"><mm:import id="formtype">guivalue</mm:import></mm:maywrite>
  <mm:fieldlist type="edit" fields="owner">
    <div class="row">
      <label for="mm_<mm:fieldinfo type="name" />">
        <strong><mm:fieldinfo type="guiname" /></strong>
		<mm:fieldinfo type="description"><mm:isnotempty><a onmouseover="showBox('descr_<mm:fieldinfo type="name" />',event);return false;" onmouseout="showBox('descr_<mm:fieldinfo type="name" />',event);return false;"><mm:fieldinfo type="name" /></a></mm:isnotempty></mm:fieldinfo>
		<mm:fieldinfo type="description"><mm:isempty><mm:fieldinfo type="name" /></mm:isempty></mm:fieldinfo>
      </label>
      <span class="content"><mm:fieldinfo type="$formtype" /></span>
      <mm:fieldinfo type="description"><mm:isnotempty><div class="description" id="descr_<mm:fieldinfo type="name" />"><mm:write /></div></mm:isnotempty></mm:fieldinfo>
    </div>
  </mm:fieldlist>
  <%-- /editfields --%>
  
  <mm:compare referid="ntype" value="oalias" inverse="true">
    <%@ include file="inc/aliases.jsp" %>
  </mm:compare>
  <mm:compare referid="ntype" value="properties" inverse="true">
    <mm:hasnodemanager name="properties"><%@ include file="inc/properties.jsp" %></mm:hasnodemanager>
  </mm:compare>
  <mm:maywrite>
    <div class="lastrow">
      <input type="submit" name="change" value="Change" />
    </div>
  </mm:maywrite>
  <mm:maywrite inverse="true">
    <div class="lastrow"><p class="message">You are not allowed to edit his node.</p></div>
  </mm:maywrite>
  </fieldset>
</form>
</div><!-- / #node -->
</mm:node>
    <mm:notpresent referid="nodefound">
      <div class="lastrow">
		<div class="message">
		  <h2>Node not found</h2>
		  <p>The node you were looking for could not be found or there was no node number specified.</p>
		</div>
      </div>
    </mm:notpresent>
  </div><!-- / .padcontent -->
  <div class="padfoot">&nbsp;</div>
</div><!-- / #content -->
<%@ include file="inc/footer.jsp" %>
</div><!-- / #frame -->
</body>
</html>
</mm:cloud></mm:content>
