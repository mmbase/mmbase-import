<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<mm:content postprocessor="reducespace">
<mm:import externid="wizardjsp" required="true" jspvar="wizardjsp" />

<%
 String imageName = "";
 String sAltText ="";
 int depth = Integer.parseInt(request.getParameterValues("depth")[0]);
 String startnode = request.getParameterValues("startnode")[0];
 String parenttree = request.getParameterValues("parenttree")[0];
 System.err.println("Get request for depth " + depth);

 depth--;
 if (depth >= 0) {
%>
<%--// entering depth <%=depth%>--%>
<%--// for node <%=startnode%>--%>
<mm:cloud loginpage="/login.jsp" jspvar="cloud">
  <%@include file="/shared/setImports.jsp"%>

<mm:node number="component.pdf" notfound="skip">
    <mm:relatednodes type="providers" constraints="providers.number=$provider">
        <mm:import id="pdfurl"><mm:treefile write="true" page="/pdf/pdfchooser.jsp" objectlist="$includePath" referids="$referids" /></mm:import>
    </mm:relatednodes>
</mm:node>

<mm:node number="<%=startnode%>">
  <mm:import id="treeName" jspvar="treeName">lbTree<mm:field name="number"/>z</mm:import>
  <mm:related path="posrel,learnobjects" orderby="posrel.pos" directions="up" searchdir="destination">
   <mm:first>
    var <mm:write referid="treeName" /> = new MTMenu();
<%--    <mm:write referid="treeName" /> = new MTMenu();--%>
   </mm:first>
   <mm:node element="learnobjects">
   <%@include file="whichimage.jsp"%>
    <mm:import id="objecttype"><mm:nodeinfo type="type" /></mm:import>

        <mm:import id="mark_error" reset="true"></mm:import>
        <mm:compare referid="objecttype" value="tests">
            <mm:field name="questionamount" id="questionamount">
                <mm:isgreaterthan value="0">
                    <mm:countrelations type="questions">
                        <mm:islessthan value="$questionamount">
                            <mm:import id="mark_error" reset="true">Er zijn minder vragen ingevoerd dan er gesteld moeten worden.</mm:import>
                        </mm:islessthan>
                    </mm:countrelations>
                </mm:isgreaterthan>
                <mm:field name="requiredscore" id="requiredscore">
                  <mm:countrelations type="questions">
                      <mm:islessthan value="$requiredscore">
                          <mm:import id="mark_error" reset="true">Er zijn minder vragen ingevoerd dan er goed beantwoord moeten worden.</mm:import>
                      </mm:islessthan>
                  </mm:countrelations>
                  <mm:isgreaterthan referid="questionamount" value="0">
                      <mm:islessthan referid="questionamount" value="$requiredscore">
                        <mm:import id="mark_error" reset="true">Er worden minder vragen gesteld dan er goed beantwoord moeten worden.</mm:import>
                      </mm:islessthan>
                  </mm:isgreaterthan>
                </mm:field>
            </mm:field>
        </mm:compare>
        <mm:compare referid="objecttype" value="mcquestions">
            <mm:import id="mark_error" reset="true">Een multiple-choice vraag moet minstens 1 goed antwoord hebben</mm:import>
            <mm:relatednodes type="mcanswers" constraints="mcanswers.correct > '0'" max="1">
                <mm:import id="mark_error" reset="true"></mm:import>
            </mm:relatednodes>
        </mm:compare>




    <mm:write referid="treeName" />.addItem(
        "<mm:field name="name"><mm:isempty><mm:field name="title"/></mm:isempty><mm:isnotempty><mm:write/></mm:isnotempty></mm:field><mm:present referid="pdfurl"><mm:compare referid="objecttype" value="pages"></a> <a href='<mm:write referid="pdfurl"/>&number=<mm:field name="number"/>' target='text'>(PDF)</mm:compare><mm:compare referid="objecttype" value="learnblocks"></a> <a href='<mm:write referid="pdfurl"/>&number=<mm:field name="number"/>' target='text'>(PDF)</mm:compare></mm:present></a> <a href='metaedit.jsp?number=<mm:field name="number"/>' target='text'><img id='img_<mm:field name="number"/>' src='<%= imageName %>' border='0' alt='<%= sAltText %>'> <mm:isnotempty referid="mark_error"></a> <a style='color: red; font-weight: bold' href='javascript:alert(&quot;<mm:write referid="mark_error"/>&quot;);'>!</mm:isnotempty>",
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
