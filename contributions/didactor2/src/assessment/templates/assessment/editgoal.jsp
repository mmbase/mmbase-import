<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.1" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>

<mm:content postprocessor="reducespace">
<mm:cloud method="delegate" jspvar="cloud">
<%@include file="/shared/setImports.jsp" %>

<mm:import externid="step">-1</mm:import>
<mm:import externid="goal_n">-1</mm:import>
<mm:import externid="goalname"/>
<mm:import externid="goaldesc"/>

<mm:compare referid="step" value="cancel">
  <mm:redirect page="/assessment/index.jsp" referids="$referids"/>
</mm:compare>

<mm:compare referid="step" value="save">
  <mm:compare referid="goal_n" value="-1">
    <mm:maycreate type="goals">
      <mm:remove referid="goal_n"/>
      <mm:createnode type="goals" id="goal_n">
      </mm:createnode>
      <mm:createrelation role="posrel" source="user" destination="goal_n"/>
    </mm:maycreate>
  </mm:compare>
  
  <mm:node referid="goal_n" notfound="skip">
    <mm:setfield name="name"><mm:write referid="goalname"/></mm:setfield>
    <mm:setfield name="description"><mm:write referid="goaldesc"/></mm:setfield>
  </mm:node>

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

  <mm:node number="$goal_n" notfound="skip">
    <mm:import id="goalname" reset="true"><mm:field name="name"/></mm:import>
    <mm:import id="goaldesc" reset="true"><mm:field name="description"/></mm:import>
  </mm:node>
  
  <form name="newgoalform" action="<mm:treefile page="/assessment/editgoal.jsp" objectlist="$includePath" 
          referids="$referids"/>" method="post">
    <input type="hidden" name="step" value="save">
    <input type="hidden" name="goal_n" value="<mm:write referid="goal_n"/>">
    <table class="font" width="90%">
      <tr>
        <td width="80">Goal:</td>
        <td><input name="goalname" class="popFormInput" type="text" size="50" maxlength="255" value="<mm:write referid="goalname"/>"></td>
      </tr>
      <tr>
        <td>Description:</td>
        <td><textarea name="goaldesc" class="popFormInput" cols="50" rows="5"><mm:write referid="goaldesc"/></textarea></td>
      </tr>
    </table>
    <input type="submit" class="formbutton" value="save">
    <input type="submit" class="formbutton" value="cancel" onClick="newgoalform.step.value='cancel'">
  </form>



      </div>
    </div>
  </div>
  <mm:treeinclude page="/cockpit/cockpit_footer.jsp" objectlist="$includePath" referids="$referids" />
</mm:cloud>
</mm:content>
