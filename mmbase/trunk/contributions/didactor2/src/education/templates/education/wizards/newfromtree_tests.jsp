<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm"%>

<mm:cloud method="delegate" jspvar="cloud">
<%@include file="/shared/setImports.jsp"%>

<!--
    TODO. This JSP is horrible.
-->

<mm:import id="wizardjsp"       reset="true"><%= request.getParameter("wizardjsp") %></mm:import>
<mm:import id="the_last_parent" reset="true"><%= request.getParameter("the_last_parent") %></mm:import>

<mm:node number="<%= request.getParameter("node") %>">

   <mm:import id="the_last_element" reset="true">true</mm:import>
   <mm:relatednodes type="questions" max="1">
      <mm:import id="the_last_element" reset="true">false</mm:import>
   </mm:relatednodes>

   <%
      String[] arrstrBuilders = {"config/question/mcquestions-origin",
                                "config/question/openquestions-origin",
                                "config/question/rankingquestions-origin",
                                "config/question/couplingquestions-origin",
                                "config/question/hotspotquestions-origin",
                                "config/question/valuequestions-origin",
                                "config/question/dropquestions-origin",
                                "config/question/fillquestions-origin",
                                "config/question/fillselectquestions-origin",
                                "config/question/opennumeralquestions-origin",
                                "config/question/essayquestions-origin",
                                "config/question/openvaluequestions-origin"};

      String[] arrstrNames = {"education.createnewmcquestions",
                              "education.createnewopenquestions",
                              "education.createnewrankingquestions",
                              "education.createnewcouplingquestions",
                              "education.createnewhotspotquestions",
                              "education.createnewvaluequestions",
                              "education.createnewdropquestions",
                              "education.createnewfillquestions",
                              "education.createnewfillselectquestions",
                              "education.createnewopennumeralquestions",
                              "education.createnewessayquestions",
                              "education.createnewopenvaluequestions"};
      String[] arrstrDescriptionsIDs = {"education.createnewmcquestionsdescription",
                                        "education.createnewopenquestionsdescription",
                                        "education.createnewrankingquestionsdescription",
                                        "education.createnewcouplingquestionsdescription",
                                        "education.createnewhotspotquestionsdescription",
                                        "education.createnewvaluequestionsdescription",
                                        "education.createnewdropquestionsdescription",
                                        "education.createnewfillquestionsdescription",
                                        "education.createnewfillselectquestionsdescription",
                                        "education.createnewopennumeralquestionsdescription",
                                        "education.createnewessayquestionsdescription",
                                        "education.createnewopenvaluequestionsdescription"};

      for(int f = 0; f < arrstrBuilders.length; f++)
      {
         %>
            <table border="0" cellpadding="0" cellspacing="0">
               <tr>
                  <td><img src="gfx/tree_spacer.gif" width="16px" height="16px" border="0" align="center" valign="middle"/></td>
                  <mm:compare referid="the_last_parent" value="true" inverse="true">
                     <td><img src="gfx/tree_vertline.gif" border="0" align="center" valign="middle"/></td>
                  </mm:compare>
                  <mm:compare referid="the_last_parent" value="true">
                     <td><img src="gfx/tree_spacer.gif" width="16px" height="16px" border="0" align="center" valign="middle"/></td>
                  </mm:compare>

                  <%
                     if(f == arrstrBuilders.length - 1)
                     {// if this is the last element we should check there are tests or not
                      // and close the branch if needed
                        %>
                           <mm:compare referid="the_last_element" value="true" inverse="true">
                              <td><img src="gfx/tree_vertline-leaf.gif" border="0" align="center" valign="middle"/></td>
                           </mm:compare>
                           <mm:compare referid="the_last_element" value="true">
                              <td><img src="gfx/tree_leaflast.gif" border="0" align="center" valign="middle"/></td>
                           </mm:compare>
                        <%
                     }
                     else
                     {
                        %>
                           <td><img src="gfx/tree_vertline-leaf.gif" border="0" align="center" valign="middle"/></td>
                        <%
                     }
                  %>

                  <td><img src="gfx/new_education.gif" width="16" border="0" align="middle" /></td>
                  <%
                  if(request.getParameter("testname") != null) {%>
                   <mm:import id="newfromtreetestname" reset="true"><%=request.getParameter("testname")%></mm:import>
                  <% } %>
                  <td><nobr>
                    <mm:link referid="wizardjsp" referids="_node@origin,newfromtreetestname@path">
                      <mm:param name="wizard"><%= arrstrBuilders[f] %></mm:param>
                      <mm:param name="objectnumber">new</mm:param>
                      <a href="${_}"
                         title='<di:translate key="<%= arrstrDescriptionsIDs[f] %>"/>' target="text">
                      <di:translate key="<%= arrstrNames[f] %>"/></a>
                      </mm:link>
                    </nobr>
                  </td>
               </tr>
            </table>
         <%
      }
   %>

</mm:node>
</mm:cloud>
