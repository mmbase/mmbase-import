<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.1" prefix="mm"%>

<mm:content postprocessor="reducespace">

<mm:cloud loginpage="/login.jsp" jspvar="cloud">



<mm:import externid="learnobject" required="true"/>



<!-- TODO Need this page? -->



<%@include file="/shared/setImports.jsp" %>



<mm:node number="$learnobject">

    <mm:field name="showtitle">
      <mm:compare value="1">
        <h2><mm:field name="name"/></h2>
      </mm:compare>
    </mm:field>

	<mm:field name="intro"/>

	<p/>

	<mm:nodeinfo type="type"/>

<%--	<mm:include page="<mm:nodeinfo type="type"/>.jsp"/>	--%>

 	<a href="<mm:nodeinfo type="type"/>.jsp">Test</a>

</mm:node>



</mm:cloud>

</mm:content>

