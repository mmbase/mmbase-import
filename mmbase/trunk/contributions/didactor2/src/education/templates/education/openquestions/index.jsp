<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"%>

<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>

<mm:content postprocessor="reducespace">

<mm:cloud loginpage="/login.jsp" jspvar="cloud">



<mm:import externid="question" required="true"/>



<%@include file="/shared/setImports.jsp" %>



<!-- TODO Make the editbox appear small and large -->

<!-- TODO Check for styles -->

<!-- TODO What if layout isn't 0 or 1. Log this situation? -->

<!-- TODO Is there always one open answer given or more? -->



<mm:node number="$question">



  <mm:field name="showtitle">
    <mm:compare value="1">
      <h2><mm:field name="title"/></h2>
    </mm:compare>
  </mm:field>


  <p/>

  <mm:field name="text" escape="none"/>

  <p/>



  <mm:import id="layout"><mm:field name="layout"/></mm:import>



  <%-- Generate large input field --%>

  <mm:compare referid="layout" value="0">

    <input type="text" size="500" name="<mm:write referid="question"/>"/>

    <br/>

  </mm:compare>



  <%-- Generate small input field --%>

  <mm:compare referid="layout" value="1">

    <input type="text" size="100" name="<mm:write referid="question"/>"/>

    <br/>

  </mm:compare>



</mm:node>

</mm:cloud>

</mm:content>

