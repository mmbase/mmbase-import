<jsp:root
    xmlns:jsp="http://java.sun.com/JSP/Page"
    version="2.0">

  <di:html
      description="bla bla"
      title_key="assessment.assessment_matrix"
      xmlns:jsp="http://java.sun.com/JSP/Page"
      xmlns:mm="http://www.mmbase.org/mmbase-taglib-2.0"
      xmlns:di="http://www.didactor.nl/ditaglib_1.0"
      component="assessmment" >

    <mm:import externid="coachmode">
      <di:hasrole role="coach">true</di:hasrole>
      <di:hasrole role="coach" inverse="true">false</di:hasrole>
    </mm:import>

    <script type="text/javascript"
            src="${mm:treelink('/assessment/javascript.js', includePath)}">
      <jsp:text> </jsp:text>
    </script>

    <div class="rows">

      <div class="navigationbar">
        <div class="titlebar">
          <img src="${mm:treelink('/gfx/icon_pop.gif', includePath)}"
               width="25" height="13" border="0"
               title="${di:translate('assessment.assessment_matrix')}"
               alt="${di:translate('assessment.assessment_matrix')}"
               /> <di:translate key="assessment.assessment_matrix" />
        </div>
      </div>



      <!-- right section -->
      <div class="mainContent">
        <div class="contentBody">
          <di:hasrole role="student">
            <di:hasrole role="coach">
              <form name="coachform" method="post">
                <select name="coachmode"
                        onChange="coachform.submit();">
                  <mm:option value="true" compare="${coachmode}"><di:translate key="assessment.overview_students" /></mm:option>
                  <mm:option value="false" compare="${coachmode}"><di:translate key="assessment.personal_assessment" /></mm:option>
                </select>
              </form>
            </di:hasrole>
          </di:hasrole>
          <mm:compare referid="coachmode" value="false">
            <di:include debug="html" page="/assessment/for_student.jsp" />
          </mm:compare>
        <mm:compare referid="coachmode" value="true">
          <di:include debug="html" page="/assessment/for_coach.jsp" />
        </mm:compare>
        </div>
      </div>
    </div>
  </di:html>
</jsp:root>
