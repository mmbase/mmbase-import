<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<mm:content postprocessor="reducespace">
<mm:import externid="wizardjsp" required="true" jspvar="wizardjsp" />

<%
 int depth = Integer.parseInt(request.getParameterValues("depth")[0]);
 String startnode = request.getParameterValues("startnode")[0];
 String parenttree = request.getParameterValues("parenttree")[0];
 System.err.println("Get request for depth " + depth);

 depth--;
 if (depth >= 0) {
%>
<%--// entering depth <%=depth%>--%>
<%--// for node <%=startnode%>--%>
<mm:cloud>
<mm:node number="<%=startnode%>">
  <mm:import id="treeName" jspvar="treeName">lbTree<mm:field name="number"/>z</mm:import>
  <mm:related path="posrel,learnobjects" orderby="posrel.pos" directions="up" searchdir="destination">
   <mm:first>
    var <mm:write referid="treeName" /> = new MTMenu();
<%--    <mm:write referid="treeName" /> = new MTMenu();--%>
   </mm:first>  
   <mm:node element="learnobjects"> 
    <mm:import id="objecttype"><mm:nodeinfo type="type" /></mm:import>
    <mm:write referid="treeName" />.addItem(
        "<mm:field name="name"><mm:isempty><mm:field name="title"/></mm:isempty><mm:isnotempty><mm:write/></mm:isnotempty></mm:field><mm:compare referid="objecttype" value="pages"></a> (<a href='pdf.jsp?number=<mm:field name="number"/>' target='text'>PDF)</mm:compare><mm:compare referid="objecttype" value="learnblocks"></a> (<a href='pdf.jsp?number=<mm:field name="number"/>' target='text'>)</mm:compare>",
        "<mm:write referid="wizardjsp"/>?wizard=<mm:write referid="objecttype" />&objectnumber=<mm:field name="number" />&origin=<mm:field name="number" />",
        null,
        "bewerk object",
		"<mm:treefile write="true" page="/education/wizards/gfx/edit_learnobject.gif" objectlist="" />");
<%--    <mm:compare referid="objecttype" value="learnobjects"> --%>
      <% if (depth > 0) { %>
        <mm:field jspvar="objectNumber" name="number">
        <% System.err.println("Next request will have depth " + depth); %>
        <mm:include page="learnobject.jsp" referids="wizardjsp">
          <mm:param name="parenttree"><%=treeName%></mm:param>
          <mm:param name="startnode"><%=objectNumber%></mm:param>
          <mm:param name="depth"><%=depth%></mm:param>
        </mm:include>
        </mm:field>
      <% } %>
<%--    </mm:compare> --%>
   </mm:node> 
   <mm:last>
    <%=parenttree%>.makeLastSubmenu(<mm:write referid="treeName" />, true);
   </mm:last>
  </mm:related>
</mm:node>
</mm:cloud>
<%--// gone from depth <%=depth%>--%>
<%
 }
%>
</mm:content>
