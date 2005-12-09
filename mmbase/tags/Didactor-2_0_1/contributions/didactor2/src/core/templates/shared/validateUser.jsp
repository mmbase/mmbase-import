<%--
  This is an example validation implementation, that will check the following:

  - if the user has a 'systemadministrator', 'teacher' or 'contenteditor' role, he is always allowed to log in
  - if the user is a student, the following check is done:
    - if he is not related to a class and not to an education, access is denied
    - if he is related to a class, the runtime of the class is checked. If the course
      has ended, or not started yet, access is denied

  If you want to use this implementation, you can place a copy of this code
  in your customization directory, and uncomment it
--%>

<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.1" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<mm:content postprocessor="reducespace" expires="0" type="text/html" encoding="UTF-8" escaper="entities">
<mm:cloud jspvar="cloud">
<mm:import externid="provider" />
<mm:import externid="education" />
<mm:import externid="class" />
<mm:import externid="user" />
<%@ include file="globalLang.jsp" %>
<mm:isgreaterthan referid="user" value="0">
<di:hasrole referid="user" role="student">
<di:hasrole referid="user" role="systemadministrator" inverse="true">
<di:hasrole referid="user" role="teacher" inverse="true">
<di:hasrole referid="user" role="contenteditor" inverse="true">
<mm:isnotempty referid="provider">
  <mm:isnotempty referid="education">
    <mm:compare referid="class" value="null">
      <mm:import id="class" reset="true" />
    </mm:compare>
    <mm:isempty referid="class">
      <mm:node number="$user">
        <mm:relatednodescontainer type="educations" role="classrel">
          <mm:size>
            <mm:compare value="0">
              <di:translate key="core.validatelogin_noclass" />
            </mm:compare>
          </mm:size>
        </mm:relatednodescontainer>
      </mm:node>
    </mm:isempty>
    <mm:isnotempty referid="class">
      <mm:node number="$class">
        <mm:relatedcontainer path="mmevents">
          <mm:size write="false">
            <mm:isgreaterthan value="0">
              <% String now = "" + (System.currentTimeMillis() / 1000); %>
              <mm:constraint field="mmevents.start" operator="LESS" value="<%=now%>" />
              <mm:constraint field="mmevents.stop" operator="GREATER" value="<%=now%>" />
              <mm:size write="false">
                <mm:compare value="0">
                  <di:translate key="core.validatelogin_invalid" />
                </mm:compare>
              </mm:size>
            </mm:isgreaterthan>
          </mm:size>
        </mm:relatedcontainer>
      </mm:node>
    </mm:isnotempty>
  </mm:isnotempty>
</mm:isnotempty>
</di:hasrole>
</di:hasrole>
</di:hasrole>
</di:hasrole>
</mm:isgreaterthan>
</mm:cloud>
</mm:content>
