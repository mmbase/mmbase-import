<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.1" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<%@page import="org.mmbase.bridge.*,java.util.ArrayList" %>

<mm:content postprocessor="reducespace">
<mm:cloud method="delegate" jspvar="cloud">
<%@include file="/shared/setImports.jsp" %>

<mm:import externid="coachmode">false</mm:import>

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

    function toggleAll(image,number) {
      var toggles = number.split(",");
      if( document.getElementById("toggle_div" + toggles[0]).style.display=='none' ){
        for (i=0;i<toggles.length;i++) {
          document.getElementById("toggle_div" + toggles[i]).style.display = '';
        }
        document.getElementById("toggle_image" + image).src = "<mm:treefile page="/assessment/gfx/minus.gif"
                                  objectlist="$includePath" referids="$referids"/>";
      } else {
        for (i=0;i<toggles.length;i++) {
          document.getElementById("toggle_div" + toggles[i]).style.display = 'none';
        }
        document.getElementById("toggle_image" + image).src = "<mm:treefile page="/assessment/gfx/plus.gif"
                                  objectlist="$includePath" referids="$referids"/>";
      }
    }
    function doClose(prompt) {
    var conf;
    if (prompt && prompt!="") {
       conf = confirm(prompt);
    }
    else
      conf=true;
      return conf;
    }
  </script>

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
        <mm:node number="$user" notfound="skip">
          <% boolean isStudent = false;
             boolean isCoach = false;
          %>
          <mm:relatednodes type="roles" path="related,roles">
            <mm:field name="name" jspvar="dummy" vartype="String">
              <% if ("student".equals(dummy)) { isStudent = true; }
                 if ("teacher".equals(dummy)) { isCoach = true; }
              %>
            </mm:field>
          </mm:relatednodes>
          <% if (isStudent && isCoach) { %>
               <form name="coachform" action="<mm:treefile page="/assessment/index.jsp" objectlist="$includePath"
                   referids="$referids"/>" method="post">
                 <select name="coachmode" style="width:300px" onChange="coachform.submit();">
                   <option value="true" <mm:compare referid="coachmode" value="true">selected</mm:compare>
                     ><di:translate key="assessment.overview_students" /></option>
                   <option value="false" <mm:compare referid="coachmode" value="false">selected</mm:compare>
                     ><di:translate key="assessment.personal_assessment" /></option>
                 </select>
               </form>
               <br/>
          <% } %>
          <% if (!isCoach) { %>
               <mm:import id="coachmode" reset="true">false</mm:import>
          <% } %>
          <mm:compare referid="coachmode" value="false">
            <mm:treeinclude page="/assessment/for_student.jsp" objectlist="$includePath" referids="$referids"/>
          </mm:compare>
          <mm:compare referid="coachmode" value="true">
            <mm:treeinclude page="/assessment/for_coach.jsp" objectlist="$includePath" referids="$referids">
              <mm:param name="coachmode">true</mm:param>
            </mm:treeinclude>
          </mm:compare>
        </mm:node>
      </div>
    </div>
  </div>
  <mm:treeinclude page="/cockpit/cockpit_footer.jsp" objectlist="$includePath" referids="$referids" />
</mm:cloud>
</mm:content>
