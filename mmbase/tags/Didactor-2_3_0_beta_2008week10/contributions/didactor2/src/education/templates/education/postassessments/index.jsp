<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm"%>

<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>

<mm:content postprocessor="reducespace">

<mm:cloud method="delegate" jspvar="cloud">



<mm:import externid="question" required="true"/>



<%@include file="/shared/setImports.jsp" %>



<mm:node number="$learnobject">



  <mm:field name="showtitle">
    <mm:compare value="1">
      <h1><mm:field name="title"/></h1>
    </mm:compare>
  </mm:field>

  <p/>

  <mm:field name="text"/>

  <p/>





</mm:node>

</mm:cloud>

</mm:content>

