<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>

<mm:content postprocessor="reducespace">
<mm:cloud method="delegate" jspvar="cloud">
<%@include file="/shared/setImports.jsp" %>

<mm:import externid="feedback_n">-1</mm:import>
<mm:import externid="coachmode">false</mm:import>

<mm:node number="$feedback_n" notfound="skip">
  <mm:field name="status" write="false">
    <mm:compare value="-1">
      <mm:import id="page"><mm:url page="showfeedback.jsp" referids="$referids" escapeamps="false">
                             <mm:param name="feedback_n"><mm:write referid="feedback_n"/></mm:param>
                           </mm:url></mm:import>
      <mm:redirect page="$page"/>
    </mm:compare>
  </mm:field>
</mm:node>

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
                <mm:node number="<%= lessonId %>"><mm:field name="name"/></mm:node> <di:translate key="assessment.by" /> 
                <mm:node number="<%= ownerId %>"><mm:field name="firstname"/> <mm:field name="lastname"/></mm:node>
              </b></td>
              <td></td>
            </tr>
            <%@ include file="includes/feedbackgoal.jsp" %>
            <%@ include file="includes/feedbacklesson.jsp" %>
            <mm:field name="status" jspvar="dummy" vartype="String" write="false">
            <% if ("0".equals(dummy)) { %>
                 <tr>
                   <td colspan="2">
                     <div class="grayBar" style="width:100%;">
                       <di:translate key="assessment.feedback" />
                     </div>
                     <br/>
                     <form name="feedbackform" action="<mm:treefile page="/assessment/savefeedback.jsp"
                          objectlist="$includePath" referids="coachmode?,$referids"/>" method="post">
                       <input type="hidden" name="step" value="save">
                       <input type="hidden" name="feedback_n" value="<mm:write referid="feedback_n"/>">
                       <textarea name="feedbacktext" class="popFormInput" cols="50" rows="5"></textarea>
                       <br/><br/>
                       <input type="submit" class="formbutton" value="<di:translate key="assessment.save" />">
                       <input type="submit" class="formbutton" value="<di:translate key="assessment.cancel" />" onClick="feedbackform.step.value='cancel'">
                     </form>
                   </td>
                 </tr>
            <% } %>
            </mm:field>
          </table>
        </mm:node>
      </div>
    </div>
  </div>
  <mm:treeinclude page="/cockpit/cockpit_footer.jsp" objectlist="$includePath" referids="$referids" />
</mm:cloud>
</mm:content>
