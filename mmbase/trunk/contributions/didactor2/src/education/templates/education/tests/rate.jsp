<%@page contentType="application/xml;charset=utf8"
%><%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm"
%><%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di"
%><%@page import="java.util.Iterator"
%><mm:content type="application/xml" postprocessor="reducespace" expires="0">
  <mm:cloud rank="didactor user">

    <mm:import externid="learnobject"  required="true"/>
    <mm:import externid="my_questions"  from="session" vartype="list" />

    <mm:import externid="command">next</mm:import>

    <jsp:directive.include file="/education/tests/definitions.jsp" />
    
    <mm:node referid="learnobject" id="my_tests">

      <di:copybook>
        <mm:node id="copybookID">
          <mm:nodefunction id="madetest" name="madetest" referids="my_tests@test">
            <mm:setfield name="testpath"><mm:field name="testpath"><mm:write /><mm:isnotempty>,</mm:isnotempty>${my_questions}</mm:field></mm:setfield>
            <mm:import id="testpath" vartype="list"><mm:field name="testpath" /></mm:import>
          </mm:nodefunction>
        </mm:node>
      </di:copybook>       

      <mm:listnodes referid="my_questions">
        <mm:import id="question">shown<mm:field name="number"/></mm:import>
        <mm:import externid="$question" id="shownquestion" />
          
        <mm:relatednodescontainer path="givenanswers,madetests" element="givenanswers">
          <mm:constraint field="madetests.number" referid="madetest"/>
          <mm:relatednodes>
            <mm:deletenode deleterelations="true"/>
          </mm:relatednodes>
        </mm:relatednodescontainer>

        <mm:import id="ratepage" reset="true">/education/<mm:nodeinfo type="type"/>/rate<mm:nodeinfo type="type"/>.jsp</mm:import>
        <mm:treeinclude page="$ratepage" objectlist="$includePath" referids="$referids,madetest,_node@question" />
      </mm:listnodes>
      <mm:compare referid="command" value="done">
        <!-- If "done" pressed then show the feedback else show next question set -->
        <div>
          <mm:hasnode number="component.progress">
            <script language="javascript">
              reloadProgress();
              reloadEducationTree();
              scrollToTop();
            </script>
          </mm:hasnode>
          <mm:field name="feedbackpage">
            <mm:compare value="0">
              <mm:treeinclude page="/education/tests/totalscore.jsp"  objectlist="$includePath" 
                              referids="$referids,madetest,_node@tests" />          
              <mm:treeinclude page="/education/tests/feedback.jsp" objectlist="$includePath" 
                              referids="$referids,madetest,_node@tests" />
              
            </mm:compare>
      
            <mm:compare value="0" inverse="true">
              <mm:treeinclude page="/education/tests/viewanswersframe.jsp" objectlist="$includePath" referids="$referids,tests@testNo,madetest@madetestNo,user@userNo" />
            </mm:compare>
            
          </mm:field>
        </div>
      </mm:compare>
      <mm:compare referid="command" value="done" inverse="true">
        <mm:import externid="page" required="true" vartype="integer" />
        <mm:treeinclude page="/education/tests/buildtest.jsp"  objectlist="$includePath" referids="$referids,learnobject,madetest">
          <mm:param name="page">${page + 1}</mm:param>
        </mm:treeinclude>
      </mm:compare>
    </mm:node>
  </mm:cloud>
</mm:content>
