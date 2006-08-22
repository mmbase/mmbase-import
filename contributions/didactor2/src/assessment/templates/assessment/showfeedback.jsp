<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.1" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>

<mm:content postprocessor="reducespace">
<mm:cloud method="delegate" jspvar="cloud">
<%@include file="/shared/setImports.jsp" %>

<mm:import externid="feedback_n">-1</mm:import>
<mm:import externid="coachmode">false</mm:import>

<% 
   String classrelId = "-1";
   String ownerId = "-1";
   String lessonId = "-1";
%>

  <mm:treeinclude page="/cockpit/cockpit_header.jsp" objectlist="$includePath" referids="$referids">
    <mm:param name="extraheader">
      <title><di:translate key="assessment.assessment_matrix" /></title>
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
      
        <mm:node number="$feedback_n" notfound="skip">
          <mm:relatednodes type="classrel">
            <mm:field name="number" jspvar="this_classrel" vartype="String" write="false">
              <% classrelId = this_classrel; %>
              <mm:list path="people,classrel,learnblocks" constraints="<%= "classrel.number=" + this_classrel %>">
                <mm:field name="people.number" jspvar="dummy" vartype="String" write="false">
                  <% ownerId = dummy; %>
                </mm:field>
                <mm:field name="learnblocks.number" jspvar="dummy" vartype="String" write="false">
                  <% lessonId = dummy; %>
                </mm:field>
              </mm:list>
            </mm:field>
          </mm:relatednodes>
          
          <table width="600px" style="FONT-SIZE : 1.3em">
            <tr>
              <td><b>
                <mm:node number="<%= lessonId %>"><mm:field name="name"/></mm:node> by 
                <mm:node number="<%= ownerId %>"><mm:field name="firstname"/> <mm:field name="lastname"/></mm:node>
              </b></td>
              <td style="text-align:right">
                <form name="backform" action="<mm:treefile page="/assessment/index.jsp" objectlist="$includePath"
                       referids="coachmode?,$referids"/>" method="post">
                  <input type="submit" class="formbutton" value="<di:translate key="assessment.back" />">
                </form>
              </td>
            </tr>
            <mm:field name="status" jspvar="dummy" vartype="String" write="false">
              <% if ("-1".equals(dummy)) { %>
                   <tr>
                     <td colspan="2">
                       <mm:relatednodes type="people" max="1">
                         <div class="grayBar" style="width:100%;">
                           <di:translate key="assessment.feedback_by" /> <mm:field name="firstname"/> <mm:field name="lastname"/>
                         </div>
                       </mm:relatednodes>
                       <mm:field name="text"/>
                     </td>
                   </tr>
              <% } %>
            </mm:field>
            <%@ include file="includes/feedbackgoal.jsp" %>
            <%@ include file="includes/feedbacklesson.jsp" %>
          </table>
        </mm:node>
      </div>
    </div>
  </div>
  <mm:treeinclude page="/cockpit/cockpit_footer.jsp" objectlist="$includePath" referids="$referids" />
</mm:cloud>
</mm:content>
