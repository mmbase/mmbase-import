<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<%@page import="org.mmbase.bridge.*" %>

<mm:cloud method="delegate" jspvar="cloud">
<mm:content postprocessor="reducespace">
<%@include file="/shared/setImports.jsp" %>

<%@include file="includes/geteducation.jsp" %>
<%@include file="includes/getlesson.jsp" %>
<%@include file="includes/variables.jsp" %>
<%@include file="includes/functions.jsp" %>
<%@include file="/education/tests/definitions.jsp" %>

<% // find users copybook %>
<mm:import externid="class" reset="true">null</mm:import>
<mm:import externid="education" reset="true"><mm:node number="$assessment_education"><mm:field name="number"/></mm:node></mm:import>
<mm:node number="$user">
  <%@include file="/education/tests/find_copybook.jsp"%>
</mm:node>

<mm:import externid="step">-1</mm:import>
<mm:import externid="problem_n">-1</mm:import>
<mm:import externid="i_lesson">-1</mm:import>
<mm:import externid="problemname"/>
<mm:import externid="problemtype">-1</mm:import>
<mm:import externid="problemrating"/>
<mm:import externid="madetest_n">-1</mm:import>

<% int problemrating = -1; // not rated %>

<!--
user <mm:write referid="user"/><br/>
class <mm:write referid="class"/><br/>
copybook <mm:write referid="copybookNo"/><br/>
step <mm:write referid="step"/><br/>
problem_n <mm:write referid="problem_n"/><br/>
i_lesson <mm:write referid="i_lesson"/><br/>
problemname <mm:write referid="problemname"/><br/>
problemtype <mm:write referid="problemtype"/><br/>
problemrating <mm:write referid="problemrating"/><br/>
madetest_n <mm:write referid="madetest_n"/><br/>
currentLesson <%= currentLesson %><br/>
-->
<mm:compare referid="step" value="cancel">
  <mm:redirect page="/assessment/index.jsp" referids="$referids"/>
</mm:compare>

<mm:compare referid="step" value="save">
  <mm:compare referid="problem_n" value="-1">
    <mm:maycreate type="problems">
      <mm:remove referid="problem_n"/>
      <mm:createnode type="problems" id="problem_n">
      </mm:createnode>
      <mm:createrelation role="posrel" source="user" destination="problem_n">
        <mm:setfield name="pos"><%= getMaxPos(cloud,thisUser,"problems")+1 %></mm:setfield>
      </mm:createrelation>
    </mm:maycreate>
  </mm:compare>
  
  <mm:node referid="problem_n" notfound="skip">
    <mm:setfield name="name"><mm:write referid="problemname"/></mm:setfield>
    <mm:related path="posrel,learnblocks" constraints="<%= "learnblocks.number=" + currentLesson %>">
      <mm:node element="posrel">
        <mm:deletenode/>
      </mm:node>
    </mm:related>
    <mm:node number="<%= currentLesson %>" id="currentlesson" notfound="skip">
      <mm:createrelation role="posrel" source="problem_n" destination="currentlesson">
        <mm:setfield name="pos"><mm:write referid="problemrating"/></mm:setfield>
      </mm:createrelation>
    </mm:node>
    <mm:related path="related,problemtypes">
      <mm:node element="related">
        <mm:deletenode/>
      </mm:node>
    </mm:related>
    <mm:createrelation role="related" source="problem_n" destination="problemtype"/>
    <mm:related path="related,madetests" constraints="madetests.number=$madetest_n">
      <mm:node element="related">
        <mm:deletenode/>
      </mm:node>
    </mm:related>
    <mm:node number="madetest_n" notfound="skip">
      <mm:createrelation role="related" source="problem_n" destination="madetest_n"/>
    </mm:node>
  </mm:node>
  <mm:node number="$provider" notfound="skip">
    <mm:related path="related,educations,related,tests,related,problemtypes" constraints="problemtypes.number=$problemtype">
      <mm:node element="tests">
        <mm:field name="tests.number" jspvar="this_test" vartype="String" write="false">
          <%@include file="includes/getmadetest.jsp" %>
          <mm:relatednodes type="questions" path="posrel,questions" orderby="posrel.pos">
            <mm:import id="page" reset="true">/education/<mm:nodeinfo type="type"/>/rate<mm:nodeinfo type="type"/>.jsp</mm:import>
            <mm:treeinclude page="$page" objectlist="$includePath" referids="$referids">
              <mm:param name="question"><mm:field name="number"/></mm:param>
              <mm:param name="madetest"><mm:write referid="madetest"/></mm:param>
            </mm:treeinclude>
          </mm:relatednodes>
        </mm:field>
      </mm:node>
    </mm:related>
  </mm:node>

  <mm:redirect page="/assessment/index.jsp" referids="$referids"/>
</mm:compare>

  <mm:treeinclude page="/cockpit/cockpit_header.jsp" objectlist="$includePath" referids="$referids">
    <mm:param name="extraheader">
      <title><di:translate key="assessment.assessment_matrix" /></title>
      <link rel="stylesheet" type="text/css" href="css/assessment.css" />
    </mm:param>
  </mm:treeinclude>

  <div class="rows">
    <div class="navigationbar">
      <div class="titlebar">
        <img src="<mm:treefile write="true" page="/gfx/icon_pop.gif" objectlist="$includePath" />" 
            width="25" height="13" border="0" title="<di:translate key="assessment.assessment_matrix" />" alt="<di:translate key="assessment.assessment_matrix" />" /> <di:translate key="assessment.assessment_matrix" />
      </div>		
    </div>

