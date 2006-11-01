<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm"%>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<mm:content postprocessor="reducespace">
<mm:cloud method="delegate" jspvar="cloud">

<mm:import externid="question" required="true"/>

<%@include file="/shared/setImports.jsp" %>

<!-- TODO Check for styles -->
<!-- TODO Implement minanswers to be chosen.. JavaScript?! -->

<%-- 
	Multiple choice questions
	type	0: only 1 answer can be selected
		1: multiple answers can be selected
		
	layout	0: all answers beneath eachother / random order
		1: all answers next to eachother / random order
		2: pulldown menu (only for type 0) / random order
		3: all answers beneath eachother / fixed order
		4: all answers next to eachtother / fixed order
		5: pulldown menu (only for type 0) / fixed order
--%>

<mm:node number="$question">

  <mm:field name="showtitle">
    <mm:compare value="1">
      <b><mm:field name="title"/></b><br>
    </mm:compare>
  </mm:field>
  <p/>
  <mm:field name="text" escape="none"/>
  <p/>

  <mm:import id="questiontype"><mm:field name="type"/></mm:import>
  <mm:import id="questionlayout"><mm:field name="layout"/></mm:import>
  <mm:import id="openquestioncount" jspvar="openAnswers"><mm:field name="openanswers"/></mm:import>

  <%-- Show answers in random order --%>
  <mm:compare referid="questionlayout" valueset="0,1,2">
    <mm:import id="order">SHUFFLE</mm:import>
    <mm:relatednodes type="valueanswers" comparator="$order" id="answerlist"/>
  </mm:compare>
	
  <%-- Show answers in fixed order --%>
  <mm:compare referid="questionlayout" valueset="3,4,5">
    <mm:relatednodes type="valueanswers" role="posrel" orderby="posrel.pos" id="answerlist"/>
  </mm:compare>

  <%-- Generate layout for radiogroup / pulldown menu (only 1 correct answer to be chosen) --%>
  <mm:compare referid="questiontype" value="0">

    <%-- Layout for radiogroup --%>
    <mm:compare referid="questionlayout" valueset="0,1,3,4">
      <mm:relatednodes referid="answerlist">
        <input type="radio" name="<mm:write referid="question" />" value="<mm:field name="text"/>"/>
        <mm:field name="text" escape="none"/>

        <%-- Each answer on a new line --%>
        <mm:compare referid="questionlayout" valueset="0,3">
          <br/>
        </mm:compare>
        
      </mm:relatednodes>
    </mm:compare>
		
    <%-- Layout for pulldown menu --%>
    <mm:compare referid="questionlayout" valueset="2,5">
      <mm:relatednodes referid="answerlist">
        <mm:first><select name="<mm:write referid="question"/>"</mm:first>
        <option><mm:field name="text" escape="none"/></option>
        <mm:last></select></mm:last>
      </mm:relatednodes>
    </mm:compare>
		
  </mm:compare>

  <%-- Generate layout for checkboxes (multiple correct answers to be chosen) --%>
  <mm:compare referid="questiontype" value="1">
    <mm:relatednodes referid="answerlist">
      <input type="checkbox" name="<mm:write referid="question"/>_<mm:field name="number"/>" value="<mm:field name="text" escape="none"/>"/>
        <mm:field name="text" escape="none"/>

        <%-- Each answer on a new line --%>
        <mm:compare referid="questionlayout" valueset="0,3">
          <br/>
        </mm:compare>

    </mm:relatednodes>
  </mm:compare>

  <%-- Generate inputfields for openanswers --%>
  <%Integer inputFieldCount = new Integer( openAnswers );
    for ( int i = 0 ; i < inputFieldCount.intValue() ; i++ ) { %>
      <input type="edit" name="openanswer_<mm:write referid="question"/>_<%=i%>"/>
      <p/>
  <%}%>
	
</mm:node>
</mm:cloud>
</mm:content>
