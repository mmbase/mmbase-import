<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.1" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>

<mm:content postprocessor="reducespace">
<mm:cloud method="delegate" jspvar="cloud">
<%@include file="/shared/setImports.jsp" %>

<mm:import externid="feedback_n">-1</mm:import>

  <mm:treeinclude page="/cockpit/cockpit_header.jsp" objectlist="$includePath" referids="$referids">
    <mm:param name="extraheader">
      <title>Assessment matrix</title>
      <link rel="stylesheet" type="text/css" href="css/assessment.css" />
    </mm:param>
  </mm:treeinclude>

  <script language="JavaScript" type="text/javascript">
    function toggle(number) {
      if( document.getElementById("toggle_div" + number).style.display=='none' ){
        document.getElementById("toggle_div" + number).style.display = '';
        document.getElementById("toggle_image" + number).src = "<mm:treefile page="/assessment/gfx/minus.gif"
                                  objectlist="$includePath" referids="$referids"/>";
      } else {
        document.getElementById("toggle_div" + number).style.display = 'none';
        document.getElementById("toggle_image" + number).src = "<mm:treefile page="/assessment/gfx/plus.gif"
                                  objectlist="$includePath" referids="$referids"/>";
      }
    }
  </script>

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
        <mm:node referid="feedback_n" notfound="skip">
          <% String classrelId = "-1"; %>
          <mm:relatednodes type="classrel">
            <mm:field name="number" jspvar="dummy" vartype="String" write="false">
              <% classrelId = dummy; %>
            </mm:field>
          </mm:relatednodes>
          <mm:list path="people,classrel,learnblocks" constraints="<%= "classrel.number=" + classrelId %>">
            <mm:field name="people.number" jspvar="ownerId" vartype="String" write="false">
              <mm:field name="learnblocks.number" jspvar="lessonId" vartype="String" write="false">
                <%@ include file="includes/feedbackgoal.jsp" %>
                <%@ include file="includes/feedbacklesson.jsp" %>
              </mm:field>
            </mm:field>
          </mm:list>
        </mm:node>
      </div>
    </div>
  </div>
  <mm:treeinclude page="/cockpit/cockpit_footer.jsp" objectlist="$includePath" referids="$referids" />
</mm:cloud>
</mm:content>
