<%@ include file="inc/top.jsp" %>
<mm:content type="text/html" postprocessor="none" escaper="none" expires="0">
<mm:cloud jspvar="cloud" method="loginpage" loginpage="login.jsp" rank="$rank">

<mm:import externid="ntype" escape="text/html,trimmer" />  <%-- type of node to create --%>
<mm:import externid="nr" />         <%-- create relation with this node nr
    when nr is present as a 'referrer', 
    the new node (rnr) will be related to the existing one (nr) --%>
<mm:import externid="rnr" />
<mm:import externid="rkind" />
<mm:import externid="dir" />

<mm:import id="pagetitle">new <mm:nodeinfo type="guitype" nodetype="$ntype" escape="lowercase" /> node - my_editors</mm:import>
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
        <th class="name"><a href="<mm:url page="index.jsp" referids="ntype" />"><img src="img/mmbase-left.png" alt="go back" width="21" height="20" /></a></th>
        <th>To <strong><mm:write referid="ntype" /></strong> <a href="<mm:url page="index.jsp" referids="ntype" />">overview</a></th>
      </tr>
    </thead>
    <tbody>
    <mm:present referid="nr">
      <tr>
        <td class="name"><a href="<mm:url page="relate_object.jsp" referids="ntype,nr,rkind?,dir?" />" title="search node for new relation"><img src="img/mmbase-search.png" alt="search node" width="21" height="20" /></a></td>
        <td><a href="<mm:url page="relate_object.jsp" referids="ntype,nr,rkind?,dir?" />">Search node</a> of type <strong><mm:write referid="ntype" /></strong></td>
      </tr><tr>
        <td class="name"><a href="<mm:url page="edit_object.jsp" referids="ntype,nr,rkind?,dir?" />"><img src="img/mmbase-edit.png" alt="edit" width="21" height="20" /></a></td>
        <td>
          <a href="<mm:url page="edit_object.jsp" referids="ntype,nr,rkind?,dir?" />">Back</a> to editing 
          <mm:node number="$nr"><strong><mm:nodeinfo type="type" /></strong> object</mm:node>
        </td>
      </tr>
      <tr>
        <td>&nbsp;</td>
        <td>
          <mm:node referid="nr">
            <mm:fieldlist type="list">
              <mm:fieldinfo type="guiname" /> 
              <mm:first><strong></mm:first><mm:fieldinfo type="guivalue" /><mm:first></strong></mm:first><br />
            </mm:fieldlist>
          </mm:node>
        </td>
      </tr>
    </mm:present>
    </tbody>
    </table>
  </div><!-- / .padder -->
</div><!-- / #sidebar -->
<div id="content">
  <div class="padcontent">

