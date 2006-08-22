<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.1" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<%@page import="org.mmbase.bridge.*,java.util.ArrayList" %>

<mm:content postprocessor="reducespace">
<mm:cloud method="delegate" jspvar="cloud">
<%@include file="/shared/setImports.jsp" %>

  <%@include file="includes/variables.jsp" %>
  <%@include file="includes/functions.jsp" %>
  <%@include file="includes/geteducation.jsp" %>
  <%@include file="includes/getlb.jsp" %>
  <%@include file="includes/getlesson.jsp" %>
  
  <% int count = 0; %>

        <span style="FONT-SIZE : 1.5em; font-weight:bold;"><di:translate key="assessment.goals" /></span><br/>
        <mm:node number="$user">
          <mm:relatednodes type="goals" path="posrel,goals" orderby="posrel.pos">
            <mm:first><table></mm:first>
              <mm:field name="number" jspvar="goal_number" vartype="String" write="false">
                <tr>
                  <td>
                    <a href="<mm:treefile page="/assessment/editgoal.jsp" objectlist="$includePath" referids="$referids">
                               <mm:param name="goal_n"><mm:field name="number"/></mm:param>
                             </mm:treefile>"
                      ><img src="<mm:treefile page="/assessment/gfx/edit_learnobject.gif" objectlist="$includePath" 
                            referids="$referids"/>" border="0" title="<di:translate key="assessment.edit_goal" />"
                            alt="<di:translate key="assessment.edit_goal" />" /></a>
                    <a href="<mm:treefile page="/assessment/deleteobject.jsp" objectlist="$includePath" referids="$referids">
                               <mm:param name="object_n"><mm:field name="number"/></mm:param>
                             </mm:treefile>"
                             onclick="return doAction('<di:translate key="assessment.prompt_to_delete_goal" />');"
                      ><img src="<mm:treefile page="/assessment/gfx/remove.gif" objectlist="$includePath" 
                            referids="$referids"/>" border="0" title="<di:translate key="assessment.delete_goal" />"
                            alt="<di:translate key="assessment.delete_goal" />" /></a>
                    <img align="middle" src="<mm:treefile page="/assessment/gfx/plus.gif" objectlist="$includePath" 
                         referids="$referids"/>" border="0" title="<di:translate key="assessment.show_goal" />" 
                         alt="<di:translate key="assessment.show_goal" />" 
                         onClick="toggle(<%=goal_number %>);" id="toggle_image<%=goal_number %>"/>
                  </td>
                  <td style="FONT-SIZE : 0.8em">
                    <mm:field name="name" jspvar="dummy" vartype="String" write="false">
                      <%= ( "".equals(dummy) ? "&nbsp;" : dummy )%>
                    </mm:field>
                  </td>
                </tr>
                <tr id="toggle_div<%=goal_number %>" style="display:none">
                  <td>&nbsp;</td>
                  <td style="FONT-SIZE : 0.8em">
                    <mm:field name="description" jspvar="dummy" vartype="String" write="false">
                      <%= ( "".equals(dummy) ? "&nbsp;" : dummy )%>
                    </mm:field>
                  </td>
                </tr>
              </mm:field>
            <mm:last></table></mm:last>
          </mm:relatednodes>
        </mm:node>
        <a href="<mm:treefile page="/assessment/editgoal.jsp" objectlist="$includePath" referids="$referids"/>"
          ><img src="<mm:treefile page="/assessment/gfx/new_learnobject.gif" objectlist="$includePath" 
               referids="$referids"/>" border="0" title="<di:translate key="assessment.add_goal" />" 
               alt="<di:translate key="assessment.add_goal" />" /></a>
        <br/>
        <br/>
        <br/>
        <span style="FONT-SIZE : 1.5em; font-weight:bold;"><di:translate key="assessment.problems" /></span><br/>
        <a href="<mm:treefile page="/assessment/editproblem.jsp" objectlist="$includePath" referids="$referids"/>"
            ><img src="<mm:treefile page="/assessment/gfx/new_learnobject.gif" objectlist="$includePath" 
                  referids="$referids"/>" border="0" title="<di:translate key="assessment.add_problem" />"
                  alt="<di:translate key="assessment.add_problem" />" /></a><br/><br/>
        <% int lessonsNum = 0;
           ArrayList styles = new ArrayList();
           boolean wasCurrent = false;
        %>
        <mm:node number="$assessment_education" notfound="skip">
          <mm:relatednodes type="learnblocks" path="posrel,learnblocks" orderby="posrel.pos">
            <mm:first>
              <mm:import id="dummy" jspvar="dummy" vartype="Integer" reset="true"><mm:size/></mm:import>
              <% lessonsNum = dummy.intValue(); %>
            </mm:first>
            <mm:field name="number" jspvar="this_lb" vartype="String" write="false">
              <% if (!wasCurrent) {
                   styles.add("");
                 } else {
                   styles.add("style=\"background-color:E7E7E7\"");
                 }
                 if (this_lb.equals(currentLesson)) {
                   wasCurrent = true;
                 }
              %>
            </mm:field>
          </mm:relatednodes>
        </mm:node>

        <div><table class="poplistTable" style="width:100%">
          <% boolean lessonShowed = false; %>
          <mm:listnodes type="problemtypes" orderby="pos">
            <mm:field name="number" jspvar="problemtypeId" vartype="String">
            <tr style="vertical-align:top;">
              <% String problems = getProblemsByType(cloud, problemtypeId, thisUser); 
                 if ("".equals(problems)) {
              %>
                   <th class="listHeader" style="width:30px;">&nbsp;</th>
              <% } else { %>
                   <th class="listHeader" style="width:30px; vertical-align:middle;"><img src="<mm:treefile page="/assessment/gfx/plus.gif" 
                         objectlist="$includePath" referids="$referids"/>" border="0" 
                         title="<di:translate key="assessment.show_problems" />" alt="<di:translate key="assessment.show_problems" />" 
                         onClick="toggleAll(<%= problemtypeId %>,'<%= problems %>');"
                         id="toggle_image<%= problemtypeId %>"/></th>
              <% } %>
              <th class="listHeader">
                <mm:field name="key" jspvar="dummy" vartype="String" write="false">
                  <di:translate key="<%= "assessment." + dummy %>"/>
                </mm:field>
              </th>
              <% if (!lessonShowed) {
                   lessonShowed = true;
                   count = 0;
              %>
                   <mm:node number="$assessment_education" notfound="skip">
                     <mm:relatednodes type="learnblocks" path="posrel,learnblocks" orderby="posrel.pos">
                       <th class="listHeader" style="width:82px; padding:0 0 0 0;<% if (!"".equals(styles.get(count))) { %>color:E7E7E7<% } %>"
                         ><mm:field name="name"/></th>
                       <% count++; %>
                     </mm:relatednodes>
                   </mm:node>
              <% } else { 
                   for(int i=0; i<lessonsNum; i++) { %>
                     <th class="listHeader">&nbsp;</th>
              <%   }
                 }
              %>
            </tr>
            <mm:related path="related,problems,posrel,people" orderby="posrel.pos"
                constraints="people.number=$user" fields="problems.number" distinct="true">
              <mm:field name="problems.number" jspvar="problem_number" vartype="String" write="false">
              <tr id="toggle_div<%=problem_number %>" style="display:none; vertical-align:top;">
                <td class="listItem" style="padding:0 0 0 0"><nobr>
                  <a href="<mm:treefile page="/assessment/editproblem.jsp" objectlist="$includePath" referids="$referids">
                             <mm:param name="problem_n"><%= problem_number %></mm:param>
                           </mm:treefile>"
                    ><img src="<mm:treefile page="/assessment/gfx/edit_learnobject.gif" objectlist="$includePath" 
                          referids="$referids"/>" border="0" title="<di:translate key="assessment.edit_problem" />"
                          alt="<di:translate key="assessment.edit_problem" />"
                   /></a><a href="<mm:treefile page="/assessment/deleteobject.jsp" objectlist="$includePath" referids="$referids">
                             <mm:param name="object_n"><%= problem_number %></mm:param>
                           </mm:treefile>"
                           onclick="return doAction('<di:translate key="assessment.prompt_to_delete_problem" />');"
                    ><img src="<mm:treefile page="/assessment/gfx/remove.gif" objectlist="$includePath" 
                          referids="$referids"/>" border="0" title="<di:translate key="assessment.delete_problem" />"
                          alt="<di:translate key="assessment.delete_problem" />" /></a>
                </nobr></td>
                <%@ include file="includes/matrixcell.jsp" %>
              </tr>
              </mm:field>
            </mm:related>
            </mm:field>
          </mm:listnodes>
          <tr>
            <td class="listItem">&nbsp;</td>
            <td class="listItem"><di:translate key="assessment.feedback_coach" /></td>
            <% count = 0; %>
            <mm:node number="$assessment_education" notfound="skip">
              <mm:relatednodes type="learnblocks" path="posrel,learnblocks" orderby="posrel.pos">
                <td class="listItem" <%= styles.get(count) %>>
                  <% String feedback = "";
                     String feedbackId = "-1";
                  %>
                  <mm:relatedcontainer path="classrel,people">
                    <mm:constraint field="people.number" value="$user"/>
                    <mm:related>
                      <mm:node element="classrel">
                        <mm:relatednodes type="popfeedback">
                          <mm:field name="status" jspvar="dummy" vartype="String" write="false">
                            <% feedback = dummy; %>
                          </mm:field>
                          <mm:field name="number" jspvar="dummy" vartype="String" write="false">
                            <% feedbackId = dummy; %>
                          </mm:field>
                        </mm:relatednodes>
                      </mm:node>
                    </mm:related>
                  </mm:relatedcontainer>
                  <% if ("".equals(feedback)) { %>
                       &nbsp;
                  <% } else { %>
                       <a href="<mm:treefile page="/assessment/showfeedback.jsp" objectlist="$includePath" referids="$referids">
                                  <mm:param name="feedback_n"><%= feedbackId %></mm:param>
                                </mm:treefile>"
                         ><%
                         if ("-1".equals(feedback)) {   
                           %><img src="<mm:treefile page="/assessment/gfx/developed.gif" objectlist="$includePath" 
                                  referids="$referids"/>" border="0" title="<di:translate key="assessment.goto_feedback" />"
                                  alt="<di:translate key="assessment.goto_feedback" />" /><%
                         } else {
                           %><img src="<mm:treefile page="/assessment/gfx/todevelop.gif" objectlist="$includePath" 
                                  referids="$referids"/>" border="0" title="<di:translate key="assessment.goto_feedback" />"
                                  alt="<di:translate key="assessment.goto_feedback" />" /><%
                         } %>
                       </a>
                  <% } %>
                </td>
                <% count++; %>
              </mm:relatednodes>
            </mm:node>
          </tr>
          <tr>
            <% for(int i=0; i<lessonsNum+2; i++) { %>
                 <td class="listItem" <%= ( i<2 ? "" : styles.get(i-2) ) %>>&nbsp;</td>
            <% } %>
          </tr>
        </table></div>
        <br/>
        <form name="closelessonform" action="<mm:treefile page="/assessment/closelesson.jsp" objectlist="$includePath" 
               referids="$referids"/>" method="post">
          <mm:node number="<%= backtolb %>" notfound="skip">
            <input type="submit" class="formbutton" value="<di:translate key="assessment.back_to_lesson" />"
                onclick="closelessonform.action='<mm:treefile page="/education/index.jsp" objectlist="$includePath" referids="$referids">
                                                   <mm:param name="learnobject"><%= backtolb %></mm:param>
                                                 </mm:treefile>'">
          </mm:node>
          <% boolean hasWeights = false; %>
          <mm:node number="<%= currentLesson %>" notfound="skip">
            <mm:related path="posrel,problems,posrel,people" max="1" constraints="people.number=$user">
              <% hasWeights = true; %>
              <input type="submit" class="formbutton" value="<di:translate key="assessment.close_and_send_to_coach" />"
                onclick="return doAction('<di:translate key="assessment.prompt_to_closing_lesson" />');">
            </mm:related>
          </mm:node>
          <% if (!hasWeights) { %>
               <input type="submit" class="formbutton" value="<di:translate key="assessment.close_and_send_to_coach" />" disabled>
          <% } %>
        </form>

</mm:cloud>
</mm:content>
