<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm" %>

<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>

<%@page import="java.util.*" %>

<mm:content postprocessor="reducespace" expires="0">

<mm:cloud method="delegate" jspvar="cloud">



<%@include file="/shared/setImports.jsp" %>

<%@include file="/education/tests/definitions.jsp" %>

<mm:import externid="studentNo" required="true"/>

<mm:import externid="testNo" required="true"/>
<mm:import externid="madetestNo" required="true"/>


<mm:node number="$studentNo" notfound="skip">

  <% List status = new ArrayList(); 
     int average = 0;
     int nof_questions = 0;
     int nof_questions_answered = 0;
  %>

<mm:node number="$testNo">
  <mm:relatednodes type="questions" role="posrel" orderby="posrel.pos" directions="UP">
    <% nof_questions++;
       boolean noAnswer = true; 
    %>
    <mm:compare referid="madetestNo" value="-1" inverse="true">
      <mm:related path="givenanswers,madetests"  constraints="madetests.number=$madetestNo">
        <% nof_questions_answered++;
           noAnswer = false; 
        %>
        <mm:field name="givenanswers.score" jspvar="answerScore" vartype="String">
          <% if ((new Integer(answerScore)).intValue()==0) {
               average--;
               status.add("-1");
             } else {
               average++;
               status.add("1");
             } 
          %>
        </mm:field>
      </mm:related>
    </mm:compare>
    <% if (noAnswer) status.add("0"); %>
  </mm:relatednodes> <%-- questions --%>
</mm:node> <%-- testNo --%>

  <tr>
    <td><mm:field name="firstname"/> <mm:field name="lastname"/></td>
    <td>
      <% double score= (double)nof_questions_answered / (double)nof_questions;
         if (nof_questions==0) score=0;
      %><%= (int)(score*100) %>%
    </td>
    <td align="center">
      <% if (average>0) { %>+<% }
         if (average<0) { %>-<% }
      %>
    </td>
    <% Iterator statusIterator = status.iterator();

    while (statusIterator.hasNext()) {
      String sStatus = (String) statusIterator.next();
      if (sStatus.equals("1")) { %>
        <td class="td_test_tbs"><img src="<mm:treefile page="/progress/gfx/checked.gif" objectlist="$includePath" referids="$referids"/>" title="Ok" alt="Ok" border="0"></td>
      <% }
      if (sStatus.equals("0")) { %>
        <td class="td_test_tbs"><img src="<mm:treefile page="/progress/gfx/box.gif" objectlist="$includePath" referids="$referids"/>" title="Ok" alt="Ok" border="0"></td>
      <% }
      if (sStatus.equals("-1")) { %>
        <td class="td_test_failed"><img src="<mm:treefile page="/pop/gfx/present.gif" objectlist="$includePath" referids="$referids"/>" title="Ok" alt="Ok" border="0"></td>
      <% } 
    } %>
  </tr>

</mm:node> <%-- student --%>

</mm:cloud>

</mm:content>

