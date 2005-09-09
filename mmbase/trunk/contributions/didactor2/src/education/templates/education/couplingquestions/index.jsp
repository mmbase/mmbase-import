<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.1" prefix="mm"%>

<mm:content postprocessor="reducespace">

<mm:cloud loginpage="/login.jsp" jspvar="cloud">



<mm:import externid="question" required="true"/>



<%@include file="/shared/setImports.jsp" %>



<!-- TODO Check for styles -->



<mm:node number="$question">

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

  <mm:field name="text1" write="true"/> 

  <mm:relatednodes referid="my_shuffled_couplinganswers">



   <mm:field name="text1"/>

    <mm:field id="answer" name="number" write="false"/>

    

    <mm:relatednodes referid="my_couplinganswers">

  

   <mm:first><select name="<mm:write referid="question"/>_<mm:write referid="answer"/>"></mm:first>

        <option><mm:field name="text2"/></option>

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

