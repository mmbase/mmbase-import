<%@include file="header.jsp" %>
  <mm:cloud name="mmbase" method="http" jspvar="cloud">
  <%  Stack states = (Stack)session.getValue("mmeditors_states");
      Properties state = (Properties)states.peek();
      String managerName = state.getProperty("manager");
      String role = state.getProperty("role");
      Module mmlanguage = cloud.getCloudContext().getModule("mmlanguage");
  %>
  <mm:import id="nodetype"><%=managerName%></mm:import>
  <head>
    <title>Editor</title>
    <link rel="stylesheet" href="css/mmeditors.css" type="text/css" />
    <style>
<%@include file="css/mmeditors.css" %>     
    </style>
  </head>
  <body>
    <mm:import externid="offset" vartype="Integer" jspvar="offset">0</mm:import>
    <mm:import externid="max" vartype="Integer" jspvar="max">10</mm:import>
    <table class="objectlist">
      <tr>
        <td class="editprecol">&nbsp;</td>
        <mm:fieldlist type="list" nodetype="$nodetype">
        <mm:first><mm:import id="columns"><mm:size jspvar="cols" vartype="Integer"><%=cols.intValue()+1%></mm:size></mm:import></mm:first> 
        <td class="editcol"><mm:fieldinfo type="guiname" /></td>
        </mm:fieldlist>
      </tr>
           
<mm:import externid="search_age" />
<mm:import id="age_constraint" />
<mm:isnotempty referid="search_age">
   <mm:remove referid="age_constraint" />     
   <mm:import id="daycount_constraint"> [daycount] <=  <mm:write referid="search_age" jspvar="maxage" vartype="integer"><%=(int)(System.currentTimeMillis()/(1000*60*60*24)) - maxage.intValue()%></mm:write></mm:import>
   <mm:listnodes type="daymarks" constraints="$daycount_constraint" max="1" orderby="daycount" directions="DOWN">
      <mm:import id="age_constraint"> [number] >= <mm:field name="mark" /></mm:import>
   </mm:listnodes>
    <mm:notpresent referid="age_constraint">
     <mm:import id="age_constraint"> [number] = -1 </mm:import><!-- will not find a thing -->
   </mm:notpresent>
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
      <mm:listnodes type="$nodetype" max="$max" offset="$offset" constraints="$constraint" id="node" orderby="number" directions="DOWN">
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
        <mm:last>
         <tr><td colspan="<mm:write referid="columns" />" >
            <mm:size jspvar="listsize" vartype="Integer">
            Weergave <%=offset.intValue()+1%> t/m <%=offset.intValue()+listsize.intValue()%>
            </mm:size>
         </td></tr>
        </mm:last>
      </mm:listnodes>
     </table>
  </body>
  </mm:cloud>
</html>
