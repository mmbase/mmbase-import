<%--
  This is an example validation implementation, that will check the following:

  - if the user has a 'systemadministrator', 'teacher' or 'contenteditor' role, he is always allowed to log in
  - if the user is a student, the following check is done:
    - if he is not related to a class and not to an education, access is denied
    - if he is related to a class, the runtime of the class is checked. If the course
      has ended, or not started yet, access is denied
  - if the user hasn't any role, access is denied

  If you want to use this implementation, you can place a copy of this code
  in your customization directory, and uncomment it
--%>

<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.1" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<mm:content postprocessor="reducespace" expires="0" type="text/html" encoding="UTF-8" escaper="entities">
<mm:cloud jspvar="cloud" method="delegate" authenticate="asis">
  <mm:import externid="provider" />
  <mm:import externid="education" />
  <mm:import externid="class" />
  <mm:import externid="user" />
  <%@ include file="globalLang.jsp" %>
  <mm:isgreaterthan referid="user" value="0">
    <mm:remove referid="userdisabled"/>


    <mm:node number="$user">
      <mm:field name="person_status">
        <mm:compare value="0">
          <mm:import id="userdisabled">true</mm:import>
          <di:translate key="core.accountdisabled" />
        </mm:compare>
      </mm:field>
    </mm:node>


    <mm:notpresent referid="userdisabled">
      <di:hasrole referid="user" role="student">
        <di:hasrole referid="user" role="systemadministrator" inverse="true">
          <di:hasrole referid="user" role="teacher" inverse="true">
            <di:hasrole referid="user" role="contenteditor" inverse="true">

              <mm:isnotempty referid="provider">
                    <mm:node number="$user">
                      <mm:import id="all_ok" reset="true">false</mm:import>
                      <mm:relatednodes type="educations" role="classrel">
                        <mm:size>
                          <mm:isgreaterthan value="0">
                            <mm:import id="all_ok" reset="true">true</mm:import>
                          </mm:isgreaterthan>
                        </mm:size>
                      </mm:relatednodes>

                      <mm:relatednodes type="classes" role="classrel">
                        <mm:size>
                          <mm:isgreaterthan value="0">
                            <mm:import id="all_ok" reset="true">true</mm:import>
                          </mm:isgreaterthan>
                        </mm:size>
                      </mm:relatednodes>
                    </mm:node>

                    <mm:compare referid="all_ok" value="false">
                       <di:translate key="core.validatelogin_noclass" />
                    </mm:compare>



                    <mm:compare referid="all_ok" value="true">
                      <mm:import id="all_ok" reset="true">false</mm:import>
                      <mm:node number="$user">
                        <mm:relatednodes type="classes" role="classrel">
                          <mm:relatedcontainer path="mmevents">
                            <mm:size write="false">
                              <mm:isgreaterthan value="0">
                                <% String now = "" + (System.currentTimeMillis() / 1000); %>
                                <mm:constraint field="mmevents.start" operator="LESS" value="<%=now%>" />
                                <mm:constraint field="mmevents.stop" operator="GREATER" value="<%=now%>" />
                                <mm:size write="false">
                                  <mm:isgreaterthan value="0">
                                    <mm:import id="all_ok" reset="true">true</mm:import>
                                  </mm:isgreaterthan>
                                </mm:size>
                              </mm:isgreaterthan>
                            </mm:size>
                          </mm:relatedcontainer>
                        </mm:relatednodes>
                      </mm:node>

                      <mm:compare referid="all_ok" value="false">
                        <di:translate key="core.validatelogin_invalid" />
                      </mm:compare>
                    </mm:compare>

              </mm:isnotempty>
            </di:hasrole>
          </di:hasrole>
        </di:hasrole>
      </di:hasrole>



      <mm:node number="$user">
        <mm:relatednodescontainer type="roles">
          <mm:size>
            <mm:compare value="0">
              <di:translate key="core.validatelogin_norole" />
            </mm:compare>
          </mm:size>
        </mm:relatednodescontainer>
      </mm:node>



    </mm:notpresent>
    <mm:remove referid="userdisabled"/>
  </mm:isgreaterthan>
</mm:cloud>
</mm:content>