<div id="node">
<mm:import externid="save" />
<mm:import externid="new_alias" />
  
  <mm:notpresent referid="save">
    <div class="firstrow">
      <a href="<mm:url referids="ntype,nr?,rkind?,dir?" />"><img src="img/mmbase-new.png" alt="new" width="21" height="20" /></a>
      <mm:present referid="rnr">
        <mm:node referid="rnr">
          <mm:link page="edit_object.jsp" referids="rnr@nr">
            <a href="${_}" title="edit this new node"><img src="img/mmbase-edit.png" alt="edit" width="21" height="20" /></a>
          </mm:link>
        </mm:node>
      </mm:present>
      <h2>New node of type <mm:nodeinfo nodetype="$ntype" type="guinodemanager" /> (<mm:nodeinfo nodetype="$ntype" type="type" />)</h2>
    </div>
  </mm:notpresent>

  <%-- save the new node & show it --%>
  <mm:present referid="save">
    <mm:createnode type="$ntype" id="new_node">
      <mm:fieldlist type="edit"><mm:fieldinfo type="useinput" /></mm:fieldlist>
    </mm:createnode>
      
    <mm:node referid="new_node">
      <mm:present referid="new_alias">
        <mm:createalias name="$new_alias" />
      </mm:present>
      <mm:notpresent referid="rnr">   <%-- rnr is the new created node --%>
        <mm:import id="rnr" reset="true"><mm:field name="number" /></mm:import>
      </mm:notpresent>

      <div class="firstrow">
        <a href="<mm:url referids="ntype,nr?,rkind?,dir?" />"><img src="img/mmbase-new.png" alt="new" width="21" height="20" /></a>
        <mm:present referid="rnr">
          <mm:node referid="rnr">
            <mm:link page="edit_object.jsp" referids="rnr@nr">
              <a href="${_}" title="edit this new node"><img src="img/mmbase-edit.png" alt="edit" width="21" height="20" /></a>
            </mm:link>
          </mm:node>
        </mm:present>
        <h2>New node of type <mm:nodeinfo nodetype="$ntype" type="guinodemanager" /> (<mm:nodeinfo nodetype="$ntype" type="type" />)</h2>
      </div>
      <div class="message">    
        <h4>
          <mm:link page="edit_object.jsp" referids="rnr@nr">
            Your new node <a href="${_}" title="edit this new node"><mm:function name="gui" /></a> (<mm:field name="number" />) is saved
          </mm:link>
        </h4>
        <mm:present referid="new_alias"><mm:compare referid="new_alias" value="" inverse="true"><p>With alias '<mm:write referid="new_alias" />'</p></mm:compare></mm:present>
      </div>
    </mm:node>
  </mm:present>  <%-- /save --%>

  <%-- display new node --%>
  <mm:present referid="rnr">
    <mm:node referid="rnr">
      <div id="newnode">
        <mm:fieldlist type="list">
          <div class="row">
            <label for="mm_<mm:fieldinfo type="name" />">
              <strong><mm:fieldinfo type="guiname" /></strong>
              <mm:fieldinfo type="description"><mm:isnotempty><a onmouseover="showBox('descr_<mm:fieldinfo type="name" />',event);return false;" onmouseout="showBox('descr_<mm:fieldinfo type="name" />',event);return false;"><mm:fieldinfo type="name" /></a></mm:isnotempty></mm:fieldinfo>
              <mm:fieldinfo type="description"><mm:isempty><mm:fieldinfo type="name" /></mm:isempty></mm:fieldinfo>
            </label>
            <span class="content"><mm:fieldinfo type="guivalue" /></span>
            <mm:fieldinfo type="description"><mm:isnotempty><div class="description" id="descr_<mm:fieldinfo type="name" />"><mm:write /></div></mm:isnotempty></mm:fieldinfo>
          </div>
        </mm:fieldlist>
        <div class="lastrow">&nbsp;</div>
      </div>
    </mm:node>
  </mm:present>
    
  <%-- are we also relating ? --%>
  <%@ include file="inc/relating.jsp" %>


  <%-- ### not saved: form ### --%>
  <mm:notpresent referid="save">
    <mm:notpresent referid="the_relation"><%-- in case a relation has been created --%>
      <form enctype="multipart/form-data" method="post" action="<mm:url referids="ntype,nr?,rkind?,dir?" />">
        <fieldset>
          <mm:fieldlist nodetype="$ntype" type="edit">
            <div class="row">
              <label for="mm_<mm:fieldinfo type="name" />">
                <strong><mm:fieldinfo type="guiname" /></strong>
                <mm:fieldinfo type="description"><mm:isnotempty><a onmouseover="showBox('descr_<mm:fieldinfo type="name" />',event);return false;" onmouseout="showBox('descr_<mm:fieldinfo type="name" />',event);return false;"><mm:fieldinfo type="name" /></a></mm:isnotempty></mm:fieldinfo>
                <mm:fieldinfo type="description"><mm:isempty><mm:fieldinfo type="name" /></mm:isempty></mm:fieldinfo>
              </label>
              <span class="content"><mm:fieldinfo type="input" /></span>
              <mm:fieldinfo type="description"><mm:isnotempty><div class="description" id="descr_<mm:fieldinfo type="name" />"><mm:write /></div></mm:isnotempty></mm:fieldinfo>
            </div>
          </mm:fieldlist>
       
          <%-- aliases --%>
          <div class="row">
            <label for="new_alias">
              <strong>Alias</strong>
              <a onmouseover="showBox('descr_alias',event);return false;" onmouseout="showBox('descr_alias',event);return false;">alias</a>
            </label>
            <input type="text" id="new_alias" name="new_alias" size="80" maxlength="255" class="small" /><br />
            <div style="display: none;" class="description" id="descr_alias"><mm:nodeinfo type="description" nodetype="oalias" /></div>
          </div>
          <%-- /aliasses --%>
          
          <%-- button --%>
          <div class="lastrow">
            <input type="submit" name="save" value="Save" />
          </div>
        </fieldset>
      </form>
    </mm:notpresent><%-- /save --%>
  </mm:notpresent><%-- /the_relation --%>
</div><!-- / #node -->
  
  </div><!-- / .padcontent -->
  <div class="padfoot">&nbsp;</div>
</div><!-- / #content -->
<%@ include file="inc/footer.jsp" %>
</div><!-- / #frame -->
</body>
</html>
</mm:cloud></mm:content>
