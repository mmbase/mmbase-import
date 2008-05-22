<%@include file="includes/abonnee/top.jsp"%>

<mm:content type="text/html" expires="0">
<mm:cloud name="mmbase">

<%-- The name of this page. 
     Used in the sidebar include make an menu-item look active --%>
<mm:import id="active">overegemmail</mm:import>

<mm:import externid="login"/>

<div id="container">

<%@include file="includes/abonnee/banner.jsp"%>

<mm:notpresent referid="login">
<%-- get menu--%>
<mm:include referids="active" page="includes/abonnee/sidebar.jsp"/>
</mm:notpresent>

<%-- get the text --%>
<%@include file="includes/privacytext.jsp"%>

<%-- get footer --%>
<%@include file="includes/abonnee/footer.jsp"%>

</div>
</body>
</html>
</mm:cloud>
</mm:content>
