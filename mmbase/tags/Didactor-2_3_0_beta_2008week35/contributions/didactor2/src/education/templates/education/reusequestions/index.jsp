<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm"%>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>

<mm:content postprocessor="reducespace">

<mm:cloud method="delegate" jspvar="cloud">



<mm:import externid="question" required="true"/>



<%@include file="/shared/setImports.jsp" %>



<!-- TODO Check for styles -->

<!-- TODO What to do if there isn't yet a answered value question. Nothing. -->

<!-- TODO Implement layout. -->



<mm:node number="$question">



  <mm:field name="showtitle">
    <mm:compare value="1">
      <h2><mm:field name="title"/></h2>
    </mm:compare>
  </mm:field>

  <p/>

  <mm:field name="text" escape="none"/>

  <p/>



  <mm:import id="questionno"><mm:field name="number"/></mm:import>

  <mm:import id="questionlayout"><mm:field name="layout"/></mm:import>



  <%-- Determine the values where you have to give answers --%>

  <mm:relatednodes type="valueanswers" id="my_valueanswers"/>



  <%-- Select the copybook of de current user and his class --%>

  <mm:node number="$user">



    <mm:relatedcontainer path="classrel,classes">

      <mm:constraint field="classes.number" value="$class"/>

      <mm:related path="classrel,classes">



      <mm:node element="classrel">



        <%-- Select at random one value question --%>

        <mm:related path="copybooks,madetests,givenanswers,valuequestions" comparator="SHUFFLE" max="1">



          <mm:node element="givenanswers">



            <mm:first><input type="hidden" name="<mm:nodeinfo type="type"/>" value="<mm:field name="number"/>"></input></mm:first>



            <!-- TODO layout here... -->



            <%-- There are 2 types of answers openanswers and valueanswers --%>

            <mm:relatednodes type="answers" comparator="SHUFFLE">



              <mm:import id="answerno"><mm:field name="number"/></mm:import>

              <mm:index/> <mm:field name="text"/>

              <mm:relatednodes referid="my_valueanswers">

                <mm:first><select name="answer<mm:write referid="questionno"/>_<mm:write referid="answerno"/>"></mm:first>

                <option><mm:field name="text"/></option>

                <mm:last></select><br/></mm:last>

              </mm:relatednodes>

              <mm:remove referid="answerno"/>



            </mm:relatednodes>



          </mm:node>



        </mm:related>

      </mm:node>



      </mm:related>

    </mm:relatedcontainer>

  </mm:node>





</mm:node>

</mm:cloud>

</mm:content>

