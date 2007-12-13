<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>

<mm:content postprocessor="reducespace">
<mm:cloud method="delegate" jspvar="cloud">
  <%@include file="/shared/setImports.jsp" %>
  
  <mm:import externid="test">-1</mm:import>
  <mm:import externid="madetest">-1</mm:import>
  <mm:import externid="coachmode">false</mm:import>
  
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
    
    
    <%-- right section --%>
    <div class="mainContent">
      <div class="contentBody">
        <form name="backform" action="<mm:treefile page="/assessment/index.jsp" objectlist="$includePath"
                 referids="coachmode?,$referids"/>" method="post">
          <input type="submit" class="formbutton" value="<di:translate key="assessment.back" />">
        </form>
        <mm:node number="$test">
          <mm:relatednodes type="questions" path="posrel,questions" orderby="posrel.pos">
            <mm:import id="page" reset="true">/education/<mm:nodeinfo type="type"/>/index.jsp</mm:import>
            <mm:treeinclude page="$page" objectlist="$includePath" referids="$referids">
              <mm:param name="question"><mm:field name="number"/></mm:param>
              <mm:param name="testnumber"><mm:write referid="test"/></mm:param>
              <mm:param name="madetest"><mm:write referid="madetest"/></mm:param>
            </mm:treeinclude>
          </mm:relatednodes>
        </mm:node>
      </div>
    </div>
  </div>
  <mm:treeinclude page="/cockpit/cockpit_footer.jsp" objectlist="$includePath" referids="$referids" />
</mm:cloud>
</mm:content>
