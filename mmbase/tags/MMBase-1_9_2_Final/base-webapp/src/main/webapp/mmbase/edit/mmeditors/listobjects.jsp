<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@include file="header.jsp" %><html xmlns="http://www.w3.org/1999/xhtml">
  <mm:cloud name="mmbase" method="http" jspvar="cloud" rank="$rank">
  <%  Stack states = (Stack)session.getValue("mmeditors_states");
      Properties state = (Properties)states.peek();
      String managerName = state.getProperty("manager");
      String role = state.getProperty("role");
      String search = state.getProperty("search");
      NodeManager daymarks = cloud.getNodeManager("daymarks");
  %>
  <mm:import id="nodetype"><%=managerName%></mm:import>
  <head>
    <title>Editor</title>
    <link rel="stylesheet" href="css/mmeditors.css" type="text/css" />
  </head>
  <body>
    <mm:import externid="offset" vartype="Integer" jspvar="offset">0</mm:import>
    <mm:import externid="max" vartype="Integer" jspvar="max">20</mm:import>
<mm:import externid="search_age" />
<mm:import id="age_constraint" />
<mm:isnotempty referid="search_age">
   <mm:remove referid="age_constraint" />
   <mm:import id="age_constraint" >[number] >= <mm:write referid="search_age" jspvar="maxage" vartype="integer"
       ><%=daymarks.getInfo("COUNTAGE-"+maxage)%></mm:write></mm:import>
</mm:isnotempty>
<mm:import id="constraint"><mm:context
  ><mm:fieldlist id="search_form" nodetype="$nodetype" type="search"
    ><mm:fieldinfo type="usesearchinput"><mm:isnotempty
      ><mm:present referid="notfirst"> AND </mm:present><mm:notpresent referid="notfirst"><mm:import id="notfirst">yes</mm:import></mm:notpresent
      ><mm:write
    /></mm:isnotempty></mm:fieldinfo
  ></mm:fieldlist
  ><mm:write referid="age_constraint"><mm:isnotempty
    ><mm:present referid="notfirst"> AND </mm:present
    ><mm:write
  /></mm:isnotempty></mm:write
></mm:context></mm:import>
<%
 int listsize = 0;
 int showsize = 0;
%>
<mm:write referid="constraint" vartype="String" jspvar="constraints">
      <%
          if (constraints!=null && !constraints.equals("")) {
            search = constraints;
            state.put("search",search);
          }
          NodeManager manager = cloud.getNodeManager(managerName);
          NodeList nl = manager.getList(search, "number", "DOWN");
          listsize = nl.size();
          showsize = listsize-offset.intValue();
          if (max.intValue() < showsize) showsize=max.intValue();
%>
    <mm:import id="pagenumbers">
            Weergave <%=offset.intValue()+1%> t/m <%=offset.intValue()+showsize%> van <%=listsize%>
            <% if (showsize < listsize) {
            %>
              <table><tr><td>
              <% if (offset.intValue()>0) { %>
                <a href="<mm:url page="listobjects.jsp" >
                           <mm:param name="offset"><%=offset.intValue()<max.intValue() ? 0 : offset.intValue()-max.intValue()%></mm:param>
                         </mm:url>">&lt;&lt;</a>&nbsp;
              <% }
                 for (int pg = 0; pg <listsize; pg+=max.intValue()) {
                     int pagenr = (pg / max.intValue()) + 1;
                     if (offset.intValue() >= pg && offset.intValue() < pg+max.intValue()) {
              %>[<%=pagenr%>]&nbsp;
              <%     } else { %>
                <a href="<mm:url page="listobjects.jsp" >
                           <mm:param name="offset"><%=pg%></mm:param>
                         </mm:url>">[<%=pagenr%>]</a>&nbsp;
              <%     }
                 }
                 if (offset.intValue()+showsize < listsize) { %>
                &nbsp;<a href="<mm:url page="listobjects.jsp" >
                           <mm:param name="offset"><%=offset.intValue()+max.intValue()%></mm:param>
                         </mm:url>">&gt;&gt;</a>
              <% } %>
              </td></tr></table>
            <%  } %>
    </mm:import>
    <mm:write referid="pagenumbers" />
    <table class="objectlist">
      <tr>
        <td class="editprecol">&nbsp;</td>
        <mm:fieldlist type="list" nodetype="$nodetype">
        <mm:first><mm:import id="columns"><mm:size jspvar="cols" vartype="Integer"><%=cols.intValue()+1%></mm:size></mm:import></mm:first>
        <td class="editcol"><mm:fieldinfo type="guiname" /></td>
        </mm:fieldlist>
      </tr>
<%
          for (int i = offset.intValue(); i < offset.intValue()+showsize; i++) {
            Node node = nl.getNode(i);
      %>
      <mm:context>
        <mm:node number='<%= "" + node.getNumber() %>' id="node">
        <tr>
          <td>
            <table>
              <tr>
                <% if (role != null) { %>
                <td class="editlink"><a href="<mm:url page="editor.jsp" referids="node" ><mm:param name="createrelation">true</mm:param></mm:url>" target="_top">##</a></td>
                <% } else { %>
                <td class="editlink"><a href="<mm:url page="editnode.jsp" referids="node" />" target="selectarea">##</a></td>
                <% } %>
              </tr>
            </table>
          </td>
        <mm:fieldlist type="list" nodetype="$nodetype">
          <td><mm:fieldinfo type="name"><mm:field name="gui($_)" /></mm:fieldinfo></td>
        </mm:fieldlist>
        </tr>
        </mm:node>
      </mm:context>
      <% } %>
</mm:write>
     </table>
    <mm:write referid="pagenumbers" />
  </body>
  </mm:cloud>
</html>
