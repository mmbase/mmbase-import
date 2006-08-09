<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.1" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>

<mm:content postprocessor="reducespace">
<mm:cloud method="delegate" jspvar="cloud">
<%@include file="/shared/setImports.jsp" %>
<%@include file="includes/getlesson.jsp" %>

<mm:import externid="step">-1</mm:import>
<mm:import externid="problem_n">-1</mm:import>
<mm:import externid="problemname"/>
<mm:import externid="problemtype"/>
<mm:import externid="problemrating"/>

<% int problemrating = -1; // not rated %>

<mm:compare referid="step" value="cancel">
  <mm:redirect page="/assessment/index.jsp" referids="$referids"/>
</mm:compare>

<mm:compare referid="step" value="save">
  <mm:compare referid="problem_n" value="-1">
    <mm:maycreate type="problems">
      <mm:remove referid="problem_n"/>
      <mm:createnode type="problems" id="problem_n">
      </mm:createnode>
      <mm:createrelation role="posrel" source="user" destination="problem_n"/>
    </mm:maycreate>
  </mm:compare>
  
  <mm:node referid="problem_n" notfound="skip">
    <mm:setfield name="name"><mm:write referid="problemname"/></mm:setfield>
    <mm:related path="posrel,learnblocks" constraints="<%= "learnblocks.number=" + currentLesson %>">
      <mm:node element="posrel">
        <mm:setfield name="pos"><mm:write referid="problemrating"/></mm:setfield>
      </mm:node>
    </mm:related>
  </mm:node>
  <% // TODO: saving q and a %>
  
  
  <mm:redirect page="/assessment/index.jsp" referids="$referids"/>
</mm:compare>

  <mm:treeinclude page="/cockpit/cockpit_header.jsp" objectlist="$includePath" referids="$referids">
    <mm:param name="extraheader">
      <title>Assessment matrix</title>
      <link rel="stylesheet" type="text/css" href="css/assessment.css" />
    </mm:param>
  </mm:treeinclude>

  <%@include file="includes/variables.jsp" %>

  <div class="rows">
    <div class="navigationbar">
      <div class="titlebar">
        <img src="<mm:treefile write="true" page="/gfx/icon_pop.gif" objectlist="$includePath" />" 
            width="25" height="13" border="0" title="<di:translate key="pop.popfull" />" alt="<di:translate key="pop.popfull" />" /> <di:translate key="pop.popfull" />
      </div>		
    </div>

<div class="folders">
  <div class="folderHeader">
  </div>
  <div class="folderBody">
  </div>
</div>


    <%-- right section --%>
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
  </mm:node>
  
  <form name="newproblemform" action="<mm:treefile page="/assessment/editproblem.jsp" objectlist="$includePath" 
          referids="$referids"/>" method="post">
    <input type="hidden" name="step" value="save">
    <input type="hidden" name="problem_n" value="<mm:write referid="problem_n"/>">
    <table class="font" width="90%">
      <tr>
        <td width="80">problem:</td>
        <td><input name="problemname" class="popFormInput" type="text" size="50" maxlength="255" value="<mm:write referid="problemname"/>"></td>
      </tr>
      <tr>
        <td>How much trouble does it cause you?</td>
        <td>
          <select name="problemrating">
            <% for(int i=2;i<=10;i+=2) { %>
              <option value="<%= i %>"<% if (problemrating == i) {%> selected<% } %>><%= problemWeights[i] %></option>
            <% } %>
          </select>
        </td>
      </tr>
    </table>
    <input type="submit" class="formbutton" value="save">
    <input type="submit" class="formbutton" value="cancel" onClick="newproblemform.step.value='cancel'">
  </form>



      </div>
    </div>
  </div>
  <mm:treeinclude page="/cockpit/cockpit_footer.jsp" objectlist="$includePath" referids="$referids" />
</mm:cloud>
</mm:content>
