<jsp:root version="2.0"
          xmlns:jsp="http://java.sun.com/JSP/Page"
          xmlns:mm="http://www.mmbase.org/mmbase-taglib-2.0"
          xmlns:di="http://www.didactor.nl/ditaglib_1.0">

  <!--
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

  WTF is this implemented  _in JSP_.
-->
<mm:content postprocessor="none" type="text/html">
  <mm:cloud method="asis">
    <mm:import externid="provider"  from="request" />
    <mm:import externid="education" from="request" />
    <mm:import externid="class" />
    <mm:import externid="user" vartype="integer" required="true" />

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
            

                <mm:compare referid="all_ok" value="false">
                  <mm:node number="$user">
                    <mm:relatednodes type="classes" role="classrel">
                      <mm:field name="number" />
                      <mm:last inverse="true">, </mm:last>
                    </mm:relatednodes>
                    </mm:node>: <di:translate key="core.validatelogin_invalid" />
                </mm:compare>
              </mm:compare>
            </mm:isnotempty>
          </di:hasrole>
        </di:hasrole>
      </di:hasrole>
    </di:hasrole>



  </mm:cloud>
</mm:content>
</jsp:root>
