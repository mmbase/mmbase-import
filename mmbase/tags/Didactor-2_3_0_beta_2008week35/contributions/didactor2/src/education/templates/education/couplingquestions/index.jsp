<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm"%>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<mm:content postprocessor="reducespace">
<mm:cloud method="delegate" jspvar="cloud">

<mm:import externid="question" required="true"/>
<mm:import externid="madetest">-1</mm:import>

<%@include file="/shared/setImports.jsp" %>

<!-- TODO Check for styles -->

<mm:node number="$question">
  <mm:import id="givenanswer">-1</mm:import>
  <mm:relatedcontainer path="givenanswers,madetests">
    <mm:constraint field="madetests.number" value="$madetest"/>
    <mm:related>
      <mm:node element="givenanswers">
        <mm:import id="givenanswer" reset="true"><mm:field name="number"/></mm:import>
      </mm:node>
    </mm:related>
  </mm:relatedcontainer>

  <mm:field name="showtitle">
    <mm:compare value="1">
      <h2><mm:field name="title"/></h2>
    </mm:compare>
  </mm:field>

  <mm:field name="text" escape="none"/>
  <p/>
  <mm:import id="questionlayout"><mm:field name="layout"/></mm:import>

  <mm:relatednodescontainer type="couplinganswers" >
  <%-- Show answers in random order --%>
  <mm:relatednodes id="my_couplinganswers" />
  <mm:relatednodes id="my_shuffled_couplinganswers" comparator="SHUFFLE"/>

  <%-- Generate for each text1 answer a dropdown with possible text2 answers --%>
  <mm:field name="text" write="true"/> 
  <mm:relatednodes referid="my_shuffled_couplinganswers">

    <mm:field name="text"/>
    <mm:field id="answer" name="number" write="false"/>
    <mm:import id="givencouplinganswer" reset="true">-1</mm:import>
    <mm:relatedcontainer path="leftanswer,givenanswers,rightanswer,couplinganswers">
      <mm:constraint field="givenanswers.number" value="$givenanswer"/>
      <mm:constraint field="leftanswer.pos" field2="rightanswer.pos"/>
      <mm:related>
        <mm:import id="givencouplinganswer" reset="true"><mm:field name="couplinganswers.number"/></mm:import>
      </mm:related>
    </mm:relatedcontainer>

    <mm:relatednodes referid="my_couplinganswers">
      <mm:field id="rightanswer" name="number" write="false"/>
      <mm:first><select name="<mm:write referid="question"/>_<mm:write referid="answer"/>"></mm:first>
        <option <mm:compare referid="givencouplinganswer" referid2="rightanswer">selected</mm:compare>><mm:field name="text2"/></option>
      <mm:last></select></mm:last>
    </mm:relatednodes>
    
    <%-- Each answer on a new line --%>
    <mm:compare referid="questionlayout" valueset="0">
      <br/>
    </mm:compare>
    
    <mm:remove referid="answer"/>

  </mm:relatednodes>
  </mm:relatednodescontainer>
  
</mm:node>
</mm:cloud>
</mm:content>
