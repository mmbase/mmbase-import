<jsp:root
    xmlns:jsp="http://java.sun.com/JSP/Page" version="2.0"
    xmlns:mm="http://www.mmbase.org/mmbase-taglib-2.0"
    xmlns:di="http://www.didactor.nl/ditaglib_1.0"
    xmlns:c="http://java.sun.com/jsp/jstl/core"
    xmlns:fn="http://java.sun.com/jsp/jstl/functions"
    >
  <mm:content type="application/xml" postprocessor="reducespace" expires="0">
    <mm:cloud rank="didactor user">
      <mm:import externid="testNo" required="true"/>
      <mm:import externid="madetestNo" required="true"/>
      <mm:import externid="userNo" required="true"/>
      <div class="learnenvironment">
        <mm:treeinclude debug="xml" page="/education/tests/totalscore.jsp"
                        objectlist="$includePath" referids="$referids,madetestNo@madetest,testNo@tests" />
        <mm:treeinclude debug="xml" page="/education/tests/feedback.jsp"
                        objectlist="$includePath" referids="$referids,testNo@tests,madetestNo@madetest" />
        <hr/>
        <mm:treeinclude debug="xml" page="/education/tests/viewanswers.jsp"
                        objectlist="$includePath" referids="$referids,testNo,madetestNo,userNo" />
      </div>
    </mm:cloud>
  </mm:content>
</jsp:root>
