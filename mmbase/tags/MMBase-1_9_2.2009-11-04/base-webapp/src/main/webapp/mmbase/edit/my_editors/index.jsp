<%@ include file="inc/top.jsp" %>
<mm:content type="text/html" escaper="none" expires="0">
<mm:cloud jspvar="cloud" method="loginpage" loginpage="login.jsp" rank="$rank">
<mm:import externid="ntype" />
<mm:import externid="pagetitle"><c:if test="${!empty ntype}"><mm:nodeinfo type="plural_guitype" nodetype="$ntype" escape="lowercase" /> - </c:if>my_editors
</mm:import>
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="nl">
<head>
  <%@ include file="inc/head.jsp" %>
</head>
<body>
<div id="frame">
<%@ include file="inc/pageheader.jsp" %>
<div id="sidebar">
  <div class="padsidebar">
    <table border="0" cellspacing="0" cellpadding="3" id="nodetypes">
    <caption>Node types in MMBase</caption>
    <thead>
    <tr>
      <th scope="col" class="right">sort by <strong><a href="javascript:;" onclick="sortT('nodetypes', 0);">name</a></strong></th>
      <th scope="col"><strong><a href="javascript:;" onclick="sortT('nodetypes', 1);">guiname</a></strong></th>
      <th>&nbsp;</th>
    </tr>
    </thead>
    <tbody>

      <%-- all nodetypes --%>
      <mm:import vartype="List" jspvar="typelist" id="typelist" />
      <mm:listnodescontainer type="typedef">
        <mm:sortorder field="name" />
        <mm:listnodes jspvar="n">
          <mm:import id="name" jspvar="name" reset="true"><mm:field name="name" /></mm:import>
          <mm:import id="dnumber" reset="true">no</mm:import>
          <mm:hasnodemanager name="${name}">
            <mm:fieldlist type="create" nodetype="$name">
              <mm:fieldinfo type="name" id="fldname" write="false" />
              <mm:compare referid="fldname" value="dnumber"><%-- test for fieldname dnumber (TODO: makes no sense, should be a more general setting) --%>
              <mm:import id="dnumber" reset="true">yes</mm:import>
              </mm:compare>
            </mm:fieldlist>
            <%-- import typedefs --%>
            <mm:compare referid="list" value="all" inverse="true">
              <mm:compare referid="dnumber" value="no"><% typelist.add(n); %></mm:compare>
            </mm:compare>
            <mm:compare referid="list" value="all"><% typelist.add(n); %></mm:compare>
          </mm:hasnodemanager>

        </mm:listnodes>
      </mm:listnodescontainer>

      <mm:listnodes referid="typelist">
        <mm:import id="name" reset="true"><mm:field name="name" /></mm:import>
        <tr <mm:odd>class="odd"</mm:odd><mm:even>class="even"</mm:even>>
          <td class="right"><mm:write referid="name" /></td>
          <td>
            <a href="index.jsp?ntype=<mm:write referid="name" />" title="List <mm:nodeinfo nodetype="$name" type="guitype" /> nodes"><mm:nodeinfo nodetype="$name" type="guitype" /></a>
          </td>
          <td>
            <mm:maycreate type="$name"><a href="new_object.jsp?ntype=<mm:write referid="name" />" title="Create a new <mm:nodeinfo nodetype="$name" type="guitype" /> node"><img src="img/mmbase-new.png" alt="new node" width="21" height="20" /></a></mm:maycreate>
          </td>
        </tr>
      </mm:listnodes>

    </tbody>
    </table>
  </div><!-- / .padsidebar -->
</div><!-- / #sidebar -->
<div id="content">
<mm:present referid="ntype">
  <div class="padcontent">
    <mm:compare referid="searchbox" value="after" inverse="true"><%@ include file="inc/searchbox.jsp" %></mm:compare>
    <%@ include file="inc/searchresults.jsp" %>
    <mm:compare referid="searchbox" value="after"><%@ include file="inc/searchbox.jsp" %></mm:compare>
  </div><!-- / .padder -->
  <div class="padfoot">&nbsp;</div>
</mm:present>
</div><!-- / #content -->
<%@ include file="inc/footer.jsp" %>
</div><!-- / #frame -->
</body>
</html>
</mm:cloud></mm:content>
