<%@ page import = "java.util.HashSet" %>

<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<mm:content postprocessor="reducespace">
<mm:import externid="wizardjsp" required="true" jspvar="wizardjsp" />

<%
 String imageName = "";
 String sAltText ="";
 int depth = Integer.parseInt(request.getParameterValues("depth")[0]);
 String startnode = request.getParameterValues("startnode")[0];
 String parenttree = request.getParameterValues("parenttree")[0];
// System.err.println("Get request for depth " + depth);

 depth--;
 if (depth >= 0)
 {
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

<mm:node number="<%=startnode%>" jspvar="thisNode">
   <mm:import id="treeName" jspvar="treeName">lbTree<mm:field name="number"/>z</mm:import>
   <%
      if(thisNode.countRelatedNodes(cloud.getNodeManager("learnobjects"),"posrel","destination")>0)
      {//needed an extra check to prevent mm:related from writing warning messages
         %>
            var <mm:write referid="treeName" /> = new MTMenu();

            <mm:nodeinfo type="type" jspvar="sNodeType" vartype="String">

               <%
                  HashSet hsetAllowedChildren = new HashSet();

                  if(sNodeType.equals("learnblocks"))
                  {
                     hsetAllowedChildren.add("learnblocks");
                     hsetAllowedChildren.add("pages");
                     hsetAllowedChildren.add("tests");
                     hsetAllowedChildren.add("flashpages");
                  }
                  if(sNodeType.equals("tests"))
                  {
                     hsetAllowedChildren.add("mcquestions");
                     hsetAllowedChildren.add("openquestions");
                     hsetAllowedChildren.add("rankingquestions");
                     hsetAllowedChildren.add("valuequestions");
                     hsetAllowedChildren.add("couplingquestions");
                     hsetAllowedChildren.add("hotspotquestions");
                     hsetAllowedChildren.add("dropquestions");
                  }
               %>
        <%--
               <mm:write referid="treeName" />.addItem("test<%= startnode %><%= sNodeType %>",
                                                                "",
                                                                null,
                                                                "",
                                                                "");
        --%>
               <%
                  if(hsetAllowedChildren.contains("learnblocks")){
                     %>
                        <mm:write referid="treeName" />.addItem("new learnblock",
                                                                "<mm:write referid="wizardjsp"/>?wizard=learnblocks&objectnumber=new&origin=<mm:field name="number"/>",
                                                                null,
                                                                "",
                                                                "<mm:treefile write="true" page="/education/wizards/gfx/new_education.gif" objectlist="$includePath" />");
                     <%
                  }
                  if(hsetAllowedChildren.contains("pages")){
                     %>
                        <mm:write referid="treeName" />.addItem("new pagina",
                                                                "<mm:write referid="wizardjsp"/>?wizard=pages&objectnumber=new&origin=<mm:field name="number"/>",
                                                                null,
                                                                "",
                                                                "<mm:treefile write="true" page="/education/wizards/gfx/new_education.gif" objectlist="$includePath" />");
                     <%
                  }
                  if(hsetAllowedChildren.contains("tests")){
                     %>
                        <mm:write referid="treeName" />.addItem("new test",
                                                                "<mm:write referid="wizardjsp"/>?wizard=tests&objectnumber=new&origin=<mm:field name="number"/>",
                                                                null,
                                                                "",
                                                                "<mm:treefile write="true" page="/education/wizards/gfx/new_education.gif" objectlist="$includePath" />");
                     <%
                  }
                  if(hsetAllowedChildren.contains("flashpages")){
                     %>
                        <mm:write referid="treeName" />.addItem("new flash",
                                                                "<mm:write referid="wizardjsp"/>?wizard=flashpages&objectnumber=new&origin=<mm:field name="number"/>",
                                                                null,
                                                                "",
                                                                "<mm:treefile write="true" page="/education/wizards/gfx/new_education.gif" objectlist="$includePath" />");
                     <%
                  }
                  if(hsetAllowedChildren.contains("mcquestions")){
                     %>
                        <mm:write referid="treeName" />.addItem("new mcquestions",
                                                                "<mm:write referid="wizardjsp"/>?wizard=mcquestions&objectnumber=new&origin=<mm:field name="number"/>",
                                                                null,
                                                                "",
                                                                "<mm:treefile write="true" page="/education/wizards/gfx/new_education.gif" objectlist="$includePath" />");
                     <%
                  }
                  if(hsetAllowedChildren.contains("openquestions")){
                     %>
                        <mm:write referid="treeName" />.addItem("new openquestions",
                                                                "<mm:write referid="wizardjsp"/>?wizard=openquestions&objectnumber=new&origin=<mm:field name="number"/>",
                                                                null,
                                                                "",
                                                                "<mm:treefile write="true" page="/education/wizards/gfx/new_education.gif" objectlist="$includePath" />");
                     <%
                  }
                  if(hsetAllowedChildren.contains("rankingquestions")){
                     %>
                        <mm:write referid="treeName" />.addItem("new rankingquestions",
                                                                "<mm:write referid="wizardjsp"/>?wizard=rankingquestions&objectnumber=new&origin=<mm:field name="number"/>",
                                                                null,
                                                                "",
                                                                "<mm:treefile write="true" page="/education/wizards/gfx/new_education.gif" objectlist="$includePath" />");
                     <%
                  }
                  if(hsetAllowedChildren.contains("valuequestions")){
                     %>
                        <mm:write referid="treeName" />.addItem("new valuequestions",
                                                                "<mm:write referid="wizardjsp"/>?wizard=valuequestions&objectnumber=new&origin=<mm:field name="number"/>",
                                                                null,
                                                                "",
                                                                "<mm:treefile write="true" page="/education/wizards/gfx/new_education.gif" objectlist="$includePath" />");
                     <%
                  }
                  if(hsetAllowedChildren.contains("couplingquestions")){
                     %>
                        <mm:write referid="treeName" />.addItem("new couplingquestions",
                                                                "<mm:write referid="wizardjsp"/>?wizard=couplingquestions&objectnumber=new&origin=<mm:field name="number"/>",
                                                                null,
                                                                "",
                                                                "<mm:treefile write="true" page="/education/wizards/gfx/new_education.gif" objectlist="$includePath" />");
                     <%
                  }
                  if(hsetAllowedChildren.contains("hotspotquestions")){
                     %>
                        <mm:write referid="treeName" />.addItem("new hotspotquestions",
                                                                "<mm:write referid="wizardjsp"/>?wizard=hotspotquestions&objectnumber=new&origin=<mm:field name="number"/>",
                                                                null,
                                                                "",
                                                                "<mm:treefile write="true" page="/education/wizards/gfx/new_education.gif" objectlist="$includePath" />");
                     <%
                  }
                  if(hsetAllowedChildren.contains("dropquestions")){
                     %>
                        <mm:write referid="treeName" />.addItem("new dropquestions",
                                                                "<mm:write referid="wizardjsp"/>?wizard=dropquestions&objectnumber=new&origin=<mm:field name="number"/>",
                                                                null,
                                                                "",
                                                                "<mm:treefile write="true" page="/education/wizards/gfx/new_education.gif" objectlist="$includePath" />");
                     <%
                  }
               %>

            </mm:nodeinfo>


            <mm:related path="posrel,learnobjects" orderby="posrel.pos" directions="up" searchdir="destination">
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
                  <%-- System.err.println("Next request will have depth " + depth); --%>
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
         <%
      }
   %>
</mm:node>
</mm:cloud>
<%--// gone from depth <%=depth%>--%>
<%
 }
%>
</mm:content>
