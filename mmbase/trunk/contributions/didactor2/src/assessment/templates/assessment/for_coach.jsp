<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<%@page import="org.mmbase.bridge.*,java.util.ArrayList" %>

<mm:content postprocessor="reducespace">
<mm:cloud method="delegate" jspvar="cloud">
<%@include file="/shared/setImports.jsp" %>
  <%@include file="includes/functions.jsp" %>
  <%@include file="includes/geteducation.jsp" %>

  <% int lessonsNum = 0; %>
  <mm:node number="$assessment_education" notfound="skip">
    <mm:relatednodes type="learnblocks" path="posrel,learnblocks" orderby="posrel.pos">
      <mm:first>
        <mm:import id="dummy" jspvar="dummy" vartype="Integer" reset="true"><mm:size/></mm:import>
        <% lessonsNum = dummy.intValue(); %>
      </mm:first>
    </mm:relatednodes>
  </mm:node>
  <% lessonsNum = lessonsNum - 1; %>
  <mm:node number="$user" notfound="skip">
    <div><table class="poplistTable" style="width:100%">
      <tr style="vertical-align:top">
        <th class="listHeader" style="width:70%">&nbsp;</th>
        <% int count = 0; %>
        <mm:node number="$assessment_education" notfound="skip">
          <mm:relatednodes type="learnblocks" path="posrel,learnblocks" orderby="posrel.pos"  max="<%=  "" + lessonsNum %>">
            <th class="listHeader"><di:translate key="assessment.period" />&nbsp;<%= count+1 %></th>
            <% count++; %>
          </mm:relatednodes>
        </mm:node>
      </tr>
      <mm:relatednodes type="classes" path="classrel,classes">
        <mm:field name="number" jspvar="classId" vartype="String">
        <mm:field name="name" jspvar="className" vartype="String">
          <mm:related path="classrel,people,related,roles"
              constraints="roles.name='student'" fields="people.number" distinct="true"
              orderby="people.lastname">
            <mm:first>
              <tr style="vertical-align:top;">
                <th class="listHeader" style="text-align:left">
                  <img src="<mm:treefile page="/assessment/gfx/minus.gif" objectlist="$includePath" referids="$referids"/>"
                       border="0" title="<di:translate key="assessment.show_problems" />" alt="<di:translate key="assessment.show_problems" />" 
                       onClick="toggleAll(<%= classId %>,'<%= getStudentsByClass(cloud, classId) %>');"
                       id="toggle_image<%= classId %>"/>
                  <%= className %>
                </th>
                <% for(int i=0; i<lessonsNum; i++) { %>
                    <th class="listHeader">&nbsp;</th>
                <% } %>
              </tr>
            </mm:first>
            <mm:field name="people.number" jspvar="studentId" id="student" vartype="String">
              <tr id="toggle_div<%= classId %>_<%= studentId %>">
                <td class="listItem">
                  <mm:field name="people.lastname"/>, <mm:field name="people.firstname"/> <mm:field name="people.suffix"/>
                </td>
                <% count = 0; %>
                <mm:node number="$assessment_education" notfound="skip">
                  <mm:relatednodes type="learnblocks" path="posrel,learnblocks" orderby="posrel.pos"  max="<%=  "" + lessonsNum %>">
                    <td class="listItem">
                      <%
                      if(count<lessonsNum-1) {
                        String feedback = "";
                        String feedbackId = "-1";
                        %>
                        <mm:relatedcontainer path="classrel,people">
                          <mm:constraint field="people.number" value="<%= studentId %>"/>
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
                        <% 
                        if ("".equals(feedback)) {
                          %>
                          &nbsp;
                          <%
                        } else { 
                             if ("-1".equals(feedback)) {   
                               %><a href="<mm:treefile page="/assessment/showfeedback.jsp" objectlist="$includePath" referids="$referids">
                                            <mm:param name="feedback_n"><%= feedbackId %></mm:param>
                                            <mm:param name="coachmode">true</mm:param>
                                          </mm:treefile>"
                                   ><img src="<mm:treefile page="/assessment/gfx/developed.gif" objectlist="$includePath" 
                                                referids="$referids"/>" border="0" title="<di:translate key="assessment.goto_feedback" />"
                                         alt="<di:translate key="assessment.goto_feedback" />" /></a><%
                             } else {
                               %><a href="<mm:treefile page="/assessment/givefeedback.jsp" objectlist="$includePath" referids="$referids">
                                            <mm:param name="feedback_n"><%= feedbackId %></mm:param>
                                            <mm:param name="coachmode">true</mm:param>
                                          </mm:treefile>"
                                   ><img src="<mm:treefile page="/assessment/gfx/todevelop.gif" objectlist="$includePath" 
                                                referids="$referids"/>" border="0" title="<di:translate key="assessment.goto_feedback" />"
                                         alt="<di:translate key="assessment.goto_feedback" />" /></a><%
                             }
                           }
                      } else {
                        %>
                        <mm:node number="$student">
                          <%@include file="/education/tests/find_copybook.jsp"%>
                        </mm:node>
                        <mm:node number="$assessment_evaluationtest" notfound="skip">
                          <!-- get madetest -->
                          <mm:relatednodescontainer path="madetests,copybooks" element="madetests">
                            <mm:constraint field="copybooks.number" referid="copybookNo"/>
                            <mm:relatednodes>
                              <a href="<mm:treefile page="/assessment/showtest.jsp" objectlist="$includePath" referids="$referids">
                                          <mm:param name="test"><mm:write referid="assessment_evaluationtest" /></mm:param>
                                          <mm:param name="madetest"><mm:field name="number" /></mm:param>
                                          <mm:param name="coachmode">true</mm:param>
                                        </mm:treefile>"
                                ><img src="<mm:treefile page="/assessment/gfx/present.gif" objectlist="$includePath" 
                                            referids="$referids"/>" border="0" title="<di:translate key="assessment.view_evaluationtest" />"
                                      alt="<di:translate key="assessment.view_evaluationtest" />" /></a>
                            </mm:relatednodes>
                          </mm:relatednodescontainer>
                        </mm:node>
                        <%                     
                      }
                      %>
                    </td>
                    <% count++; %>
                  </mm:relatednodes>
                </mm:node>
              </tr>
            </mm:field>
          </mm:related>
        </mm:field>
        </mm:field>
      </mm:relatednodes>
    </table></div>
  </mm:node>
</mm:cloud>
</mm:content>
