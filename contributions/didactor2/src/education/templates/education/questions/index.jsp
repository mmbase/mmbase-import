<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.1" prefix="mm"%>

<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>

<mm:content postprocessor="reducespace">

<mm:cloud method="delegate" jspvar="cloud">



<mm:import externid="learnobject" required="true"/>



<!-- TODO Need this page? -->



<%@include file="/shared/setImports.jsp" %>



<mm:node number="$learnobject">

  <h2><mm:field name="name"/></h2>

  <p/>

  <mm:field name="showtitle">
    <mm:compare value="1">
      <mm:field name="title"/>
    </mm:compare>
  </mm:field>

  <mm:field name="text"/>

  <mm:field name="layout"/>

</mm:node>



</mm:cloud>

</mm:content>

