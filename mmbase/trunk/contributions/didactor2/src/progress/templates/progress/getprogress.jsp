<jsp:root version="2.0"
          xmlns:c="http://java.sun.com/jsp/jstl/core"
          xmlns:jsp="http://java.sun.com/JSP/Page"
          xmlns:mm="http://www.mmbase.org/mmbase-taglib-2.0"
          xmlns:di="http://www.didactor.nl/ditaglib_1.0"
          xmlns:os="http://www.opensymphony.com/oscache"
          >
  <mm:content type="text/plain"
              postprocessor="none">
    <mm:cloud
        rank="didactor user">
      <jsp:directive.include file="/education/tests/definitions.jsp" />


      <!-- report either the current user's progress, or the one given by "student" argument -->
      <mm:import externid="student" id="student"><mm:write referid="user"/></mm:import>


      <mm:node number="$student" notfound="skip">
        <mm:context>
          <di:copybook student="${student}">
            <mm:node id="copybookNo" />
          </di:copybook>

          <mm:present referid="copybookNo">
            <os:cache time="${empty copybookNo ? 600 : 0}" key="progress-${education}-${student}-${copybookNo}">
              <!-- performance of this is very bad if no copybook DIDACTOR-50
                   So caching it some time, to increase responsiveness.
              -->

              <jsp:scriptlet>
                int nof_tests= 0;
                int nof_tests_passed= 0;
              </jsp:scriptlet>

              <mm:node number="$education" notfound="skip">
                <mm:import id="previousnumber"><mm:field name="number"/></mm:import>

                <mm:relatednodescontainer type="learnobjects" role="posrel">
                  <mm:sortorder field="posrel.pos" direction="up"/>

                  <mm:tree type="learnobjects" role="posrel" searchdir="destination" orderby="posrel.pos" directions="up">
                    <mm:nodeinfo type="type">
                      <mm:compare value="tests">
                        <jsp:scriptlet>nof_tests++;</jsp:scriptlet>
                        <mm:import id="testNo" reset="true"><mm:field name="number"/></mm:import>

                        <jsp:directive.include file="teststatus.jspx" />

                        <mm:compare referid="teststatus" value="passed">
                          <jsp:scriptlet>nof_tests_passed++;</jsp:scriptlet>
                        </mm:compare>
                      </mm:compare>
                    </mm:nodeinfo>
                  </mm:tree>
                </mm:relatednodescontainer>

                <jsp:expression>nof_tests > 0 ? (double)nof_tests_passed / (double)nof_tests : 0</jsp:expression>

              </mm:node>
            </os:cache>
          </mm:present>
          <mm:notpresent referid="copybookNo">0</mm:notpresent>

        </mm:context>
      </mm:node>

    </mm:cloud>
  </mm:content>
</jsp:root>


