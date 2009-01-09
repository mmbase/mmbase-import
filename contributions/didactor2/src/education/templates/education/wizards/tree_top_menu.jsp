<jsp:root version="2.0"
          xmlns:jsp="http://java.sun.com/JSP/Page"
          xmlns:c="http://java.sun.com/jsp/jstl/core"
          xmlns:fn="http://java.sun.com/jsp/jstl/functions"
          xmlns:mm="http://www.mmbase.org/mmbase-taglib-2.0"
          xmlns:di="http://www.didactor.nl/ditaglib_1.0">
  <mm:content>
    <mm:cloud rank="editor">

      <mm:import externid="e" jspvar="chosen_education" vartype="string">${education}</mm:import>
      <style type="text/css">
        .education_top_menu {
        font-weight: bold;
        }
        .education_top_menu.selected {
        background:#888586;
        }
      </style>

      <mm:import externid="mode">educations</mm:import>

      <mm:node number="$user" >
        <mm:hasrank value="administrator">
          <mm:listnodes type="educations" id="educations" />
        </mm:hasrank>
        <mm:hasrank value="administrator" inverse="true">
          <mm:nodelistfunction name="educations" id="educations" />
        </mm:hasrank>
      </mm:node>


      <!--
           TODO TODO
           Follows lots of code duplication, why not iterate over all modes or so?
           Will the sillyness ever stop.
      -->
      <di:has editcontext="componenten">
        <mm:link referids="e?" page=".">
          <mm:param name="mode">components</mm:param>
          <a class="${mode eq 'components' ? education_top_menu_selected : ''}"
             href="${_}"><di:translate key="education.educationmenucomponents" /></a>
        </mm:link>
      </di:has>

      <di:has  editcontext="rollen">
        <mm:link page="." referids="e?">
          <mm:param name="mode">roles</mm:param>
          <a class="education_top_menu ${mode eq 'roles' ? 'selected' : ''}"
             href="${_}"><di:translate key="education.educationmenupersons" /></a>
        </mm:link>
      </di:has>

      <c:if test="${di:setting('core', 'pagereporter') eq 'true'}">
        <mm:link page="." referids="e?">
          <mm:param name="mode">pagereports</mm:param>
          <a class="education_top_menu ${mode eq 'pagereports' ? 'selected' : ''}"
             href="${_}"><di:translate key="core.pagereports" /></a>
        </mm:link>
      </c:if>


      <mm:hasnode number="component.pop">
        <mm:node number="component.pop">
          <mm:relatednodes type="providers" constraints="providers.number=$provider"> <!-- WTF -->
            <di:has  editcontext="competentie">
              <mm:link page="." referids="e?">
                <mm:param name="mode">compentence</mm:param>
                <a class="education_top_menu ${mode eq 'compentence' ? 'selected' : ''}"
                   href="${_}">
                <di:translate key="education.educationmenucompetence" /></a>
              </mm:link>
            </di:has>
          </mm:relatednodes>
        </mm:node>
      </mm:hasnode>

      <mm:hasnode number="component.metadata" >
        <di:has  editcontext="metadata">
          <mm:link page="." referids="e?">
            <mm:param name="mode">metadata</mm:param>
            <a class="education_top_menu ${mode eq 'metadata' ? 'selected' : ''}"
               href="${_}">
            <di:translate key="education.educationmenumetadata" /></a>
          </mm:link>
        </di:has>
      </mm:hasnode>

      <di:has editcontext="contentelementen">
        <mm:link page="." referids="e?">
          <mm:param name="mode">content_metadata</mm:param>
          <a class="education_top_menu ${mode eq 'content_metadata' ? 'selected' : ''}"
             href="${_}">
          <di:translate key="education.educationmenucontentmetadata" /></a>
        </mm:link>
      </di:has>

      <mm:hasnode number="component.filemanagement">
        <di:has editcontext="filemanagment">
          <mm:link page="." referids="e?">
            <mm:param name="mode">filemanagement</mm:param>
            <a class="education_top_menu ${mode eq 'filemanagement' ? 'selected' : ''}"
               href="${_}">
            <di:translate key="filemanagement.filemanagement" /></a>
          </mm:link>
        </di:has>
      </mm:hasnode>

      <!-- this is stupid -->

      <mm:hasnode number="component.virtualclassroom">
        <di:has editcontext="virtualclassroom">
          <a class="education_top_menu ${mode eq 'virtualclassroom' ? 'selected' : ''}"
             href="?mode=virtualclassroom" style="font-weight:bold;">
            <di:translate key="virtualclassroom.virtualclassroom" />
          </a>
        </di:has>
      </mm:hasnode>

      <mm:hasnode number="component.proactivemail">
        <di:has editcontext="proactivemail">
          <a class="education_top_menu ${mode eq 'proactivemail' ? 'selected' : ''}"
             href="?mode=proactivemail" style="font-weight:bold;"><di:translate key="proactivemail.proactivemail" /></a>
        </di:has>
      </mm:hasnode>

      <di:include debug="html"
                  page="/education/wizards/menu_items.jspx" />


      <di:getsetting setting="new_learnobjects" component="core" vartype="list" id="new_learnobjects" write="false" />
      <c:if test="${mm:contains(new_learnobjects, 'tests')}">
        <di:has editcontext="toetsen">
          <mm:link page="." referids="e?">
            <mm:param name="mode">tests</mm:param>
            <a class="education_top_menu ${mode eq 'tests' ? 'selected' : ''}"
               href="${_}">
            <di:translate key="education.educationmenutests" /></a>
          </mm:link>
        </di:has>
      </c:if>



      <di:has editcontext="opleidingen">
        <span class="education_top_menu ${mode eq 'educations' ? 'selected' : ''}">
          <mm:link page="." referids="e?">
            <mm:param name="mode">educations</mm:param>
            <a href="${_}">
              <di:translate key="education.educationmenueducations" />
            </a>
          </mm:link>
          <c:if test="${fn:length(educations) ge 2}">
            <script>
              function chooseEducation(eid) {
              if (eid != 0) {
              document.location.href = "?mode=educations&amp;amp;e=" + eid;
              }
              }
            </script>
            <select name="course" id="course"
                    onchange="chooseEducation(this.value);">
              <option value="0">--------</option>
              <mm:listnodes referid="educations">
                <c:choose>
                  <c:when test="${_node eq e}">
                    <option selected="selected" value="${_node}"><mm:field name="name" /></option>
                  </c:when>
                  <c:otherwise>
                    <option value="${_node}"><mm:field name="name" /></option>
                  </c:otherwise>
                </c:choose>
              </mm:listnodes>
            </select>
          </c:if>
        </span>
      </di:has>
    </mm:cloud>
  </mm:content>
</jsp:root>
