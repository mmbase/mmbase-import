<%@page contentType="application/xml;charset=UTF-8"
%><%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm"
%><%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" 
%><mm:content 
type="application/xml"
postprocessor="reducespace" expires="0">
<mm:cloud rank="didactor user">
  <mm:import externid="testNo" required="true"/>
  <mm:import externid="madetestNo" required="true"/>
  <mm:import externid="userNo" required="true"/>
  <div class="learnenvironment">
    <mm:treeinclude page="/education/tests/totalscore.jsp"  objectlist="$includePath" referids="$referids,madetestNo@madetest,testNo@tests" />
    <mm:treeinclude page="/education/tests/feedback.jsp"    objectlist="$includePath" referids="$referids,testNo@tests,madetestNo@madetest" />
    <hr/>
    <mm:treeinclude page="/education/tests/viewanswers.jsp" objectlist="$includePath" referids="$referids,testNo,madetestNo,userNo" />
  </div>
</mm:cloud>
</mm:content>