<div class="folders">
  <div class="folderHeader">
  </div>
  <div class="folderBody">
  </div>
</div>


    <% // right section %>
    <div class="mainContent">
      <div class="contentBody">

  <mm:node number="$problem_n" notfound="skip">
    <mm:import id="problemname" reset="true"><mm:field name="name"/></mm:import>
    <mm:related path="posrel,learnblocks" constraints="<%= "learnblocks.number=" + currentLesson %>">
      <mm:field name="posrel.pos" jspvar="problem_weight" vartype="Integer" write="false">
<%
        try {
          problemrating = problem_weight.intValue();
        }
        catch (Exception e) {
        }
%>
      </mm:field>
    </mm:related>
    <mm:compare referid="problemtype" value="-1">
      <mm:relatednodes type="problemtypes">
        <mm:import id="problemtype" reset="true"><mm:field name="number"/></mm:import>
      </mm:relatednodes>
    </mm:compare>
  </mm:node>
  <mm:compare referid="problemtype" value="-1">
    <mm:listnodes type="problemtypes" orderby="pos" max="1">
        <mm:import id="problemtype" reset="true"><mm:field name="number"/></mm:import>
    </mm:listnodes>
  </mm:compare>

  <form name="questionform" action="<mm:treefile page="/assessment/editproblem.jsp" objectlist="$includePath" 
          referids="$referids"/>" method="post">
    <input type="hidden" name="step" value="save">
    <input type="hidden" name="problem_n" value="<mm:write referid="problem_n"/>">
    <input type="hidden" name="i_lesson" value="<mm:write referid="i_lesson"/>">
    <table class="font" width="70%">
      <tr>
        <td width="80" style="vertical-align:top"><di:translate key="assessment.problem" />:</td>
        <td align="right"><textarea style="width:400px" name="problemname" class="popFormInput" cols="50" rows="4"><mm:write referid="problemname"/></textarea></td>
      </tr>
      <tr>
        <td><di:translate key="assessment.type" /></td>
        <td align="right">
          <select name="problemtype" style="width:400px" onchange="questionform.step.value='next';questionform.submit();">
            <mm:listnodes type="problemtypes" orderby="pos">
              <mm:field name="number" jspvar="problemtypeId" vartype="String">
                <option value="<%= problemtypeId %>"<mm:compare referid="problemtype" value="<%= problemtypeId %>"> selected</mm:compare>
                        ><mm:field name="key" jspvar="dummy" vartype="String" write="false"
                           ><di:translate key="<%= "assessment." + dummy %>"
                        /></mm:field></option>
              </mm:field>
            </mm:listnodes>
          </select>
        </td>
      </tr>
      <tr>
        <td><di:translate key="assessment.how_much_trouble" /></td>
        <td align="right">
          <select name="problemrating" style="width:400px">
            <% for(int i=2;i<=10;i+=2) { %>
              <option value="<%= i %>"<% if (problemrating == i) {%> selected<% } %>><%= problemWeights[i] %></option>
            <% } %>
          </select>
        </td>
      </tr>
      <% // first lesson does not have a form 
      %>
      <mm:isgreaterthan referid="i_lesson" value="0">
        <mm:node number="<%= currentLesson %>" notfound="skip">
          <tr>
            <td style="padding-top:30px;" colspan="2">
              <mm:node number="$problemtype">
                 <mm:field name="key" jspvar="dummy" vartype="String" write="false">
                   <h2><di:translate key="<%= "assessment.strategy_for_" + dummy %>" /></h2>
                 </mm:field>
              </mm:node>
            </td>
          </tr>
          <tr>
            <td colspan="2">
              <mm:node number="$provider" notfound="skip">
                <mm:related path="related,educations,related,tests,related,problemtypes" constraints="problemtypes.number=$problemtype">
                  <mm:node element="tests">
                    <mm:field name="number" jspvar="this_test" vartype="String" write="false">
                      <%@include file="includes/getmadetest.jsp" %>
                      <input type="hidden" name="madetest_n" value="<mm:write referid="madetest"/>">
                      <mm:relatednodes type="questions" path="posrel,questions" orderby="posrel.pos">
                        <mm:import id="page" reset="true">/education/<mm:nodeinfo type="type"/>/index.jsp</mm:import>
                        <mm:treeinclude page="$page" objectlist="$includePath" referids="$referids">
                          <mm:param name="question"><mm:field name="number"/></mm:param>
                          <mm:param name="testnumber"><%= this_test %></mm:param>
                          <mm:param name="madetest"><mm:write referid="madetest"/></mm:param>
                        </mm:treeinclude>
                      </mm:relatednodes>
                    </mm:field>
                  </mm:node>
                </mm:related>
              </mm:node>
            </td>
          </tr>
        </mm:node>
      </mm:isgreaterthan>
    </table>
    <input type="submit" class="formbutton" value="<di:translate key="assessment.save" />">
    <input type="submit" class="formbutton" value="<di:translate key="assessment.cancel" />" onClick="questionform.step.value='cancel'">
  </form>



      </div>
    </div>
  </div>
  <mm:treeinclude page="/cockpit/cockpit_footer.jsp" objectlist="$includePath" referids="$referids" />
</mm:content>
</mm:cloud>
