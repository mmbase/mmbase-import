<%--
  This is an example validation implementation, that will check the following:

  - if the user has a 'systemadministrator', 'teacher' or 'contenteditor' role, he is always allowed to log in
  - if the user is a student, the following check is done:
    - if he is not related to a class and not to an education, access is denied
    - if he is related to a class, the runtime of the class is checked. If the course
      has ended, or not started yet, access is denied
  - if the user hasn't any role, access is denied
  - if related to an education, he needs also te be related to a class which is not outdated

  If you want to use this implementation, you can place a copy of this code
  in your customization directory, and uncomment it
--%>

<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<mm:content postprocessor="reducespace" type="text/html" 
            escaper="entities">
<mm:cloud>
  <mm:import externid="provider" />
  <mm:import externid="education" />
  <mm:import externid="class" />
  <mm:import externid="user" vartype="integer" />
  <jsp:directive.include file="globalLang.jsp"  />
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
                  <mm:relatednodescontainer type="educations" role="classrel">
                    <mm:size>
                      <mm:isgreaterthan value="0">
                        <mm:import id="all_ok" reset="true">true</mm:import>
                      </mm:isgreaterthan>
                    </mm:size>
                  </mm:relatednodescontainer>

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
                  <!-- why did we bother at all to determin all_ok ? -->
                  <mm:node number="$user">
                    <mm:relatednodes type="classes" role="classrel">
                      <mm:relatednodescontainer type="mmevents">
                        <mm:time id="now" time="now" write="false" />
                        <mm:constraint field="start" operator="LESS" value="$now" />
                        <mm:constraint field="stop" operator="GREATER" value="$now" />
                        <mm:size>
                          <mm:isgreaterthan value="0">
                            <mm:import id="all_ok" reset="true">true</mm:import>
                          </mm:isgreaterthan>
                        </mm:size>
                      </mm:relatednodescontainer>
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
