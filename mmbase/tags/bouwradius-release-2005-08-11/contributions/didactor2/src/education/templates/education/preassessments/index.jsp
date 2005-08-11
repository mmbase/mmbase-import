<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"%>

<mm:content postprocessor="reducespace">

<mm:cloud loginpage="/login.jsp" jspvar="cloud">



<mm:import externid="question" required="true"/>



<%@include file="/shared/setImports.jsp" %>



<!-- TODO Everything -->



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

