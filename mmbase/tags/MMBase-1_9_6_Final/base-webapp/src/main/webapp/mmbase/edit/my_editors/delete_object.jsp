<%@ include file="inc/top.jsp" %>
<mm:content type="text/html" escaper="none" expires="0">
<mm:cloud jspvar="cloud" method="loginpage" loginpage="login.jsp" rank="$rank">

<mm:import externid="nr" />
<mm:import externid="ntype"><mm:node number="$nr"><mm:nodeinfo type="type" /></mm:node></mm:import>
<mm:import externid="pagetitle">Delete <mm:present referid="ntype"><mm:write referid="ntype" /></mm:present> node</mm:import>
<%-- mm:node number="$nr"><mm:import id="ntype"><mm:nodeinfo type="type" /></mm:import></mm:node --%>
<mm:import externid="delete" />

<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="nl">
<head>
  <%@ include file="inc/head.jsp" %>
</head>
<body>
<div id="frame">
<%@ include file="inc/pageheader.jsp" %>
<div id="sidebar">
  <div class="padsidebar">
    <table border="0" cellspacing="0" cellpadding="3" id="back">    
    <thead>
    <tr>
      <th class="name"><a href="<mm:url page="index.jsp" referids="ntype" />"><img src="img/mmbase-left.png" alt="overview" width="21" height="20" /></a></th>
      <th>Overview <mm:write referid="ntype" /></th>
    </tr>
    </thead>
    <tbody>
    <mm:notpresent referid="delete">
      <mm:node number="$nr">
      <tr>
        <td class="name"><mm:maywrite><a href="edit_object.jsp?nr=<mm:field name="number" />" title="edit node"><img src="img/mmbase-edit.png" alt="edit object" width="21" height="20" /></a></mm:maywrite></td>
        <td>Edit the node</td>
      </tr>
      </mm:node>
    </mm:notpresent>
    <mm:present referid="delete">
      <tr>
        <td class="name"><a href="<mm:url page="new_object.jsp" referids="ntype" />" title="a new node of this type"><img src="img/mmbase-new.png" alt="new" width="21" height="20" /></a></td>
        <td>New node of type <mm:write referid="ntype" /></td>
      </tr>
    </mm:present>
    </tbody>
    </table>
  </div><!-- / .padder -->
</div><!-- / #sidebar -->
<div id="content">
  <div class="padcontent">
  
  <div id="node">
  <mm:notpresent referid="delete">
    <mm:node number="$nr">
    <form method="post" action="<mm:url referids="nr,ntype" />">
      <fieldset>
      <div class="firstrow">
        <strong># <mm:field name="number" /></strong>
        <mm:maywrite><a href="<mm:url page="edit_object.jsp" referids="nr" />" title="edit node"><img src="img/mmbase-edit.png" alt="edit object" width="21" height="20" /></a></mm:maywrite>
        <h2>Delete node <mm:function name="gui" /> (<mm:nodeinfo type="type" />)</h2>
      </div>  
      <mm:fieldlist type="edit">
        <div class="row">
          <label for="mm_<mm:fieldinfo type="name" />">
            <strong><mm:fieldinfo type="guiname" /></strong>
			<mm:fieldinfo type="description"><mm:isnotempty><a onmouseover="showBox('descr_<mm:fieldinfo type="name" />',event);return false;" onmouseout="showBox('descr_<mm:fieldinfo type="name" />',event);return false;"><mm:fieldinfo type="name" /></a></mm:isnotempty></mm:fieldinfo>
			<mm:fieldinfo type="description"><mm:isempty><mm:fieldinfo type="name" /></mm:isempty></mm:fieldinfo>
          </label>
          <span class="content"><mm:fieldinfo type="guivalue" /></span>
          <div class="description" style="display: none;" id="descr_<mm:fieldinfo type="name" />"><mm:fieldinfo type="description" /></div>
        </div>
      </mm:fieldlist>
      <div class="row">
        <label>
          <strong>Alias</strong>
          <a onmouseover="toggle('descr_alias');return false;" onmouseout="toggle('descr_alias');return false;">alias</a>
        </label>
        <span class="content"><mm:aliaslist id="aliasses"><mm:write /> </mm:aliaslist></span>
        <div class="description" style="display: none;" id="descr_alias"><mm:nodeinfo type="description" nodetype="oalias" /></div>
      </div>
  
      <mm:maydelete>
      <mm:import id="nr_relations" jspvar="nr_rels" vartype="String"><mm:countrelations /></mm:import>
      <div class="lastrow">
        <% 
        int nr_rel = Integer.parseInt(nr_rels);
        if (nr_rel > 0) { 
        %>
          <p class="message">This node has <%= nr_rel %> relation(s) with other node(s).</p>
          <input type="submit" name="delete" value="Delete_with_relations" />
        <% } else { %>
          <input type="submit" name="delete" value="Delete" />
        <% } %>
        </mm:maydelete>
        <mm:maydelete inverse="true"><p class="message">You are not allowed to delete this node.</p></mm:maydelete>
      </div>
      </fieldset>
    </form>
    </mm:node>
  </mm:notpresent>
  <mm:present referid="delete">
    
    <mm:node number="$nr"><mm:maydelete><mm:deletenode number="$nr" deleterelations="true" /></mm:maydelete></mm:node>
    <div class="firstrow"> 
      <a href="<mm:url page="new_object.jsp" referids="ntype" />" title="new"><img src="img/mmbase-new.png" alt="new" width="21" height="20" /></a>
      <h2>Node deleted!</h2> 
    </div>
    <div class="row">
      <div class="message">
        The node of type <b><mm:nodeinfo type="guitype" nodetype="$ntype" /></b> (<mm:write referid="ntype" />) is deleted.<br />
        <a href="<mm:url page="index.jsp" referids="ntype" />" title="back to the overview of <mm:write referid="ntype" />"><img src="img/mmbase-left.png" alt="go back" width="21" height="20" /></a>
        Back to the <a href="<mm:url page="index.jsp" referids="ntype" />">overview of <mm:write referid="ntype" /></a>.
      </div>
    </div>
    
  </mm:present>
  </div><!-- / #node-->
  
  </div><!-- / .padcontent -->
  <div class="padfoot">&nbsp;</div>
</div><!-- / #content -->
<%@ include file="inc/footer.jsp" %>
</div><!-- / #frame -->
</body>
</html>
</mm:cloud></mm:content>
