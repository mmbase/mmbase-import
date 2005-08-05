<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"%>

<fmt:bundle basename="nl.didactor.component.education.EducationMessageBundle">
<mm:cloud loginpage="/login.jsp" jspvar="cloud">
<%@include file="/shared/setImports.jsp"%>

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
                                "config/couple/couplingquestions-origin",
                                "config/question/hotspotquestions-origin",
                                "config/question/valuequestions-origin",
                                "config/question/dropquestions-origin"};

      String[] arrstrNames = {"createNewMcquestions",
                              "createNewOpenquestions",
                              "createNewRankingquestions",
                              "createNewCouplingquestions",
                              "createNewHotspotquestions",
                              "createNewValuequestions",
                              "createNewDropquestions"};
      String[] arrstrDescriptionsIDs = {"createNewMcquestionsDescription",
                                        "createNewOpenquestionsDescription",
                                        "createNewRankingquestionsDescription",
                                        "createNewCouplingquestionsDescription",
                                        "createNewHotspotquestionsDescription",
                                        "createNewValuequestionsDescription",
                                        "createNewDropquestionsDescription"};

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
                  <td>&nbsp;<nobr><a href='<mm:write referid="wizardjsp"/>?wizard=<%= arrstrBuilders[f] %>&objectnumber=new&origin=<mm:field name="number"/>' title='<fmt:message key="<%= arrstrDescriptionsIDs[f] %>"/>' target="text"><fmt:message key="<%= arrstrNames[f] %>"/></a></nobr></td>
               </tr>
            </table>
         <%
      }
   %>

</mm:node>
</mm:cloud>
</fmt:bundle>
