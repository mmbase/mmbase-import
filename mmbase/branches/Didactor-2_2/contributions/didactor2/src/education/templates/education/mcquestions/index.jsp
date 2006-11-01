<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.1" prefix="mm"%>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<mm:content postprocessor="reducespace" expires="0">
<mm:cloud method="delegate" jspvar="cloud">

<mm:import externid="question" required="true"/>
<mm:import externid="madetest">-1</mm:import>

<%@include file="/shared/setImports.jsp" %>

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
   <% String allGivenAnswers = ""; %>
   <mm:relatedcontainer path="givenanswers,madetests">
      <mm:constraint field="madetests.number" value="$madetest"/>
      <mm:related>
         <mm:node element="givenanswers">
            <mm:relatednodes type="answers">
               <mm:first>
                  <mm:field name="number" jspvar="answerNo" vartype="String">
                     <% allGivenAnswers = answerNo; %>
                  </mm:field>
               </mm:first>
               <mm:first inverse="true">
                  <mm:field name="number" jspvar="answerNo" vartype="String">
                     <% allGivenAnswers += "," + answerNo; %>
                  </mm:field>
               </mm:first>
            </mm:relatednodes>
         </mm:node>
      </mm:related>
   </mm:relatedcontainer>

  <mm:field name="showtitle">
    <mm:compare value="1">
      <h2><mm:field name="title"/></h2>
    </mm:compare>
  </mm:field>

  <p/>
  <mm:field name="text" escape="none"/>
  <p/>

  <mm:import id="questiontype"><mm:field name="type"/></mm:import>
  <mm:import id="questionlayout"><mm:field name="layout"/></mm:import>

  <%-- Show answers in random order --%>
  <mm:compare referid="questionlayout" valueset="0,1,2">
    <mm:import id="order">SHUFFLE</mm:import>
    <mm:relatednodes type="mcanswers" comparator="$order" id="answerlist"/>
  </mm:compare>
	
  <%-- Show answers in fixed order --%>
  <mm:compare referid="questionlayout" valueset="3,4,5">
    <mm:relatednodes type="mcanswers" role="posrel" orderby="posrel.pos" id="answerlist"/>
  </mm:compare>

  <%-- Generate layout for radiogroup / pulldown menu (only 1 correct answer to be chosen) --%>
  <mm:compare referid="questiontype" value="0">

    <%-- Layout for radiogroup --%>
    <mm:compare referid="questionlayout" valueset="0,1,3,4">
      <mm:listnodes referid="answerlist">
          <div class="images">
            <mm:relatednodes type="images">
              <mm:field name="showtitle">
                <mm:compare value="1">
                  <h3><mm:field name="title"/></h3>
                </mm:compare>
              </mm:field>
              <img src="<mm:image />" width="200" border="0" /><br/>
              <mm:field name="description" escape="none"/> 
            </mm:relatednodes>
          </div>
          <mm:field name="number" id="answer_id" write="false"/>
        <input type="radio" name="<mm:write referid="question" />" value="<mm:field name="text"/>"
           <mm:compare referid="answer_id" valueset="<%= allGivenAnswers %>">checked</mm:compare>
        />
        <mm:field name="text"/>

        <%-- Each answer on a new line --%>
        <mm:compare referid="questionlayout" valueset="0,3">
          <br/>
        </mm:compare>
        <mm:remove referid="answer_id"/>
      </mm:listnodes>
    </mm:compare>
		
    <%-- Layout for pulldown menu --%>
    <mm:compare referid="questionlayout" valueset="2,5">
      <mm:listnodes referid="answerlist">
        <mm:field name="number" id="answer_id" write="false"/>
        <mm:first><select name="<mm:write referid="question"/>"></mm:first>
         <option <mm:compare referid="answer_id" valueset="<%= allGivenAnswers %>">selected</mm:compare> ><mm:field name="text"/></option>
        <mm:last></select></mm:last>
        <mm:remove referid="answer_id"/>
      </mm:listnodes>
    </mm:compare>
		
  </mm:compare>

  <%-- Generate layout for checkboxes (multiple correct answers to be chosen) --%>
  <mm:compare referid="questiontype" value="1">
    <mm:listnodes referid="answerlist">
      <mm:field name="number" id="answer_id" write="false"/>
      <input type="checkbox" name="<mm:write referid="question"/>_<mm:field name="number"/>" value="<mm:field name="text"/>"
         <mm:compare referid="answer_id" valueset="<%= allGivenAnswers %>">checked</mm:compare>
      />
        <mm:field name="text"/>

        <%-- Each answer on a new line --%>
        <mm:compare referid="questionlayout" valueset="0,3">
          <br/>
        </mm:compare>
        <mm:remove referid="answer_id"/>
    </mm:listnodes>
  </mm:compare>
	
</mm:node>
</mm:cloud>
</mm:content>
