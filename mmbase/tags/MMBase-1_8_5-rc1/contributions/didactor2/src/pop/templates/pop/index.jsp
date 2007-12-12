<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>

<mm:content postprocessor="reducespace">
<mm:cloud method="delegate" jspvar="cloud">
<%@include file="/shared/setImports.jsp" %>
<%@include file="/education/wizards/roles_defs.jsp" %>
<mm:import id="editcontextname" reset="true">docent schermen</mm:import>
<%@include file="/education/wizards/roles_chk.jsp" %>
  <mm:treeinclude page="/cockpit/cockpit_header.jsp" objectlist="$includePath" referids="$referids">
    <mm:param name="extraheader">
      <title>POP</title>
      <link rel="stylesheet" type="text/css" href="css/pop.css" />
    </mm:param>
  </mm:treeinclude>

  <%@include file="getids.jsp" %>

  <mm:islessthan referid="rights" referid2="RIGHTS_RW">
    <mm:import id="t_mode" reset="true">-1</mm:import>
    <mm:import id="whatselected" reset="true">student</mm:import>
  </mm:islessthan>

  <div class="rows">

    <div class="navigationbar">
      <div class="titlebar">
        <img src="<mm:treefile write="true" page="/gfx/icon_pop.gif" objectlist="$includePath" />" 
            width="25" height="13" border="0" title="<di:translate key="pop.popfull" />" alt="<di:translate key="pop.popfull" />" /> <di:translate key="pop.popfull" />
      </div>		
    </div>

    <%@include file="leftpanel.jsp" %> 

    <%-- right section --%>
    <mm:compare referid="whatselected" value="student">
      <mm:treeinclude page="/pop/s_rightpanel.jsp" objectlist="$includePath" referids="$referids"/>
    </mm:compare>
    <mm:compare referid="whatselected" value="student" inverse="true">
      <mm:compare referid="t_mode" value="true" inverse="true">
        <mm:treeinclude page="/pop/s_rightpanel.jsp" objectlist="$includePath" referids="$referids"/>
      </mm:compare>
      <mm:compare referid="t_mode" value="true">
        <mm:treeinclude page="/pop/t_rightpanel.jsp" objectlist="$includePath" referids="$referids">
          <mm:param name="wgroup"><mm:write referid="wgroup"/></mm:param>
          <mm:param name="whatselected"><mm:write referid="whatselected"/></mm:param>
        </mm:treeinclude>
      </mm:compare>
    </mm:compare>
  </div>
  <mm:treeinclude page="/cockpit/cockpit_footer.jsp" objectlist="$includePath" referids="$popreferids" />
</mm:cloud>
</mm:content>
