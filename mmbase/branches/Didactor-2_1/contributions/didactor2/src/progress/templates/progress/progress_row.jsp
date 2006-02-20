<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.1" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>

<mm:content postprocessor="reducespace" expires="0">
<mm:cloud method="delegate" jspvar="cloud">

<%@include file="/shared/setImports.jsp" %>
<%@include file="/education/tests/definitions.jsp" %>
<%@include file="/education/wizards/roles_defs.jsp" %>
<mm:import id="editcontextname" reset="true">docent schermen</mm:import>
<%@include file="/education/wizards/roles_chk.jsp" %>

<mm:import externid="student"           required="true"/>
<mm:import externid="startAt"           jspvar="startAt" vartype="Integer" required="true"/>
<mm:import externid="direct_connection" required="true"/>

<%
/*
   NOTE:
   temp_class is used to avoid a bug with a double calling
   I mean the parent page is called as index.jsp?class=null
   And in all internal calls "class" will be "null" insted of proper value
*/
%>
<mm:import externid="temp_class"  />
<mm:compare referid="class" value="null">
   <mm:compare referid="direct_connection" value="true" inverse="true">
      <mm:import id="class" reset="true"><mm:write referid="temp_class"/></mm:import>
   </mm:compare>
</mm:compare>

<%--
<di:may component="education" action="isSelfOrTeacherOf" arguments="student">
--%>
   <mm:node number="$student">
      <tr>
         <td style="border-color:#000000; border-top:0px; border-left:0px">
            <a href="<mm:treefile page="/progress/student.jsp" objectlist="$includePath" referids="provider,education,student">
                        <mm:param name="class"><mm:write referid="class"/></mm:param>
                     </mm:treefile>"><mm:field name="firstname"/> <mm:field name="lastname"/>
            </a>
         </td>
         <td style="border-color:#000000; border-top:0px; border-left:0px">
            <mm:import jspvar="progress" id="progress" vartype="Double"><mm:treeinclude page="/progress/getprogress.jsp" objectlist="$includePath" referids="$referids"><mm:param name="student"><mm:write referid="student"/></mm:param></mm:treeinclude></mm:import>
            <%= (int)(progress.doubleValue()*100.0)%>%
         </td>


         <% //direct relation people-classrel-educations %>
         <mm:compare referid="direct_connection" value="true">
            <mm:list fields="classrel.number" path="people,classrel,educations" constraints="people.number=$student and educations.number=$education">
               <mm:field name="classrel.number" id="classrel" write="false"/>
            </mm:list>
         </mm:compare>
         <% //people-classrel-class-related-educations %>
         <mm:compare referid="direct_connection" value="true" inverse="true">
            <mm:list fields="classrel.number" path="people,classrel,classes" constraints="people.number=$student and classes.number=$class">
               <mm:field name="classrel.number" id="classrel" write="false"/>
            </mm:list>
         </mm:compare>


         <mm:node referid="classrel">
            <td style="border-color:#000000; border-top:0px; border-left:0px">
               <mm:field name="logincount"/>
            </td>
            <td style="border-color:#000000; border-top:0px; border-left:0px">
               <mm:field name="onlinetime" jspvar="onlinetime" vartype="Integer" write="false">
                  <%
                     int hour = onlinetime.intValue() / 3600;
                     int min = (onlinetime.intValue() % 3600) / 60;
                  %>
                  <%=hour%>:<%
                     if (min < 10)
                     {
                        %>0<%
                     }
                  %><%=min%>
               </mm:field>
            </td>
         </mm:node>

         <%
            int testCounter = 0;
         %>

         <%-- find copybook --%>
         <%@include file="find_copybook.jsp"%>

         <mm:node number="$education">
            <mm:relatednodescontainer type="learnobjects" role="posrel">

               <mm:sortorder field="posrel.pos" direction="up"/>
               <mm:tree type="learnobjects" role="posrel" searchdir="destination" orderby="posrel.pos" directions="up">
                  <mm:import id="nodetype"><mm:nodeinfo type="type" /></mm:import>

                  <mm:compare referid="nodetype" value="tests">
                  <%
                     if (testCounter >= startAt.intValue() && testCounter < startAt.intValue()+15)
                     {
                        %>
                           <mm:import id="testNo" reset="true"><mm:field  name="number" /></mm:import>
                           <mm:field id="feedback" name="feedbackpage" write="false"/>

<%--
                           <mm:import id="teststatus" reset="true" jspvar="testStatus" escape="reducespace"><mm:treeinclude page="/progress/teststatus.jsp" objectlist="$includePath" referids="$referids"><mm:param name="copybookNo"><mm:write referid="copybookNo"/></mm:param><mm:param name="testNo"><mm:field name="number"/></mm:param></mm:treeinclude></mm:import>
                           <%
                              testStatus = testStatus.trim();
                           %>
                           <mm:import id="teststatus" reset="true" jspvar="testStatus" escape="reducespace"><%= testStatus %></mm:import>
--%>

                           <%@include file="teststatus.jsp"%>

                           <mm:compare referid="teststatus" value="incomplete" inverse="true">
                              <mm:compare referid="teststatus" value="toberated">
                                 <mm:islessthan inverse="true" referid="rights" referid2="RIGHTS_RW">
                                    <td class="td_test_tbs" style="border-color:#000000; border-top:0px; border-left:0px"><a href="<mm:treefile page="/education/tests/rateopen.jsp" objectlist="$includePath" referids="$referids">
                                                                        <mm:param name="studentNo"><mm:write referid="student"/></mm:param>
                                                                        <mm:param name="testNo"><mm:write referid="testNo"/></mm:param>
                                                                     </mm:treefile>"><img src="<mm:treefile page="/progress/gfx/question.gif" objectlist="$includePath" referids="$referids"/>" title="?" alt="?" border="0"></a>
                                    </td>
                                 </mm:islessthan>
                                 <mm:islessthan referid="rights" referid2="RIGHTS_RW">
                                    <td class="td_test_tbs" style="border-color:#000000; border-top:0px; border-left:0px"><img src="<mm:treefile page="/progress/gfx/question.gif" objectlist="$includePath" referids="$referids"/>" title="?" alt="?" border="0"></td>
                                 </mm:islessthan>
                              </mm:compare>

                              <mm:compare referid="teststatus" value="passed">
                                 <td class="td_test_tbs" style="border-color:#000000; border-top:0px; border-left:0px">
                                    <img src="<mm:treefile page="/progress/gfx/checked.gif" objectlist="$includePath" referids="$referids"/>" title="Ok" alt="Ok" border="0">
                                 </td>
                              </mm:compare>

                              <mm:compare referid="teststatus" value="failed">
                                 <td class="td_test_failed" style="border-color:#000000; border-top:0px; border-left:0px">
                                    <img src="<mm:treefile page="/progress/gfx/box.gif" objectlist="$includePath" referids="$referids"/>" alt="" border="0">
                                 </td>
                              </mm:compare>
                           </mm:compare>

                           <mm:compare referid="teststatus" value="incomplete" >
                              <td class="td_test_not_done" style="border-color:#000000; border-top:0px; border-left:0px">
                                 <img src="<mm:treefile page="/progress/gfx/box.gif" objectlist="$includePath" referids="$referids"/>" alt="" border="0">
                              </td>
                           </mm:compare>

                           <mm:remove referid="madetestscore"/>
                           <mm:remove referid="save_madetestscore"/>
                           <mm:remove referid="testNo"/>
                        <%
                     }
                     testCounter++;
                  %>
                  </mm:compare>

                  <mm:remove referid="nodetype"/>
               </mm:tree>
            </mm:relatednodescontainer> <%-- learnobjects --%>
         </mm:node> <%-- education --%>

         <mm:remove referid="copybookNo"/>
      </tr>
   </mm:node> <%-- student --%>
<%--
</di:may>
--%>

</mm:cloud>
</mm:content>

